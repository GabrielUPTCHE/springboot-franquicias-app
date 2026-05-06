# ECS CLUSTER ---
resource "aws_ecs_cluster" "main" {
  name = "franquicias-cluster"
}

# TASK DEFINITION
resource "aws_ecs_task_definition" "app" {
  family                   = "franquicias-webflux-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = var.execution_role_arn

  container_definitions = jsonencode([
    {
      name      = "webflux-container"
      image     = var.image_url
      essential = true
      
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]
      
      # Integramos los logs con CloudWatch para no perder la trazabilidad reactiva
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = var.log_group_name
          "awslogs-region"        = "us-east-1"
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

#  ECS SERVICE 
resource "aws_ecs_service" "app_service" {
  name            = "franquicias-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = 2 
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.public_subnets
    security_groups  = [var.fargate_sg_id]
    # Debe ser true para que los contenedores puedan jalar la imagen desde ECR por internet sin un NAT Gateway
    assign_public_ip = true 
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = "webflux-container"
    container_port    = 8080
  }
}