workspace "Getting Started" "This is a model of my software system." {

    model {
        !include model58.dsl
    }

    views {
        systemContext mysoftwareSystem "SystemContext" "An example of a System Context diagram." {
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
