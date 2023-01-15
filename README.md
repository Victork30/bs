# Practical Learning
Microservice - REST API with two endpoints. Has a config file src/config.properties. Configurable settings: 
HEALTHY_HOST=localhost
HEALTHY_PORT=8080

Microservice expects to get a JSON formatted list on a /packages endpoint. Example: 
{
    "package1": "list-it",
    "package2": "s-bpsw",
    "package3": "bffs",
    "package4": "express"
}

Deploy helm chart with command:
cd ./chart
helm install resty -f resty.yaml .
helm install healthy -f healthy.yaml .

