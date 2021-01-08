@admin @admin_resources @admin_resources_resource_profiles
@mcp
Feature: Admin - Resource Profiles

  Scenario: Resource Profiles page displayed as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Resource profiles" from the sub menu
     Then the Resource Profiles page is displayed as expected    

  Scenario: Add new Resource Profile page displayed as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Resource profiles" from the sub menu
      And the add new resource profile button is clicked
     Then all the resource types are displayed in the dropdown
      And the Create Resource Profile button is unavailable     

  Scenario Outline: Add new Resource Profile with Resource Type "<Resource Type>"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Resource profiles" from the sub menu
      And a new "<Resource Type>" Resource Profile is added
     Then the Resource Profile is displayed in the correct table
    Examples:
      | Resource Type             |
      | City Resource             |
      | Operational Manager       |
      | Head Office               |
      | Client Contact            |
      | Directors                 |
      | System                    |
      | Contractor                |
      | Landlord                  |
      | Team (Distribution Group) |    

  Scenario: Edit and de-activate a Resource Profile
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Resource profiles" from the sub menu
      And a new "random" Resource Profile is added
      And the Resource Profile is de-activated
     Then the confirm removal checkbox is displayed

  Scenario: Resource Profile working hours can be edited as expected
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
     When the user selects "Resource profiles" from the sub menu
      And a new "random" Resource Profile is added
      And the Resource Profile working hours are edited
     Then the Resource Profile shift summary has been updated with the edited values