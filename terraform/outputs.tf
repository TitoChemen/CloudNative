output "ec2_public_ip" {
  description = "IP Elástica pública asignada a la EC2"
  value       = aws_eip.app_eip.public_ip
}

output "ssh_command" {
  value = "ssh -i ec2-key.pem ubuntu@${aws_eip.app_eip.public_ip}"
}

output "api_gateway_url" {
  description = "URL pública de tu AWS API Gateway"
  value       = aws_apigatewayv2_api.http_api.api_endpoint
}