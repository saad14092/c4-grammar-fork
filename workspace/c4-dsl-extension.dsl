workspace "name" "description" {
    
    model {
         user = person "User"

        languageServer = softwareSystem "Language Server" "" "Server" {
            
            coredsl = container "c4.dsl" "description" "technology" "taglist" {
                validator = component "Validator" ""
                scoping = component "Scope Provider" ""
                generator = component "Generator" "description" 
            }

            dslIde = container "c4.dsl.ide" "" {
                service = component "C4 Language Service Launcher" ""
                codelense = component "Code Lense Service"
                hover = component "Tooltip Service"
                contentAssist = component "Proposal Provider"
            }

            lsp4e = container "org.eclipse.lsp4j.*" "Java implemention of the Language Server Protocol" "Java" "Extern"

            xtext = container "org.eclipse.xtext.*" "Xtext libraries" "Xtext" "Extern"

            parser = container "structurizr-dsl" "The origin parser of the structurizr-dsl project" "Java" "Structurizr"

            plantumlWriter = container "structurizr-plantuml" "The origin plant-uml writer from the structurizr-plantuml project" "Java" "Structurizr"
            
        }       

        extension = softwareSystem "Extension"  {
            
            languageClient = container "Language Client"

            semanticHighlighter = container "Semantic Highlighter"

            commands = container "C4 commands"
        }

        user -> languageClient "" "" ""

        coredsl -> xtext
        dslIde -> xtext

        service -> lsp4e

        codelense -> service "publish code lenses"
        hover -> service "publish tooltips"
        contentAssist -> service "publish prposals"

        service -> validator "publish diagnostics"
        
        scoping -> service "publish scoping"

        languageClient -> service "description" "JSON-RPC/LSP"

        service -> generator "called whenever model is valid"

        generator -> parser "transform the xtext resource into a c4 model"
        parser -> plantumlWriter "create a puml file from the c4 model"

    }

    views {
        systemContext languageServer "name" "description" {
            include *
            include user
        }

        container languageServer "C4Overview" "description" {
            include *
        }

        container extension "VSCodeExtension" "" {
            include *
            include user
            autoLayout
        }

        component coredsl "LanguageServerCore" {
            include *
            include plantumlWriter
            include xtext
            autoLayout
        }

        component dslIde "LanguageServerIde" {
            include *
            include lsp4e
            autoLayout
        }

        styles {
            element "Person" {
                background #08427b
                color #ffffff
                fontSize 22
                shape Person
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }

            element "Server" {
                background #00707F
                color #ffffff
            }
        
            element "Extern" {
                background #999999
                color #ffffff
            }

            element "Structurizr" {
                background #387743                
                color #ffffff
            }

            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
        }

    }

}