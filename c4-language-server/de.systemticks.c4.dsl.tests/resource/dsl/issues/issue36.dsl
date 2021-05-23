workspace {
    model {
 
       dhw_developer = person "DHW Developer" "Developer of DataHandwerk Toolkit"

        dhw_toolkit = softwareSystem "DataHandwerk Toolkit"
        project_a_dwh_dev = softwareSystem "DWH A Development" {
            project_a_repo_db = container "Repository database (Project A)"
            project_a_dwh_db = container "DWH database (Project A)"
        }
        dhw_git = softwareSystem "DataHandwerk git" "Code of DataHandwerk unter Version Control"

        enterprise "Customer" {
            dwh_developer = person "DWH Developer" "Developer of some DWH"
            deployment_team = person "Deployment Team"
            project_b_dwh_user = person "Project B DWH User" "user of DWH (Project B)"

            project_b_dev = softwareSystem "DWH B Development" {
                project_b_repo_db = container "Repository database (Project B)" {
                    qwertz = component "qwertz"
                    asdfg = component "asdfg"
                }
                project_b_dev_dwh_db = container "DWH database Development (Project B)"
            }
            project_b_prod = softwareSystem "DWH B Production" {
                project_b_prod_dwh_db = container "DWH database Production (Project B)"
            }
            project_b_git = softwareSystem "DWH B git" "Code of Project B unter Version Control (git, subversion, ...)"
        }

        dhw_developer -> dhw_toolkit "developes"
        dhw_developer -> project_a_dwh_dev "uses for testing and development"
        dhw_developer -> dhw_git "uses"

        dwh_developer -> dhw_git "takes code or builds"
        // dwh_developer -> dhw_toolkit "uses for development"
        dwh_developer -> project_b_dev "developes"
        dwh_developer -> project_b_git "uses"

        deployment_team -> project_b_git "takes code or builds"
        deployment_team -> project_b_prod_dwh_db "deployes on production system"

        project_b_dwh_user -> project_b_prod_dwh_db "uses"
    }
    views {
        systemlandscape "SystemLandscape" {
            include *
            autoLayout
        }
        systemContext dhw_toolkit {
            include *
            autoLayout
        }
        systemContext project_a_dwh_dev {
            include *
            autoLayout
        }
        systemContext project_b_dev {
            include *
            autoLayout
        }
        systemContext project_b_prod {
            include *
            autoLayout
        }
        container dhw_toolkit {
            include *
            autoLayout
        }
        component project_a_repo_db {
            include *
            autoLayout
        }
    }

}
