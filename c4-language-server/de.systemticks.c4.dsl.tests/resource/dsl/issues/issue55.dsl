workspace "Getting Started" "This is a model of my software system." {

    model {
        user = person "User" "A user of my software system."
        system = softwareSystem "Software System" "My software system." {
            apiApplication = container "API Application" "Provides Internet banking functionality via a JSON/HTTPS API." "Java and Spring MVC" {
                signinController = component "Sign In Controller" "Allows users to sign in to the Internet Banking System." "Spring MVC Rest Controller"
            }

            apiApplication2 = container "Another API Application" "Provides Internet banking functionality via a JSON/HTTPS API." "Java and Spring MVC" {
                signinController2 = component "Sign In Controller" "Allows users to sign in to the Internet Banking System." "Spring MVC Rest Controller"
            }
        }

        system2 = softwareSystem "Another Software System" "My software system." {
            apiApplication3 = container "API Application" "Provides Internet banking functionality via a JSON/HTTPS API." "Java and Spring MVC" {
                signinController3 = component "Sign In Controller" "Allows users to sign in to the Internet Banking System." "Spring MVC Rest Controller"
            }

            apiApplication4 = container "Another API Application" "Provides Internet banking functionality via a JSON/HTTPS API." "Java and Spring MVC" {
                signinController4 = component "Sign In Controller" "Allows users to sign in to the Internet Banking System." "Spring MVC Rest Controller"
            }
        }

        user -> apiApplication "Makes API calls to" "JSON/HTTPS"
    }

    views {
        systemContext system "SystemContext" "An example of a System Context diagram." {
            include *
            autoLayout
        }
        
        container system "Containers" {
            include *
            autoLayout
        }
    }
}
