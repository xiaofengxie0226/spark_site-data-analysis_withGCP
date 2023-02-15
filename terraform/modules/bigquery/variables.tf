variable "project_id" {
  description = "プロジェクトの ID"
  type        = string
  default     = "sinkcapital-002"
}
variable "location" {
  description = "データセット＆テーブルの場所"
  type        = string
  default     = "us-east1"
}
variable "dataset_id" {
  type        = string
  default     = "web_log"
}
variable "deletion_protection" {
  description = "true に設定すると、バージョニングがこのバケットで完全に有効になる"
  type        = bool
  default     = false
}