resource "aws_ecr_repository" "webflux_app" {
  name                 = "franquicias-webflux-app"
  image_tag_mutability = "MUTABLE"
  
  force_delete         = true 
}