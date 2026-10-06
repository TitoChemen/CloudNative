output "elastic_ip_fija" {
  description = "🔥 IP FIJA OFICIAL: Usa esta para Azure, el .env y el SSH"
  value       = aws_eip.app_server_eip.public_ip
}

output "ssh_command" {
  description = "Comando SSH directo a la Elastic IP fija"
  value       = "ssh -i ec2-key.pem ubuntu@${aws_eip.app_server_eip.public_ip}"
}

output "api_gateway_url" {
  description = "URL pública de tu AWS API Gateway"
  value       = aws_apigatewayv2_api.http_api.api_endpoint
}