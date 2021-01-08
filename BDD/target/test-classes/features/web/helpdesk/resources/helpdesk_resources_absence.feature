@helpdesk @helpdesk_resources @helpdesk_manage_resource
@mcp
Feature: Helpdesk - Resources - Absence
    
  Scenario Outline: Helpdesk user adding a new absence to a resource with "<cover>" cover
    Given a "Helpdesk Operator" has logged in       
      And a search is run for an "active" resource
     When an absence is added with "<cover>" cover
     Then the absence details are displayed/recorded correctly
    Examples: 
      | cover     | 
      | backup    | 
      | no backup |  
     
  Scenario: Helpdesk user adding a new absence to a resource where existing absence exists
    Given a "Helpdesk Operator" has logged in       
      And a search is run for an "active" resource
      And an absence is added with "backup" cover
     When a new Absence is created with backup resource assigned that overlaps an existing absence
     Then an alert is displayed and the new absence is not added/saved
     
  Scenario: Helpdesk user canceling an absence before saving it
    Given a "Helpdesk Operator" has logged in       
      And a search is run for an "active" resource
     When an "Absence" is keyed but cancelled before being saved
     Then the absence will not be displayed in the table view         

  Scenario: Helpdesk user adding a new absence to a resource - Previously added absences still outstanding will show in a table on the Add or override absence panel
    Given a "Helpdesk Operator" has logged in       
      And a search is run for an active resource with outstanding absences
     When the "Add or override absence" action is selected
     Then the previously added absences still outstanding will show in a table on the Add or override absence panel
     