output "alb_sg_id" {
  value = aws_security_group.alb_sg.id
}

output "fargate_sg_id" {
  value = aws_security_group.fargate_sg.id
}

output "execution_role_arn" {
  value = aws_iam_role.ecs_execution_role.arn
}

output "log_group_name" {
  value = aws_cloudwatch_log_group.fargate_logs.name
}