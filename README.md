# Spark site-data-analysis inGCP
The project that use spark to ETL log data

## Runtime
Scala: 2.12.15<br>
Java: JDK8<br>
python:3.7

## License
Apache License 2.0

# 事前準備：<br>
1,各APIに有効にする<br>
2,権限周りの設定が整っている<br>

## Architecture
https://www.figma.com/file/ZPMt0a5aegkc5jJJg1HdIq/Archtecture?node-id=1%3A155&t=ZHxDlbY1mOKiTGoK-1

## Index
0,terraform<br>
1,dummy data create<br>
2,spark job<br>
3,VextexAI + docker<br>
4,apacheAirflow<br>




### GCP config
PROJECT_ID=sinkcapital-001 #your gcp project_id<br>
CLUSTER_NAME=spark-scala-job<br>
REGION=us-central1



### dataproc to connectBQ
```shell
gsutil cp gs://goog-dataproc-initialization-actions-us-central1/connectors/connectors.sh gs://sinkcapital-spark-dependencies-us-central1/
```

### dataproc cluster create
```shell
gcloud dataproc clusters create spark-scala-job-us-west1 \
    --region us-west1 \
    --initialization-actions gs://sinkcapital-spark-dependencies-us-east1/connectors.sh \
	--num-workers=2 \
	--worker-machine-type n1-standard-2 \
	--master-boot-disk-size=50GB \
	--worker-boot-disk-size=50GB \
	--metadata bigquery-connector-version=1.2.0 \
    --metadata spark-bigquery-connector-version=0.21.0
```

```shell
gcloud dataproc clusters start spark-scala-job --region=us-central1
```

### dataproc sparkjob run --dummydata create
```shell
gcloud dataproc jobs submit spark \
    --cluster=spark-scala-job \
    --class=util.mockData \
    --jars=gs://sinkcapital-spark-dependencies-us-central1/spark_site-data-analysis_inGCP-1.0-SNAPSHOT.jar \
    --region=us-central1
```

### dataproc sparkjob run --spark job
```shell
gcloud dataproc jobs submit spark \
    --cluster=spark-scala-job-us-west1 \
    --class=com.sparkETL \
    --jars=gs://sinkcapital-spark-dependencies-us-east1/spark_site-data-analysis_inGCP-1.0-SNAPSHOT.jar \
    --region=us-west1
```

### requirements.txt
```shell
pip list --format=freeze >> requirements.txt
```

### Docker
```shell
gcloud builds submit --tag us-central1-docker.pkg.dev/sinkcapital-001/model/prophet .
```

