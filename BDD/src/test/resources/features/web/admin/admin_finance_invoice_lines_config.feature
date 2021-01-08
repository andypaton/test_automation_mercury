@admin @admin_finance
@toggles @Invoicing @MandatoryInvoiceGeneral
@ukrb @usad
Feature: Admin - Finance - Invoice line configuration

Background:
   Given the system feature toggle "Invoicing" is "enabled"
      And the system sub feature toggle "MandatoryInvoiceGeneral" is "enabled"
   
  #@MP7-10
  Scenario: Set mandatory invoice lines to active
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Finance" tile is selected
      And the user selects "Invoice Line Types" from the sub menu 
     When the "mandatory" invoice line types are set to "active"
      And the "Save" button is clicked
     Then the "mandatory" Invoice Line types are stored as "active" in the database 
         
  #@MP7-10
  Scenario: Set mandatory invoice lines to inactive
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Finance" tile is selected
      And the user selects "Invoice Line Types" from the sub menu 
     When the "mandatory" invoice line types are set to "inactive"
      And the "Save" button is clicked     
     Then the "mandatory" Invoice Line types are stored as "inactive" in the database
     
  #@MP7-423
  Scenario: Set labour invoice lines to active
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Finance" tile is selected
      And the user selects "Invoice Line Types" from the sub menu 
     When the "labour" invoice line types are set to "active"
      And the "Save" button is clicked     
     Then the "Labour" Invoice Line types are stored as "active" in the database
     
  #@MP7-423
  Scenario: Set labour invoice lines to inactive
    Given a user with "Mercury_Admin_Finance_Configuration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Finance" tile is selected
      And the user selects "Invoice Line Types" from the sub menu 
     When the "labour" invoice line types are set to "inactive"
      And the "Save" button is clicked     
     Then the "Labour" Invoice Line types are stored as "inactive" in the database