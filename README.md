# Spark site-data-analysis inGCP
The project that use spark to ETL log data

## Runtime
Scala: 2.12.15<br>
Java: JDK8

## License
Apache License 2.0

# 事前準備：<br>
1,各APIに有効にする<br>
2,権限周りの設定が整っている<br>
3,必要なGCSバケット＆Bigqueryデータセット作成済み<br>

## Architecture
https://www.figma.com/file/ZPMt0a5aegkc5jJJg1HdIq/Archtecture?node-id=1%3A155&t=ZHxDlbY1mOKiTGoK-1

## Index
1,dummydata create<br>
2,spark job<br>
3,cloudFunctions<br>
4,apacheAirflow<br>
5,Looker<br>
6,VextexAI



### GCP config
PROJECT_ID=sinkcapital-001<br>
CLUSTER_NAME=spark-scala-job<br>
REGION=us-central1



### dataproc to connectBQ
```shell
gsutil cp gs://goog-dataproc-initialization-actions-us-central1/connectors/connectors.sh gs://sinkcapital-spark-dependencies-us-central1/
```

### dataproc cluster create
```shell
gcloud dataproc clusters create spark-scala-job \
    --region us-central1 \
    --initialization-actions gs://sinkcapital-spark-dependencies-us-central1/connectors.sh \
	--num-workers=2 \
	--master-boot-disk-size=100GB \
	--worker-boot-disk-size=100GB \
	--metadata bigquery-connector-version=1.2.0 \
    --metadata spark-bigquery-connector-version=0.21.0
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
    --cluster=spark-scala-job \
    --class=com.sparkETL \
    --jars=gs://sinkcapital-spark-dependencies-us-central1/spark_site-data-analysis_inGCP-1.0-SNAPSHOT.jar \
    --region=us-central1
```

