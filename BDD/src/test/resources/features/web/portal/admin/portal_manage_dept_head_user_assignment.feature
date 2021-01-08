@portal @portal_admin
@mcp
Feature: Portal - Admin - Manage Department Head User Assignment

  @grid
  Scenario: Managed resource assigned to Department Head
    Given a user with "Helpdesk Manager" profile and "Portal_Administrator_Section_Access" role
    When the user logs in
     And "Portal" is selected from the Mercury navigation menu
     And the "Manage Department Head User Assignment" sub menu is selected from the "Admin" top menu
     And a random user is selected as department head
     And a colleague is selected to fall under their management
     And the managed changes are saved
    Then the updated resource relationship is stored in the database
     