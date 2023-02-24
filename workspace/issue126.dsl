workspace {
    
    !identifiers hierarchical

    model {
        softwareSystem1 = softwareSystem "Software System 1" {
            api = container "API"
        }

        softwareSystem2 = softwareSystem "Software System 2" {
            api = container "API"
        }

        softwareSystem1.api -> softwareSystem2.api "uses" "something" "and a tag"
    }
}