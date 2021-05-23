workspace "Getting Started" "This is a model of my software system." {

    model {
        !include "include/model2.dsl"
    }

    views {
        systemContext mysoftwareSystem53 "SystemContext" "An example of a System Context diagram." {
            include *
            autoLayout
        }

        styles {
            !include "include/syles.dsl"
        }
    }

}
