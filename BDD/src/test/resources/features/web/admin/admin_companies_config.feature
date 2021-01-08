@admin @admin_companies @admin_consolidated_invoicing @wip
@toggles @Invoicing @MandatoryInvoiceGeneral @LegalEntityAdmin
@ukrb
Feature: Admin - Companies - Configuration 

  Background: 
    Given the system feature toggle "Invoicing" is "enabled" 
      And the system sub feature toggle "MandatoryInvoiceGeneral" is "enabled"
      And the system sub feature toggle "LegalEntityAdmin" is "enabled"        
    
  #@MP7-493
  @notsignedoff
  Scenario: View Edit Company page - Primary Company 
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected 
     When a primary company is selected for editing 
     Then the Edit Company page is displayed 
    
  #@MP7-493
  @notsignedoff 
  Scenario: View Edit Company page - Non Primary Company 
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected 
     When a non-primary company is selected for editing  
     Then the Edit Company page is displayed
     
  #@MP7-512
  @notsignedoff 
  Scenario: Update a non primary company to become a primary company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a non-primary company is selected for editing
      And the "Additional details" section is selected
      And the company is changed to a primary company
      And the "Save" button is clicked
      And the company is confirmed to be a primary company
     Then the company is set as a primary company
     
  #@MP7-512
  @notsignedoff 
  Scenario: Update a primary company to become a non primary company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a primary company is selected for editing
      And the "Additional details" section is selected
      And the company is changed to a non-primary company
      And the "Save" button is clicked
      And the company is confirmed to be a non-primary company
     Then the company is set as a non-primary company
     
  @MP7-556
  @notsignedoff 
  Scenario: Add an Alias to a primary company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a primary company is selected for editing
      And the "Aliases" section is selected
      And an alias is added to the company
     Then the alias is linked to the company
    
  #@MP7-556
  @notsignedoff @grid
  Scenario: Remove an Alias from a primary company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a primary company is selected for editing
      And the "Aliases" section is selected
      And an alias is added to the company
      And the alias is removed from the company
     Then the alias is not linked to the company
     
  #@MP7-556
  @notsignedoff
  Scenario: Add an Alias to a non primary company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a non-primary company is selected for editing
      And the "Aliases" section is selected
      And an alias is added to the company
     Then the alias is linked to the company
    
  #@MP7-556
  @notsignedoff
  Scenario: Remove an Alias from a non primary company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a non-primary company is selected for editing
      And the "Aliases" section is selected
      And an alias is added to the company
      And the alias is removed from the company
     Then the alias is not linked to the company
     
  #@MP7-591
  @notsignedoff  
  Scenario: View the list of company assignment rules
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When the user selects "Assignment Rule" from the sub menu
     Then the assignment rules list is shown
     
  #@MP7-591
  @notsignedoff 
  Scenario: Check if the correct rule is assigned to a company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When the user selects "Assignment Rule" from the sub menu
     Then the correct assignment rule is shown for the company
     
  #@MP7-591
  @notsignedoff 
  Scenario: Check if the primary company has the correct rule assigned to it
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the primary company is setup
     When the user selects "Assignment Rule" from the sub menu
     Then the primary company is visible in the grid
     
  #@MP7-591
  @notsignedoff 
  Scenario: Add new rule - Non Primary - Funding Route
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user adds a new "Funding Route" rule for a non-primary company 
     Then the new rule is added to the company
     
  #@MP7-591
  @notsignedoff 
  Scenario: Add new rule - Non Primary - Site Type
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user adds a new "Site Type" rule for a non-primary company 
     Then the new rule is added to the company
     
  #@MP7-629
  @notsignedoff
  Scenario: Edit an existing rule - Non Primary - Site Type
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user clicks on a "Site Type" assignment rule to edit
      And the user changes the "Site Type" assignment rule details
     Then the company uses the new "Site Type" rule detail
     
  #@MP7-629
  @notsignedoff 
  Scenario: Edit an existing rule - Non Primary - Funding Route
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user clicks on a "Funding Route" assignment rule to edit
      And the user changes the "Funding Route" assignment rule details
     Then the company uses the new "Funding Route" rule detail
     
  #@MP7-690
  @notsignedoff 
  Scenario: Delete an existing rule - Non Primary - Site Type
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user clicks on a "Site Type" assignment rule to delete
      And the user clicks delete
     Then the "Site Type" rule detail is deleted
     
  #@MP7-690
  @notsignedoff 
  Scenario: Delete an existing rule - Non Primary - Funding Route
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user clicks on a "Funding Route" assignment rule to delete
      And the user clicks delete
     Then the "Funding Route" rule detail is deleted
     
  #@MP7-629
  @notsignedoff 
  Scenario: Change the company name for an existing rule - Non Primary - Funding Route
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user clicks on a "Funding Route" assignment rule to edit
      And the user changes the "Funding Route" assigned rule company name
     Then the "Funding Route" rule is assigned to the new company
     
  #@MP7-629
  @notsignedoff 
  Scenario: Change the company name for an existing rule - Non Primary - Site Type
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu
     When the user clicks on a "Site Type" assignment rule to edit
      And the user changes the "Site Type" assigned rule company name
     Then the "Site Type" rule is assigned to the new company
     
  #@MP7-702
  @notsignedoff
  Scenario Outline: Rename the company name for an existing rule - Non Primary - "<ruleType>"
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a "<ruleType>" rule type company with "existing orders" is selected for editing
      And the company name is changed
      And the "Save" button is clicked
      And the user confirms the update to the company name
     Then the company is renamed
      And the previous company name is added as a permanent alias
    Examples: 
      | ruleType      | 
      | Funding Route | 
      | Site Type     | 
    
  #@MP7-702
  @notsignedoff
  Scenario: Rename the company name for an existing rule - Primary 
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a primary company is selected for editing
      And the company name is changed
      And the "Save" button is clicked
      And the user confirms the update to the company name
    Then the company is renamed
      And the previous company name is added as a permanent alias
      
  #@MP7-702
  @notsignedoff 
  Scenario: Validate the permanent alias for a company - Primary 
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a primary company is selected for editing
      And the "Aliases" section is selected
     Then the permanent alias is marked with the permanent flag
     
  #@MP7-702
  @notsignedoff 
  Scenario Outline: Validate the permanent alias for an existing rule - "<ruleType>"
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When a "<ruleType>" rule type company with "a permanent alias" is selected for editing
      And the "Aliases" section is selected
     Then the permanent alias is marked with the permanent flag
    Examples: 
      | ruleType      | 
      | Funding Route | 
      | Site Type     |
      
  #@MP7-875
  @notsignedoff 
  Scenario: Validate the primary company is the first shown in the grid
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
     When the user selects "Assignment Rule" from the sub menu
     Then the primary company is the first in the grid

  #@MP7-875
  @notsignedoff 
  Scenario: Reassign the primary company to another active company
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in 
      And "Admin" is selected from the Mercury navigation menu 
      And the "Companies" tile is selected
      And the user selects "Assignment Rule" from the sub menu 
     When the user clicks edit on the primary company
      And the user reassigns the primary company
     Then the "Primary" rule is assigned to the new company