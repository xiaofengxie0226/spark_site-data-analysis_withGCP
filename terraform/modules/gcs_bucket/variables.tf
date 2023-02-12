variable "name" {
  description = "作成するバケットの名前"
  type        = string
  default     = "一意のバケット名前"
}
variable "project_id" {
  description = "バケットを作成するプロジェクトの ID"
  type        = string
  default     = "sinkcapital-001"
}
variable "location" {
  description = "バケットの場所"
  type        = string
  default     = "us-central1"
}
variable "bucket_policy_only" {
  description = "バケットに対するバケット ポリシーのみのアクセスを有効にする"
  type        = bool
  default     = true
}
variable "versioning" {
  description = "true に設定すると、バージョニングがこのバケットで完全に有効になる"
  type        = bool
  default     = false
}
variable "force_destroy" {
  description = "バケットを削除する際、含まれているすべてのオブジェクトが削除されるかどうかがこのブール値オプションで決まる。false の場合、Terraform はオブジェクトが含まれているバケットを削除できない。"
  type        = bool
  default     = true
}