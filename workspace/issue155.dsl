workspace  {

    # directive to set all identifiers to be hierarchical
    !identifiers hierarchical 
    model {
        asystem = softwareSystem "System A" {
            fun1 = container "Container Fun 1"
        }
        bsystem = softwareSystem "System B" {
            joy1 = container "Container Joy 1"
            -> asystem "Callback"
        }

        asystem -> bsystem "Calls" // works.
        asystem.fun1 -> bsystem.joy1
    }

    views {
        container bsystem "containers_sys_B" {
            include *
            autolayout lr
        }
    }

}
