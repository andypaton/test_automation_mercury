@admin @admin_login
@mcp
@sanity
Feature: Admin - Login

  Scenario: Refrigerant Gas Usage - login with user without Mercury_Admin_Refrigeration role
    Given a helpdesk user without "Mercury_Admin_Refrigeration" role
     When the user logs in    
     Then they cannot navigate to the Admin "Refrigerant Gas Usage" tile

         
  #@MTA-928
  @grid
  Scenario: Impersonate a user with Mercury_Job_View role
    Given an IT user has logged in
     When a user with "Mercury_Job_View" role is impersonated
     Then the impersonated users home screen is displayed
