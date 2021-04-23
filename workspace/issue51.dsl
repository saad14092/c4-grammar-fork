workspace "MyWorkspace" "This is my fancy workspace" {

    
    model {
        !include platformModel/platform.dsl
    }

    views {
        !include platformViews/platform.dsl
    }

}
