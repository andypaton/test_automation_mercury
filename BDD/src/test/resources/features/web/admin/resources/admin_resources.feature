@admin @admin_resources
Feature: Admin - Resources

  @mcp
  Scenario: Resources & Users main page displayed as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Resources & Users" tile is selected
     Then the Resources & Users main page is displayed as expected
     
  @mcp
  Scenario: Resources page displayed as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Resources" from the sub menu
     Then the Resources page is displayed as expected
     
  #No env tag added as this scenario is just used for data setup
  Scenario: Add new Landlord Resource
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Resources" from the sub menu
     Then a Landlord resource is added
  
  @mcp
  Scenario Outline: Add new Resource - "<Status>"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a "<Status>" resource is added
     Then the Resource has been added into the "<Status>" table
    Examples:
      | Status     |
      | Active     |
      | Inactive   |
      | Incomplete |
      
  @mcp
  Scenario Outline: Edit a "<Status>" Resource
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a "<Status>" resource is added
      And the "<Status>" resource is edited
     Then the edited Resource has been added into the correct table
    Examples:
      | Status     |
      | Active     |
      | Inactive   |
      
  @mcp
  Scenario: De-activate a City Resource with no jobs assigned
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource is added with a site assigned
      And the City Resource is de-activated
     Then all permanent sites have been removed
     
  @mcp
  Scenario: Verify that error message is displayed when de-activating a City Resource with job assigned
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource is added with a site assigned
      And a job is assigned to the city resource
      And the City Resource is de-activated
     Then the error "The resource is still assigned to 1 jobs." is displayed
     
  @mcp
  Scenario: De-activate a City Resource with job assigned
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource is added with a site assigned
      And a job is assigned to the city resource
      And the resource is removed from the job
     Then the City Resource is de-activated
      And all permanent sites have been removed
     
  @mcp
  Scenario: Verify that error message is displayed when de-activating a Contractor with job assigned
        * using dataset "admin_resources_001"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When an Active Contractor Resource is created
      And a Job is assigned to the resource
      And the Resource is de-activated
     Then the error "The resource is still assigned to 1 jobs." is displayed
     
  @mcp
  Scenario: De-activate a Contractor Resource with Job assigned then removed
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When an Active Contractor Resource is created
      And a Job is assigned to the resource
      And the resource is removed from the job
     Then the Resource is de-activated
      And the contractor sites are still displayed
     
  @mcp
  Scenario: Resource working hours are displayed as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource is added and the resource profile is selected
     Then the Resource working hours are populated as expected
     
  @mcp
  Scenario: Resource working hours can be edited as expected 
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource who works 5 days or less is added and the resource profile is selected
      And the working hours are edited
     Then the shift summary has been updated with the edited values

  @mcp
  Scenario: Map a site to a resource
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource who can be assigned to a site is added
      And the resource is edited and a site which already has resource assigned is added
     Then the user is presented with an option to replace the existing resource
     
  @mcp
  Scenario: Map a site to a Contractor resource
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When an Active Contractor Resource is created
      And the resource is edited and another site is added
     Then the classifications are displayed as expected
     
  @mcp
  Scenario: Editing Classification Mapped to a Contractor 
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When an Active Contractor Resource is created
      And the resource is edited
     Then the classification is displayed as expected
     
  @mcp
  Scenario: Rota entry is generated for Permanent site
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource is added with a site assigned
     Then a rota entry is generated
     
  @mcp
  Scenario: Remove a Permanent Site from a Resource
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a new resource is added with a site assigned
      And the site is removed
     Then the warning message is displayed
     
  @mcp
  Scenario Outline: Geo-Fencing for a Contractor Resource is "<Geo-Fencing>"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When an Active Contractor Resource is created with Geo-Fencing "<Geo-Fencing>"
     Then Geo-Fencing is set to "<Geo-Fencing>"
    Examples:
      | Geo-Fencing |
      | Enabled     |
      | Disabled    |

  #Adding deprecated and wip tags until but MCP-15862 is resolved
  #Business Team have acknowledged that the bug is in live and are happy with it
  @notsignedoff @bugWalmart @deprecated @wip
  Scenario: E-mail is sent to Landlord Resource [bug: MCP-15862]
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When a Landlord resource is added
      And a job is logged against this resource with ETA provided
    #Once bug is resolved write Then step to check that email has been sent to Landlord