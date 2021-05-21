workspace {

    model {
        softwareSystem1 = softwareSystem "Software System" {
            database = container "Database"
            api = container "Service API" {
                -> database "Uses"
            }
        }

        deploymentEnvironment "Example 1" {
            deploymentNode "Server 1" {
                containerInstance api
                containerInstance database
            }
            deploymentNode "Server 2" {
                containerInstance api
                containerInstance database
            }
        }
    }

    views {
        deployment * "Example 1" {
            include *
            autolayout
        }

    }

}