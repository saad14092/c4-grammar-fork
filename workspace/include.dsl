workspace "Getting Started" "This is a model of my software system." {

    model {
        !include include2/model2.dsl
    }

    views {
        systemContext mysoftwareSystem "SystemContext" "An example of a System Context diagram." {
            include *
            autoLayout
        }

        styles {
            !include include2/syles.dsl
        }
    }

}
