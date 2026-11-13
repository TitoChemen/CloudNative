variable "aws_region" {
  type        = string
  default     = "us-east-1"
  description = "Región de AWS"
}

variable "instance_type" {
  type        = string
  default     = "t3.small" # Recomendado para soportar varios servicios Spring Boot + Swap
  description = "Tipo de instancia EC2"
}