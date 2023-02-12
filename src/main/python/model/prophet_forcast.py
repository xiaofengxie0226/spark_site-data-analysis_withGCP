#!/usr/bin/env python
# coding: utf-8

#pip install fbprophet
#pip install pystan==2.19

from google.cloud import bigquery
from google.cloud import storage
from fbprophet import Prophet
import pickle

#bigqueryからデータを読み込む
client = bigquery.Client(project="sinkcapital-001")
query1= """
SELECT
remoteIP,os,Access_time
FROM
  `sinkcapital-001.web_log.UserLog`
limit
  100000
"""
result = client.query(query1)
df = result.to_dataframe()

#整形
df["user-id"] = df["remoteIP"] +"-"+ df["os"]
df = df[["user-id","Access_time"]]
df['Access_time'] = df['Access_time'].dt.round("T")
df = df.groupby("Access_time").count().reset_index().sort_values("Access_time")
df = df.rename(columns={'Access_time': 'ds', 'user-id': 'y'})
df["ds"] = df["ds"].dt.tz_localize(None)

# モデル学習
proph = Prophet()
model = proph.fit(df)

#model save to gcs
bucket_name="vertex-ai-save-forcast-us-central1"
storage_client=storage.Client(project="sinkcapital-001")
bucket=storage_client.bucket(bucket_name)
blob=bucket.blob("prophet_model.pkl")
with blob.open ("wb") as f:
    pickle.dump(model,f)