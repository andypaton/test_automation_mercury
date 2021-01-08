@admin @admin_organisation
Feature: Admin - Organisation
  
  @mcp
  Scenario: View Organisation Structure Home Page
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Organisation" tile is selected
     Then the user is taken to the Organisational Structure Home Page
      And the system displays a key explaining the colour coding
      And the current active hierarchies are displayed
      
  @uswm
  Scenario Outline: Verify that a user can view the "<hierarchy>" hierarchy option
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When the user clicks on the "<hierarchy>" hierarchy
     Then the system displays correct divisions
    Examples: 
      | hierarchy              | 
      | Retail                 |
      | CLT                    |
      | MST                    |
      | RHVAC                  |
      | Landscape              |
      | Groundskeeper          |
      | Irrigation             |
      | Power Washing          |
      | Sweeper                |
      | Seasonal Groundskeeper |
        
  @ukrb
  Scenario Outline: Verify that a user can view the "<hierarchy>" hierarchy option
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When the user clicks on the "<hierarchy>" hierarchy
     Then the system displays correct divisions
    Examples: 
      | hierarchy         | 
      | Retail            |
      | AMT               |
      | Fire and Security |
      | HVAC              |
      | Refrigeration     |
      | Water             |
      | Water Tank        |
      | EQS               |
      | Client            |
      | Lift              |
  
  @usad
  Scenario Outline: Verify that a user can view the "<hierarchy>" hierarchy option
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When the user clicks on the "<hierarchy>" hierarchy
     Then the system displays correct divisions
    Examples: 
      | hierarchy | 
      | MST       |
      | RHVAC     |
      | Retail    |
      | Fuel      |
      
  @mcp
  Scenario: Verify that a user moving a site can select the Cancel button on the site page
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When a random tech position is selected
      And a random location to be moved is selected
      And the "Cancel" button on the "site page" is clicked
     Then the site location is available for the tech position

  @mcp
  Scenario: Verify that a user moving a site can select the Cancel button on the dialog box
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When a random tech position is selected
      And a random location to be moved is selected
      And the "Save" button on the "site page" is clicked
      And the "Cancel" button on the "dialog box" is clicked
     Then the site location is available for the tech position
      
  @mcp
  Scenario: Verify that a user can move a site
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When a random tech position is selected
      And a random location to be moved is selected
      And the "Save" button on the "site page" is clicked
      And the "Save" button on the "dialog box" is clicked
     Then the site location is not available for the tech position
  
  #Can't finish the following tests until bug MCP-16574 is fixed. The dropdown is unclickable and positioned incorrectly on the page
  @wip @notsignedoff @mcp
  Scenario: Verify that a user adding a manager can select the Cancel button next to the drop down menu
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When a random tech position is selected
      And the "add" manager button is clicked
      And a random manager is selected
      And the "remove" manager button is clicked
     Then the manager is not added to the site
           
  @wip @notsignedoff @mcp
  Scenario: Verify that a user can add a manager
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Organisation" tile is selected
     When a random tech position is selected
      And the "add" manager button is clicked
      And a random manager is selected
      And the "Save" button on the "site page" is clicked
      And the "Save" button on the "dialog box" is clicked
     Then the manager is added to the site