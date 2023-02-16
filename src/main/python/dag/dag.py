"""
WebLog GCS to BigQuery to VertexAi

## Authors

xiexiaofeng0226@gmail.com

## Runtime

composer:2.1.6
Airflow:2.4.3
python: 3.8

## Usage

１、Spark jobをsubmit
２，VertexAI training jobをsubmit
３、slack通知

"""

from airflow import DAG
from airflow.models import Variable
from airflow.decorators import task
from airflow.operators.dummy_operator import DummyOperator
from airflow.providers.google.cloud.operators.dataproc import (
    DataprocSubmitJobOperator,
)
from airflow.providers.google.cloud.operators.vertex_ai.custom_job import (
    CreateCustomContainerTrainingJobOperator,
)
from dag_base import DagBase
import slackweb
from airflow.hooks.base import BaseHook


class dag(DagBase):
    def __init__(self):
        super().__init__(file_name=__file__,
                         doc_md=__doc__,
                         schedule_interval="0 0 * * *")  # 毎日0時で処理開始

    def generate_tasks(self, dag: DAG):
        # start signal
        start = DummyOperator(task_id="start_dag")
        # spark job
        spark_scala_job = {
            "reference": {"project_id": PROJECT_ID},
            "placement": {"cluster_name": CLUSTER_NAME},
            "spark_job": {
                "jar_file_uris": [jars],
                "main_class": main_class,
            },
        }
        spark_task = DataprocSubmitJobOperator(
            task_id="spark_task", job=spark_scala_job, region=REGION, project_id=PROJECT_ID
        )
        # slack notify
        @task()
        def notify(title, text, color):
            slack = slackweb.Slack(url=BaseHook.get_connection(conn_id="slack_webhook").host)
            attachments = [{"title": title,
                            "text": text,
                            "color": color,  # good, warning, danger
                            "footer": "Send from Python",
                            }]
            slack.notify(text=None, attachments=attachments)
        # vertexAI training job
        create_custom_container_training_job = CreateCustomContainerTrainingJobOperator(
            task_id="custom_container_task",
            staging_bucket=f"gs://{CUSTOM_CONTAINER_GCS_BUCKET_NAME}",
            display_name="prophet-model-train",
            container_uri=CUSTOM_CONTAINER_URI,
            service_account=service_account,
            region=REGION,
            project_id=PROJECT_ID,
        )

        notify_slack_start = notify("start job", "pipeline start, send job to dataproc", "good")
        notify_slack_train = notify("training job", "customer model training start ", "good")
        notify_slack_over = notify("trainning over", "train is over, go gcs to see result", "good")

        start >> notify_slack_start >> spark_task >> notify_slack_train >> create_custom_container_training_job >> notify_slack_over


# get params from airflow variables
gcp_config = Variable.get("config", deserialize_json=True)
PROJECT_ID = gcp_config["PROJECT_ID"]
CLUSTER_NAME = gcp_config["CLUSTER_NAME"]
REGION = gcp_config["REGION"]
main_class = gcp_config["main_class"]
jars = gcp_config["jars"]
service_account = gcp_config["service_account"]
CUSTOM_CONTAINER_GCS_BUCKET_NAME = gcp_config["CUSTOM_CONTAINER_GCS_BUCKET_NAME"]
CUSTOM_CONTAINER_URI = gcp_config["CUSTOM_CONTAINER_URI"]

# dag run
dag = dag()
dg = dag.generate_dag()
