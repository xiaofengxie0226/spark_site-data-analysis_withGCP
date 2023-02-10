#!/usr/bin/env python
# coding: utf-8

#モデル学習.py

from google.cloud import bigquery
from fbprophet import Prophet

#bigqueryからデータを読み込む
client = bigquery.Client(project="sinkcapital-001")
query1= """
SELECT
remoteIP,os,Access_time
FROM
  `sinkcapital-001.web_log.UserLog`
LIMIT
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

#学習・予測データ分け
li = 2*round(len(df)/3)
x_train = df[:li]
x_test = df[li:]

# モデル学習
proph = Prophet()
proph.fit(x_train)
# 未来予測用のデータフレーム
# future = proph.make_future_dataframe(periods=1,freq='T')
# 時系列を予測
forecast = proph.predict(x_test)

#save to gcs
bucket_name = "vertex-ai-save-forcast-us-central1"
forecast.to_csv(f'gs://{bucket_name}/forcast.csv')