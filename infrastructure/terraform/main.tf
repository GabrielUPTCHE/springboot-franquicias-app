# main.tf
terraform {
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "~> 1.15.0"
    }
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# Proveedor de MongoDB Atlas
provider "mongodbatlas" {
  public_key  = var.public_key
  private_key = var.private_key
}

# Proveedor de AWS
provider "aws" {
  region = "us-east-1"
  default_tags {
    tags = {
      Project     = "Franquicias-WebFlux"
      Environment = "Dev"
      ManagedBy   = "Terraform"
    }
  }
}

# modulos de AWS


module "networking" {
  source = "./modules/networking"
}

module "security" {
  source = "./modules/security"
  vpc_id = module.networking.vpc_id
}

module "load_balancer" {
  source = "./modules/load_balancer"

  vpc_id         = module.networking.vpc_id
  public_subnets = module.networking.public_subnets
  alb_sg_id      = module.security.alb_sg_id
}

output "url_aplicacion" {
  value = module.load_balancer.alb_dns_name
}

module "ecr" {
  source = "./modules/ecr"
}

output "ecr_repository_url" {
  value = module.ecr.repository_url
}


module "compute" {
  source = "./modules/compute"

  public_subnets     = module.networking.public_subnets
  fargate_sg_id      = module.security.fargate_sg_id
  target_group_arn   = module.load_balancer.target_group_arn
  execution_role_arn = module.security.execution_role_arn
  log_group_name     = module.security.log_group_name

  image_url = "${module.ecr.repository_url}:latest"
}