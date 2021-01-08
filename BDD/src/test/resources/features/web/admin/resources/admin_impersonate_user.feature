@admin @admin_resources @admin_resources_impersonate
Feature: Admin - Resources - Impersonate User
 
  @mcp
  Scenario: Impersonate User page displayed as expected
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Impersonate user" from the sub menu
     Then the Impersonate User page has the correct column names
      And each column filter defaults to "Contains"
      And the "Name, User Name, Resource Profile" columns can be filtered
      
  @mcp
  Scenario Outline: Impersonate <PROFILE>
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Impersonate user" from the sub menu
      And a "<PROFILE>" is selected from Resource Profiles
     Then the impersonated users home screen is displayed
    Examples:
      | PROFILE               |
      | Contractor Admin      |
      | Contractor Technician |   
      
  @uswm
  Scenario: Impersonate RFM
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Impersonate user" from the sub menu
      And a "RFM" is selected from Resource Profiles
     Then the impersonated users home screen is displayed
      
  @ukrb
  Scenario: Impersonate AMM
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Impersonate user" from the sub menu
      And a "AMM" is selected from Resource Profiles
     Then the impersonated users home screen is displayed      

  @uswm @ukrb @usah
  Scenario: Impersonate Landlord
    Given a job is logged and assigned to a "Landlord"
      And an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user is impersonated
      And menu item "Jobs > Jobs Awaiting Acceptance" is selected
      And the Job Awaiting Acceptance is selected
     Then the job can be Accepted
      And the job can be Declined
      And the ETA can be updated
      And the impersonating and impersonated users are recorded on the job event timeline
