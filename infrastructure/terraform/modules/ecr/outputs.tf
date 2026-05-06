output "repository_url" {
  value       = aws_ecr_repository.webflux_app.repository_url
  description = "URL del repositorio para hacer el docker push"
}