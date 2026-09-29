# 1. Genera el par de claves RSA
resource "tls_private_key" "ec2_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# 2. Registra la clave pública en AWS
resource "aws_key_pair" "generated_key" {
  key_name   = "microservicios-key"
  public_key = tls_private_key.ec2_key.public_key_openssh
}

# 3. Guarda la clave privada en tu máquina local para el SSH
resource "local_file" "ssh_key" {
  content         = tls_private_key.ec2_key.private_key_pem
  filename        = "${path.module}/ec2-key.pem"
  file_permission = "0400"
}
# Obtener la AMI más reciente de Ubuntu 22.04 LTS
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# Security Group con los puertos de la arquitectura
resource "aws_security_group" "microservicios_sg" {
  name        = "microservicios-sg"
  description = "Reglas de entrada para Microservicios y Frontend"

  # SSH
  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Frontend Web (Nginx)
  ingress {
    description = "Frontend HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # API Gateway
  ingress {
    description = "Spring Cloud API Gateway"
    from_port   = 9000
    to_port     = 9000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Eureka Server (Dashboard web)
  ingress {
    description = "Eureka Server"
    from_port   = 8761
    to_port     = 8761
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Salida libre a Internet
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "microservicios-security-group"
  }
}

# Instancia EC2 con aprovisionamiento inicial (Docker + Swap de 4GB)
resource "aws_instance" "app_server" {
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.instance_type
  key_name                    = aws_key_pair.generated_key.key_name
  vpc_security_group_ids = [aws_security_group.microservicios_sg.id]
  associate_public_ip_address = true

  root_block_device {
    volume_size           = 25
    volume_type           = "gp3"
    delete_on_termination = true
  }

  user_data = <<-EOF
              #!/bin/bash
              set -e

              # 1. Crear Swap de 4GB para evitar OOM durante los builds
              fallocate -l 4G /swapfile
              chmod 600 /swapfile
              mkswap /swapfile
              swapon /swapfile
              echo '/swapfile none swap sw 0 0' >> /etc/fstab

              # 2. Instalar Docker y Docker Compose plugin
              apt-get update -y
              apt-get install -y ca-certificates curl gnupg lsb-release
              install -m 0755 -d /etc/apt/keyrings
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
              chmod a+r /etc/apt/keyrings/docker.gpg

              echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

              apt-get update -y
              apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

              # 3. Habilitar servicio y dar permisos al usuario ubuntu
              systemctl enable --now docker
              usermod -aG docker ubuntu
              EOF

  tags = {
    Name = "microservicios-backend-instance"
  }
}

# ==========================================
# AWS API GATEWAY V2 (HTTP API)
# ==========================================

# 1. Definimos el API Gateway
resource "aws_apigatewayv2_api" "http_api" {
  name          = "microservicios-api-gateway"
  protocol_type = "HTTP"
  description   = "API Gateway nativo de AWS apuntando a la EC2"
}

# 2. Creamos la integración (conecta AWS API Gateway con la IP de tu EC2)
resource "aws_apigatewayv2_integration" "ec2_integration" {
  api_id             = aws_apigatewayv2_api.http_api.id
  integration_type   = "HTTP_PROXY"
  integration_uri    = "http://${aws_instance.app_server.public_ip}:9000/{proxy}" 
  integration_method = "ANY"
  connection_type    = "INTERNET"
}

# 3. Configuramos la ruta para que atrape cualquier endpoint que le pidan
resource "aws_apigatewayv2_route" "default_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.ec2_integration.id}"
}

# 4. Desplegamos el Stage por defecto para que funcione automático
resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}