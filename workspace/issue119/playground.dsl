workspace {
    model {
        user = person "User" "A user of my software system."
        softwareSystem = softwareSystem "Software System" "My software system."
        user -> softwareSystem "Uses something"
    }

    views {
        !include "myview.dsl"
    }
}
