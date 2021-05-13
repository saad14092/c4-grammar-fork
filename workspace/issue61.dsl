workspace "CyrillicSupport"  {

    model {
        bug = softwareSystem "Reproducing the bug" "When description contains UTF-8 chars like Ö Ä Ü"
    }

    views {
        systemContext bug "SystemContext" "It seems the C4DslGenerator.xtend saves temporary file in OS default encoding, that is not UTF-8. Workaround: set the environment variable JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8" {
            include *
        }
    }
}
