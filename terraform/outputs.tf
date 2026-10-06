output "instance_public_ip" {
  value = aws_instance.app_server.public_ip
}

output "ssh_command" {
  value = "ssh -i ec2-key.pem ubuntu@${aws_instance.app_server.public_ip}"
}

output "api_gateway_url" {
  description = "URL pública de tu AWS API Gateway"
  value       = aws_apigatewayv2_api.http_api.api_endpoint
}

output "elastic_ip_fija" {
  description = "Esta es la IP que tienes que poner en las Redirect URIs del portal de Azure"
  value       = aws_eip.app_server_eip.public_ip
}