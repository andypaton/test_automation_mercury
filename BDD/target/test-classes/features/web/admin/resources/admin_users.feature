@admin @admin_resources @admin_resources_users
@mcp
Feature: Admin - Resources - Users

  Scenario: Users page displayed as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Users" from the sub menu
     Then the Users page is displayed as expected
     
  Scenario: Add new User with a Username that already exists
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Users" from the sub menu
      And a new "random" User is created
      And a duplicate User is created
     Then a "Create user failed" error message is displayed

  Scenario Outline: Add new User - "<Resource Type>"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Users" from the sub menu
      And a new "<Resource Type>" User is created
     Then the User is displayed in the correct table
    Examples:
      | Resource Type |
      | Contractor    |
      | Landlord      |
      
  Scenario Outline: Edit User - "<Resource Type>"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Users" from the sub menu
      And a new "<Resource Type>" User is created
      And the User is edited
     Then the edited User is displayed in the correct table
    Examples:
      | Resource Type |
      | Contractor    |
      | Landlord      |