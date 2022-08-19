# spark_site-data-analysis_inGCP

spark site-data/user-action analysis -GCP Version

IDE: Intellij IEAD<br>
version control: Github<br>
cloud platform: GCP<br>

spark: <br>
sparkCore sparkSQL sparkStreaming

needs<br>
1, user session analysis<br>
2, CVR<br>
3, region heat production map<br>
4, paylist analysis realtime<br>

workflow<br>
Simulation data generation-batch -> GCS/DB -> batch dealing -> Bigquery<br>
Simulation data generation-streaming -> pubsub -> streaming dealing -> Bigquery
