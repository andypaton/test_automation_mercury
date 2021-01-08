@admin @admin_sites
@mcp
Feature: Admin - Sites
  
  Scenario: Admin Sites - Home Screen
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Sites" tile is selected
     Then the mercury navigation menu in the admin "Sites" page displays options correctly
      And the "Sites" menu option in the admin "Sites" page is highlighted  

  Scenario Outline: Search for an <status> site in the admin sites grid
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
     When user searches for an "<status>" site
     Then the "Sites" table on the "Sites" page displays each row correctly
    Examples: 
      | status   | 
      | Active   | 
      | Inactive | 
      
  @bugAdvocate
  Scenario Outline: Adding a new site - <button> [bug: MCP-21761]
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
     When the new site information is entered
      And the "<button>" button is clicked
     Then the site "<added>" added to the database
      And the site "<searchable>" searchable in the Sites page
    Examples: 
      | button | added  | searchable | 
      | Cancel | is not | is not     | 
      | Save   | is     | is         |   
      
  Scenario: Editing a Site - Verify Site already exists error
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
     When a site is edited with already existing site name
      And the "Save" button is clicked
     Then "Error" popup alert is displayed with text "Site already exists with this name."
      And the following alert is displayed: "Failed to update site"
     
  @bugAdvocate
  Scenario: Editing a Site [bug: MCP-21761]
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
     When a site is edited
      And the "Save" button is clicked
     Then the site "is" saved to the database
      And the site "is" searchable in the Sites page
      
  Scenario: Making a site Inactive - Verify deactivate account question alert
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
     When a site is made Inactive
     Then "Site Administration" popup alert is displayed with text "Do you really wish to deactivate this account?"
     
  Scenario: Making a site Inactive
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Sites" tile is selected
     When a site is made Inactive
      And the "Site Administration" popup alert is confirmed
     Then the site now appears in the Inactive tab     