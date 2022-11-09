workspace "Test" {
    model {

        group "ABC" {
            system = softwareSystem "abc" {
            }
        }

        deploymentEnvironment "QAT" {
             deploymentNode "testnode" {
                deploymentNode "testnode2" {
                        
                }
            }
        }
    }

    views {
        deployment system "QAT" {
            include *
            autoLayout 
            title "QAT"
        }

    }
}
