@admin @admin_assets
@mcp
Feature: Admin - Assets

  Scenario: The Asset Homepage is displayed correctly
    Given a "Helpdesk Manager" has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Assets" tile is selected
     Then the Asset screen is displayed as expected
      And the Asset Register menu is displayed by default
      
  Scenario: The Asset Register page is displayed correctly
    Given a "Helpdesk Manager" has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Assets" tile is selected
     Then the Asset Register page is displayed as expected
     
  #Bug MCP-18269 raised for console error.
  #No bug tag added as the test is still running fine
  Scenario: Add a new Asset
    Given a "Helpdesk Manager" has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Assets" tile is selected
      And a new Asset is added
     Then the Asset is present in the Asset Register
     
  Scenario: Edit an Asset
    Given a "Helpdesk Manager" has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Assets" tile is selected
      And a new Asset is added
      And the Asset is edited
     Then an Audit event has been created


