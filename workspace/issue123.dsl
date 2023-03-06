workspace "A typical Elastic Load Balancer" {

    model {

        client = person "Client"
        typicalELB = softwaresystem "Typical ELB" "Application Load Balancer, Network Load Balancer or Classic Load Balancer" {
            webApplication = container "Web Application" "" "nodejs" {
                tags "Application"
            }
        }


        live = deploymentEnvironment "Live" {

            deploymentNode "Amazon Web Services" {
                tags "Amazon Web Services - Cloud"


                  route53 = infrastructureNode "Route 53" {
                      description "Highly available and scalable cloud DNS service."
                      tags "Amazon Web Services - Route 53"
                  }

                  elb = infrastructureNode "Elastic Load Balancer" {
                      description "Automatically distributes incoming application traffic."
                      tags "Amazon Web Services - Elastic Load Balancing"
                  }

                  deploymentNode "Autoscaling group" {
                      tags "Amazon Web Services - Auto Scaling"

                      deploymentNode "Amazon EC2" {
                          tags "Amazon Web Services - EC2"
                          instances "1..N"

                          webApplicationInstance = containerInstance webApplication
                      }
                  }

            }

            route53 -> elb "Forwards requests to" "HTTPS"
            elb -> webApplicationInstance "Forwards requests to" "HTTPS"
        }
    }

    views {
        deployment typicalELB "Live" "AmazonWebServicesDeployment" {
            include *
            autolayout lr
        }

        styles {
            element "Element" {
                shape roundedbox
                background #ffffff
            }
            element "Container" {
                background #ffffff
            }
            element "Application" {
                background #ffffff
            }
            element "Database" {
                shape cylinder
            }
        }

        themes https://static.structurizr.com/themes/amazon-web-services-2020.04.30/theme.json
    }

}
