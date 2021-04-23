okta = softwareSystem "Okta"  "User authn and authz" "auth" {
    test = container "Test Container" "Just a test container"
}

enterprise "MyEnterprise" {

    mySystem = softwareSystem "My System" "Some description here" {
        url "https://abc.def"
    }

}
