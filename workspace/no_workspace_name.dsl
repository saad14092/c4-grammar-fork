workspace {

    model {
        user = person "User" "A user of my software system."
        sys = softwareSystem "Software System" "My software system."

        user -> sys "Uses"
    }

    views {
        systemContext sys "a" {
            include *
            autoLayout
        }
    }
}
