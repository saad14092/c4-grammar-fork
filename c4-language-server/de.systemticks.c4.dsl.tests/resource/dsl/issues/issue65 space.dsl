workspace "Getting Started" "This is a model of my software system." {

    model {

        user = person "User" "A user of my software system."
        system1 = softwareSystem "Software System" "My software system." {
            apiApplication = container "API Application" "Provides Internet banking functionality via a JSON/HTTPS API." "Java and Spring MVC"
        
             user -> apiApplication "Makes API calls to" "JSON/HTTPS"
        }
    }

    views {
        systemContext system1 "SystemContext" "Another cool example of a System Context diagram." {
            include *
            autoLayout
        }
        
        container system1 "Containers" {
            include *
            autoLayout
        }

        styles {
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Person" {
                shape Person
                background #08427b
                color #ffffff
            }
        }
    }
    
}
