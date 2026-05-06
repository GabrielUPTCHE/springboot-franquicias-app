output "target_group_arn" {
  value = aws_lb_target_group.fargate_tg.arn
}

output "alb_dns_name" {
  value       = aws_lb.main.dns_name
  description = "La URL publica para acceder a la aplicacion"
}