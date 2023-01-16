# Practical Learning
Microservice - REST API with two endpoints. Has a config file src/config.properties. Configurable settings: 
* HEALTHY_HOST=localhost
* HEALTHY_PORT=8080

HEALTHY_HOST - healthy service name;
HEALTHY_PORT - healthy service port

Microservice expects to get a JSON formatted list on a /packages endpoint. Example: 
> {
>     "package1": "list-it",
>     "package2": "s-bpsw",
>     "package3": "bffs",
>     "package4": "express"
> }

Deploy nginx ingress controller with terraform (make sure your kubeconfig file is located at ~/.kube/config):
```sh
cd ./terraform
terraform init
terraform apply
```
Deploy helm chart with command:
```sh
cd ./chart
helm install resty -f resty.yaml .
helm install healthy -f healthy.yaml .
```
