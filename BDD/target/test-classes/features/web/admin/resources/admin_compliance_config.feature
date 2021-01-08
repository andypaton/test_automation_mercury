@admin @admin_compliance_config
@mcp
Feature: Admin - Compliance Config

@notsignedoff
  Scenario Outline: Assigning compliance certificate validation permissions to user profile
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "User profiles" from the sub menu
      And a new "Active" User Profile is created
     When the user profile is edited and compliance "<Permission Name>" selected and save the changes
     Then the "<Permission Name>" is saved to the database
      And the user is returned to the user profiles main screen
    Examples: 
      | Permission Name                          | 
      | Portal_Compliance_Certificate_Validation | 
      | Portal_Compliance_Certificate_Review     | 
  
  Scenario: User is taken to the Certificate Configuration page
    Given a "Helpdesk Manager" has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Certificate Configuration" tile is selected
     Then certificate configuration screen is displayed as expected