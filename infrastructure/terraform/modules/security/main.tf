# CLOUDWATCH LOGS 
resource "aws_cloudwatch_log_group" "fargate_logs" {
  name              = "/ecs/franquicias-webflux"
  retention_in_days = 7
}

# SECURITY GROUPS

resource "aws_security_group" "alb_sg" {
  name        = "franquicias-alb-sg"
  description = "Permite trafico HTTP desde Internet al Balanceador"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# SG para Fargate
resource "aws_security_group" "fargate_sg" {
  name        = "franquicias-fargate-sg"
  description = "Permite trafico SOLO desde el ALB"
  vpc_id      = var.vpc_id

  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"

    security_groups = [aws_security_group.alb_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# IAM ROLES
# Task Execution Role
resource "aws_iam_role" "ecs_execution_role" {
  name = "franquicias_ecs_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}