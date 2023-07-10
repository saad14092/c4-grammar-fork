workspace "Test" {
   model {
      
      group "groupName" {
         testSystem = softwareSystem "System"
         
         system2 = softwareSystem "System2" {
            webapp = container "Web Application" {
               
               -> testSystem "Push email/Pull authentication data" "HTTPS"
            }
            
            # -> testSystem "Push email/Pull authentication data"
         }
      }
   }
}