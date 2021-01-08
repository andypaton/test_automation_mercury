@admin @admin_import_configuration
@mcp
Feature: Admin - Import Configuration

  Scenario: The Import Configuration screen is displayed correctly
    Given a user with "Mercury_Import_PO_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Import Configuration" tile is selected
     Then the Import Configuration screen is displayed as expected
      And the "Purchase Order Configuration" menu is displayed by default
     
  Scenario Outline: The "<Page>" page is displayed correctly
    Given a user with "Mercury_Import_PO_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Import Configuration" tile is selected
      And the user selects "<Page>" from the sub menu
     Then the "<Page>" page is displayed as expected
    Examples:
      | Page                         |
      | Purchase Order Configuration |
      | PPM Configuration            |
      | Finance Recode Import        |
      
  @notsignedoff
  Scenario Outline: Purchase Order Configuration - Import Orders "<Import Orders>" 
    Given a user with "Mercury_Import_PO_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Import Configuration" tile is selected
      And the user selects "Purchase Order Configuration" from the sub menu
     When a "Purchase Order Configuration" file is created "<Import Orders>"
      And the "<Import Status>" "Purchase Order Configuration" file is uploaded and processed
      And the file is imported "<Import Orders>"
     Then a "<Import Status>" "Purchase Order Configuration" file has been imported and relevant message is correct
    Examples:
      | Import Orders  | Import Status |
      | Successfully   | Successful    |
      | Unsuccessfully | Unsuccessful  |
   
  @notsignedoff @bugAdvocate
  Scenario Outline: PPM Configuration - Import Orders "<Import Orders>" [bug: MCP-21936]
    Given a user with "Mercury_Import_PPM_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Import Configuration" tile is selected
      And the user selects "PPM Configuration" from the sub menu
     When a "PPM Configuration" file is created "<Import Orders>"
      And the "<Import Status>" "PPM Configuration" file is uploaded and processed
     Then a "<Import Status>" "PPM Configuration" file has been imported and relevant message is correct
    Examples:
      | Import Orders  | Import Status |
      | Successfully   | Successful    |
      | Unsuccessfully | Unsuccessful  |
