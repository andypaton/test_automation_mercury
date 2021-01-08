@admin @admin_fgas
@mcp
Feature: Admin - Refrigerant Gas Usage - Search

  #@MCP-5988
  Scenario: Refrigerant Gas Usage - search - invalid job reference
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for an invalid job reference
     Then the following alert is displayed: "No site visit found for this job reference."
     
     
  #@MCP-5988
  Scenario: Refrigerant Gas Usage - search - job does not have site visit
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for job without a site visit
     Then the following alert is displayed: "No site visit found for this job reference."
     
     
  #@MCP-5988
  Scenario: Refrigerant Gas Usage - search - job with a non gas site visit
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for job with a non gas site visit
     Then the following alert is displayed: "There was no gas used for this job reference"      
         