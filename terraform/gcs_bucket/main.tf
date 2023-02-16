#weblog storage bucket
resource "google_storage_bucket" "user-log" {
  name               = "user-log-data-us-east1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
  #delete object after 180 days/6 month
  lifecycle_rule {
    condition {
      age = 180
    }
    action {
      type = "Delete"
    }
  }
}
#spark to bigquery temp bucket
resource "google_storage_bucket" "dataproc-to-bigquery" {
  name               = "dataproc-to-bigquery-us-east1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}
#dataproc initialize&.jar upload
resource "google_storage_bucket" "sinkcapital-spark-dependencies" {
  name               = "sinkcapital-spark-dependencies-us-east1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}
#vertexAI stage bucket
resource "google_storage_bucket" "vertex-ai-stage" {
  name               = "vertex-ai-stage-us-east1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}
#vertexAI save bucket
resource "google_storage_bucket" "vertex-ai-save" {
  name               = "vertex-ai-save-forcast-us-east1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}