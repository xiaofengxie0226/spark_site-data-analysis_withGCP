module "gcs-bucket" {
  source = "./modules/gcs_bucket"
}

module "bigquery" {
  source = "./modules/bigquery"
}

module "compute" {
  #   depends_on = [module.gcs-bucket]
  source = "./modules/compute"
}