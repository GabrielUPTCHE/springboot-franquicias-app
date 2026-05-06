variable "public_subnets" {
  description = "Subredes donde vivirán los contenedores"
  type        = list(string)
}

variable "fargate_sg_id" {
  description = "Security Group de Fargate"
  type        = string
}

variable "target_group_arn" {
  description = "ARN del Target Group del ALB"
  type        = string
}

variable "execution_role_arn" {
  description = "Rol de IAM para ejecutar la tarea"
  type        = string
}

variable "image_url" {
  description = "URL exacta de la imagen en ECR"
  type        = string
}

variable "log_group_name" {
  description = "Nombre del Log Group en CloudWatch"
  type        = string
}