@admin @admin_resources @admin_resources_user_profiles
@mcp
Feature: Admin - Resources - User Profiles

  Scenario: User Profiles page displayed as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "User profiles" from the sub menu
     Then the User Profiles page is displayed as expected
     
  Scenario Outline: Add a new User Profile - "<User Profile>"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "User profiles" from the sub menu
      And a new "<User Profile>" User Profile is created
     Then the User Profile is displayed in the "<User Profile>" table
    Examples: 
      | User Profile |
      | Active       |
      | Inactive     |      

  Scenario: De-activate a User Profile
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "User profiles" from the sub menu
     When a new "Active" User Profile is created
      And the profile is de-activated
     Then the User Profile is displayed in the "Inactive" table  

  Scenario: De-activate a User Profile associated to active Resource
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "User profiles" from the sub menu
     When a new User Profile is created
      And an active Resource is associated to this User Profile
      And the User Profile is de-activated
     Then the de-activate profile modal is displayed as expected
      And the User Profile is displayed in the "Inactive" table   

  Scenario: Activate a User Profile
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "User profiles" from the sub menu
     When a new "Inactive" User Profile is created
      And the profile is activated
     Then the User Profile is displayed in the "Active" table
