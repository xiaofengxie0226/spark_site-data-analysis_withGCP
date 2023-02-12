#weblog storage bucket
resource "google_storage_bucket" "bucket" {
  name               = "user-log-data-us-central1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
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
resource "google_storage_bucket" "bucket" {
  name               = "dataproc-to-bigquery-us-central1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}
#dataproc initialize
resource "google_storage_bucket" "bucket" {
  name               = "sinkcapital-spark-dependencies-us-central1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}
#vertexAI stage bucket
resource "google_storage_bucket" "bucket" {
  name               = "vertex-ai-stage-us-central1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}
#vertexAI save bucket
resource "google_storage_bucket" "bucket" {
  name               = "vertex-ai-save-forcast-us-central1"
  project            = var.project_id
  location           = var.location
  force_destroy      = var.force_destroy
  versioning {
    enabled = var.versioning
  }
}