resource "google_bigquery_dataset" "dataset" {
  project                     = var.project_id
  dataset_id                  = var.dataset_id
  location                    = var.location
}


resource "google_bigquery_table" "UserInfo" {
  dataset_id = var.dataset_id
  table_id   = "UserInfo"
  depends_on = [
    google_bigquery_dataset.dataset
  ]
  deletion_protection=var.deletion_protection
}

resource "google_bigquery_table" "UserLog" {
  dataset_id = var.dataset_id
  table_id   = "UserLog"
  depends_on = [
    google_bigquery_dataset.dataset
  ]
  deletion_protection=var.deletion_protection
}

resource "google_bigquery_table" "useragent_os_info" {
  dataset_id = var.dataset_id
  table_id   = "useragent_os_info"
  depends_on = [
    google_bigquery_dataset.dataset
  ]
  deletion_protection=var.deletion_protection

  schema = <<EOF
[
  {
    "name": "userAgent",
    "type": "STRING",
    "mode": "NULLABLE"
  },
  {
    "name": "os",
    "type": "STRING",
    "mode": "NULLABLE"
  }
]
EOF
}