provider "google" {
  project = "sinkcapital-001"
}

resource "google_artifact_registry_repository" "my-repo" {
  location      = "us-central1"
  repository_id = "model"
  description   = "vertexAI model docker repository"
  format        = "DOCKER"
}

resource "google_dataproc_cluster" "mycluster" {
  name     = "spark-scala-job"
  region   = "us-central1"

  cluster_config {

    master_config {
      num_instances = 1
      machine_type  = "n1-standard-4"
      disk_config {
        boot_disk_type    = "pd-ssd"
        boot_disk_size_gb = 30
      }
    }

    worker_config {
      num_instances    = 2
      machine_type     = "n1-standard-4"
      disk_config {
        boot_disk_size_gb = 50
      }
    }

    # Override or set some custom properties
    software_config {
      image_version = "2.1.2-debian11"
    }

    # You can define multiple initialization_action blocks
    initialization_action {
      script      = "gs://sinkcapital-spark-dependencies-us-central1/connectors.sh"
      timeout_sec = 500
    }
    gce_cluster_config {
      metadata = {
        bigquery-connector-version="1.2.0"
        spark-bigquery-connector-version = "0.21.0"
      }
    }
  }
}

resource "google_composer_environment" "composerV2" {
  name   = "sinkcapital-001-us-central1-composerv2"
  region = "us-central1"
  config {
    environment_size = "ENVIRONMENT_SIZE_SMALL"
    software_config {
      image_version = "composer-2-airflow-2"
      pypi_packages = {
        slackweb = ""
      }
    }
  }
}