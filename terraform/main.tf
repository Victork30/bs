provider "kubernetes" {
  config_path = "~/.kube/config"
}

resource "kubernetes_namespace" "nginx-ingress" {
  metadata {
    annotations = {
      name = "terraform"
    }

    labels = {
      mylabel = "terraform-managed"
    }

    name = "nginx-ingress"
  }
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

resource "helm_release" "nginx_ingress" {
  name       = "nginx-ingress-controller"
  namespace  = "nginx-ingress"
  repository = "https://charts.bitnami.com/bitnami"
  chart      = "nginx-ingress-controller"

  set {
    name  = "service.type"
    value = "NodePort"
  }

}

