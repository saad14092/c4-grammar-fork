workspace "name" "description" {

    model {
        s1 = softwareSystem "S1" "Software System"
        s2 = softwareSystem "S2" "Software System" {
            c1 = container "NameXYZ" {
                co1 = component "c101"
                co2 = component "c2"
                co3 = component "c3"
            }
            c2 = container "ABC"
        }

        co1 -> co2 "Uses"
        co2 -> co3 "Use also me"
        s1 -> s2 "Uses"
        
                
    }

    views {

        systemLandscape "s1" {

        }

        component c1 {
            include *
            autoLayout
        }

        container s2 {
            include *
            autoLayout
        }

    }

}