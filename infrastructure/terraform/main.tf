terraform {
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "~> 1.15.0"
    }
  }
}

variable "public_key" {}
variable "private_key" {}
variable "org_id" {}

provider "mongodbatlas" {
  public_key  = var.public_key
  private_key = var.private_key
}

resource "mongodbatlas_project" "franquicias_project" {
  name   = "Franquicias-Project"
  org_id = var.org_id
}

resource "mongodbatlas_advanced_cluster" "franquicias_cluster" {
  project_id   = mongodbatlas_project.franquicias_project.id
  name         = "FranquiciasCluster"
  cluster_type = "REPLICASET"

  replication_specs {
    region_configs {
      electable_specs {
        instance_size = "M0" # La capa gratuita
      }
      provider_name         = "TENANT"
      backing_provider_name = "AWS" # Lo alojaremos en AWS
      region_name           = "US_EAST_1"
      priority              = 7
    }
  }
}

resource "mongodbatlas_database_user" "db_user" {
  username           = "admin_franquicias"
  password           = "SuperSecretPassword123" # En un entorno real, esto también sería una variable
  project_id         = mongodbatlas_project.franquicias_project.id
  auth_database_name = "admin"
  roles {
    role_name     = "readWriteAnyDatabase"
    database_name = "admin"
  }
}

resource "mongodbatlas_project_ip_access_list" "allow_all" {
  project_id = mongodbatlas_project.franquicias_project.id
  cidr_block = "0.0.0.0/0"
  comment    = "Permitir acceso desde cualquier lugar para pruebas"
}

output "mongodb_uri" {
  value = mongodbatlas_advanced_cluster.franquicias_cluster.connection_strings[0].standard_srv
}