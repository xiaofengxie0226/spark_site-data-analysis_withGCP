variable "project_id" {
  description = "プロジェクトの ID"
  type        = string
  default     = "sinkcapital-001"
}
variable "location" {
  description = "データセット＆テーブルの場所"
  type        = string
  default     = "us-central1"
}
variable "dataset_id" {
  type        = string
  default     = "web_log_test"
}
variable "deletion_protection" {
  description = "true に設定すると、バージョニングがこのバケットで完全に有効になる"
  type        = bool
  default     = false
}