@admin @admin_rules
@mcp
Feature: Admin - Rules Engine

  @bugRainbow @bugWalmart
  Scenario: Question list displayed as expected [bug: MCP-13769 ukrb, MCP-20612 uswm]
    Given a user with "Mercury_Admin_Rule_Engine" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Rules Engine" tile is selected
     When the user selects "Question List" from the sub menu
     Then the "Question List" table is displayed
      And the "Question List" table can be sorted on all columns
      And a search can be run on the "Question List" table
      
  Scenario: Add new question to the list
    Given a user with "Mercury_Admin_Rule_Engine" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Rules Engine" tile is selected
     When the user selects "Question List" from the sub menu 
     Then a new question can be added
      And the question has been added to the database
      
  Scenario: Edit a question on the list
    Given a "Helpdesk Manager" has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Rules Engine" tile is selected
     When the user selects "Question List" from the sub menu
      And the selected question is edited
     Then the question is updated on the database
    
  Scenario: Add new rule to the list
    Given a user with "Mercury_Admin_Rule_Engine" role has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Rules Engine" tile is selected
      And add new rule is selected
     Then a new rule can be added
      And the rule has been added to the database