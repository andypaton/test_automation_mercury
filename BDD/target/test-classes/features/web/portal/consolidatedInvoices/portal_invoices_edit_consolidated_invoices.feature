@portal @portal_invoices_consolidated
@toggles @Invoicing @MandatoryInvoiceGeneral @InvoicingLineFulfilled @ConsolidatedInvoicing
@ukrb
Feature: Portal - Invoices - Consolidated Invoices - Edit

Background: 
    Given the system feature toggle "Invoicing" is "enabled" 
      And the system sub feature toggle "MandatoryInvoiceGeneral" is "enabled" 
      And the system sub feature toggle "InvoicingLineFulfilled" is "enabled"
      And the system sub feature toggle "ConsolidatedInvoicing" is "enabled"

      
  #@MP7-706 @MTA-946
  @grid @smoke1
  Scenario: Add Additional Line to "Materials Line"
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user clicks on manage lines
      And a "Materials Line" is added
     When a new line with "Parts" and tax less than "14" percent is added to the "Materials Line"
     Then a line is added to the "Materials Line" invoice lines

  #To find the user with required criteria from the Database, it's mandatory to have the Labour invoice line type is set as Labour type.
  #Please do not switch or replace the the first 2 lines of this scenario.
  #@MP7-706 @MTA-946
  Scenario: Add Additional Line to "Labour Line"
    Given the invoice line type "Labour" is set as labour 
      And a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user clicks on manage lines
      And a "Labour Line" is added
     When a new line with "Labour" and tax less than "14" percent is added to the "Labour Line"
     Then a line is added to the "Labour Line" invoice lines

  #@MP7-8 @MTA-974
  Scenario: Edit an existing additional Line of "Materials Line"
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Materials Line" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user clicks on manage lines
     When the user edits the "Materials Line"
     Then the "Materials Line" line is updated
    
  #To find the user with required criteria from the Database, it's mandatory to have the Labour invoice line type is set as Labour type.
  #Please do not switch or replace the the first 2 lines of this scenario.
  #@MP7-8 @MTA-974
  Scenario: Edit an existing additional Line of "Labour Line"
    Given the invoice line type "Labour" is set as labour
      And a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Labour Line" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user clicks on manage lines
     When the user edits the "Labour Line"
     Then the "Labour Line" line is updated
     
  #@MP7-706 @MTA-961
  Scenario: Delete an existing additional Line from "Materials Line" 
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Materials Line" PPM Jobs
     And the user logs in
     And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     And the order awaiting invoice is searched for and opened
     And the user clicks on manage lines
    When the user deletes a "Materials Line"
    Then the "Materials Line" line is deleted
      
  #To find the user with required criteria from the Database, it's mandatory to have the Labour invoice line type is set as Labour type.
  #Please do not switch or replace the the first 2 lines of this scenario.
  #@MP7-706 @MTA-961
  Scenario: Delete an existing additional Line from "Labour Line" 
    Given the invoice line type "Labour" is set as labour 
     And a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Labour Line" PPM Jobs
     And the user logs in
     And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     And the order awaiting invoice is searched for and opened
     And the user clicks on manage lines
    When the user deletes a "Labour Line"
    Then the "Labour Line" line is deleted