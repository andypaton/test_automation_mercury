@portal @portal_invoices_consolidated @portal_invoices_validate
@toggles @Invoicing @ConsolidatedInvoicing
@ukrb
Feature: Portal - Invoices - Consolidated Invoices - Validate

#Background: 
#    Given the system feature toggle "Invoicing" is "enabled" 
#      And the system sub feature toggle "MandatoryInvoiceGeneral" is "enabled" 
#      And the system sub feature toggle "InvoicingLineFulfilled" is "enabled"
#      And the system sub feature toggle "ConsolidatedInvoicing" is "enabled"
#      And the system sub feature toggle "LegalEntityAssignment and Validation" is "enabled"

  Scenario: Validate the invoice lines are shown as Fullfiled - no existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
      And the user is taken to the upload documents page
      And the user is able to upload an invoice to the order
     When the order line is added to the invoice  
      And the user clicks add order line to invoice
     Then the invoice line is shown as Fullfilled 
    
  Scenario: Validate the invoice lines are shown as Fullfiled - existing invoices
     Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
     When the order line is added to the invoice 
      And the user clicks add order line to invoice 
     Then the invoice line is shown as Fullfilled
  
  Scenario: Validate the legal entity text box shows the correct message
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
      And the user is taken to the upload documents page
     When the user uploads invoice documents
      And the Invoice Details page is displayed
     Then the legal entity text box shows "Enter the name of the City company you are invoicing"
  
  Scenario: Validate the incorrect legal entity against alias and company name - Supplier - no existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
      And the user is taken to the upload documents page
     When the user upload an invoice to an order with an incorrect legal entity
     Then the following error is displayed: "The Legal Entity listed for the Invoice does not match the order, please resubmit your invoice with the City company name as shown in our order"
   
  Scenario: Validate the incorrect legal entity against alias and company name - Supplier - existing invoices 
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
     When the user edits the invoice with an incorrect legal entity
     Then the following error is displayed: "The Legal Entity listed for the Invoice does not match the order, please resubmit your invoice with the City company name as shown in our order"
   
  Scenario Outline: Validate the correct legal entity against "<entityType>" name - Supplier - existing invoices 
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
     When the user edits the invoice with a correct legal entity against the "<entityType>" name
     Then the "invoice" page is displayed
    Examples: 
      | entityType | 
      | Company    | 
      | Alias      |     

  Scenario: Validate the edit invoice line modal has the correct order
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
     When the order line is added to the invoice
      And the user clicks edit on an order line
     Then the edit invoice line modal has the correct order

  Scenario: Validate the invoice can not be updated with a quantity higher than that has been invoiced
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
     When the order line is added to the invoice
      And the user clicks edit on an order line
      And the user updates the order with an incorrect invoice quantity
     Then the following error is displayed: "Quantity can not be greater than the quantity available"

  @notsignedoff
  Scenario: Validate the correct legal entity - Contractor Admin - existing invoices 
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Consolidated Invoice" button is clicked
      And an invoice document is uploaded
     When the user enters the invoice details with correct legal entity 
     Then the "invoice" page is displayed
  
  @notsignedoff
  Scenario: Validate the incorrect legal entity against alias and company name - Contractor Admin - no existing invoices
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With No Invoice" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Consolidated Invoice" button is clicked
      And an invoice document is uploaded
     When the user enters the invoice details with incorrect legal entity 
     Then the following error is displayed: "The Legal Entity selected is not valid"
  
  @notsignedoff
  Scenario: Validate the legal entity section shows the correct message
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With No Invoice" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Consolidated Invoice" button is clicked
      And an invoice document is uploaded
     When the Invoice page is displayed
     Then the legal entity section shows "Select the name of the City company you are invoicing"
 
  Scenario: Validate the consolidated invoice button is visible for a contractor admin
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
     When the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     Then the "consolidated invoice" button is visible
 
  Scenario: Validate the message is presented when a contractor admin clicks consolidated invoice - no PPM jobs
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the "Consolidated Invoice" button is clicked
     Then the message "There are no PPM orders available to consolidate" is displayed
 
  @notsignedoff
  Scenario: Validate create consolidated invoice page is displayed to Contractor Admin with PPM orders awaiting invoice 
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With No Invoice" PPM Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the "Consolidated Invoice" button is clicked
     Then the "Upload Invoice Documents" page is displayed

  @notsignedoff
  Scenario: Validate the manage lines button is displayed
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Consolidated Invoices In Progress" sub menu is selected from the "Invoices and Credits" top menu
     When the invoice in progress is searched for and opened
     Then the Manage Lines link is displayed on the page
    
  @notsignedoff
  Scenario: Validate the manage lines modal is displayed
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Consolidated Invoices In Progress" sub menu is selected from the "Invoices and Credits" top menu
      And the invoice in progress is searched for and opened
     When the user clicks on manage lines link
     Then the "Manage Lines" modal is displayed
      And the Save button is disabled
     
  @notsignedoff
  Scenario: Validate the correct message is displayed to Contractor Admin with no consolidated invoices in progress
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders With No Invoice In Progress" PPM Jobs
      And the user logs in
     When the "Consolidated Invoices In Progress" sub menu is selected from the "Invoices and Credits" top menu
     Then the message "There are no consolidated invoices currently in Progress" is displayed
      And the "Back" button is displayed on the page
      
  @grid @notsignedoff
  Scenario: Validate the user can add PPM order lines to the invoice
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Consolidated Invoices In Progress" sub menu is selected from the "Invoices and Credits" top menu
     When the invoice in progress is searched for and opened
      And ppm order lines are added to the invoice
     Then the ppm orders are displayed on the consolidated invoice
     
  @wip @notsignedoff
  Scenario: Validate the user can click the Cancel button when adding PPM order lines to the invoice
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Consolidated Invoices In Progress" sub menu is selected from the "Invoices and Credits" top menu
     When the invoice in progress is searched for and opened
      And ppm order lines are added to the invoice
      And the "Cancel" button is clicked
     Then the ppm orders are not displayed on the consolidated invoice
     
  @wip @notsignedoff
  Scenario: Validate the user can delete a PPM order line from the consolidated invoice
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Consolidated Invoices In Progress" sub menu is selected from the "Invoices and Credits" top menu
      And the invoice in progress is searched for and opened
      And ppm order lines are added to the invoice
     When an order line is deleted from the invoice
     Then the order is not displayed on the consolidated invoice
     
  @wip @notsignedoff
  Scenario: Validate the user can click the Cancel button when deleting a PPM order line from the invoice
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice" PPM Jobs
      And the user logs in
      And the "Consolidated Invoices In Progress" sub menu is selected from the "Invoices and Credits" top menu
      And the invoice in progress is searched for and opened
      And ppm order lines are added to the invoice
     When an order line is deleted from the invoice
      And the "Cancel" button is clicked
     Then the order is displayed on the consolidated invoice
     
  @notsignedoff
  Scenario: Contractor Admin - Consolidated Invoice Menu Option
     Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With No Invoice" PPM Jobs
      When the user logs in
      Then the "Invoices and Credits" top menu contains following sub menu options
       | All Orders, Orders Awaiting Invoice, Orders Not Invoiced, Submitted Invoices and Credits, Invoices Template, Create Consolidated Invoice, Consolidated Invoices In Progress |