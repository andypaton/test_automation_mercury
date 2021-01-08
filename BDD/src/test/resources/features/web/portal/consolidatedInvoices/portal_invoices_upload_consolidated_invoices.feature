@portal @portal_invoices_consolidated @portal_invoices_upload_consolidated
@toggles @Invoicing @MandatoryInvoiceGeneral @InvoicingLineFulfilled @ConsolidatedInvoicing
@ukrb
Feature: Portal - Invoices - Consolidated Invoices - Upload

Background: 
    Given the system feature toggle "Invoicing" is "enabled" 
      And the system sub feature toggle "MandatoryInvoiceGeneral" is "enabled" 
      And the system sub feature toggle "InvoicingLineFulfilled" is "enabled"
      And the system sub feature toggle "ConsolidatedInvoicing" is "enabled"
      
  Scenario: Upload and submit an Invoice to an order awaiting invoice with supplier profile - no existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
      And the user is taken to the upload documents page
      And the user is able to upload an invoice to the order with a "Company" legal entity 
     When an order line is added to the uploaded invoice and submitted
     Then the user is taken to the invoice submitted page
     
  #@MP7-1028 
  #marking this scenario wip, need to discuss the current functionality and rewrite. 
  @wip
  Scenario: Upload and submit an OCR Invoice to a PPM order awaiting invoice with contractor profile and existing invoices - read from ocr
    Given a portal user with a "Contractor Admin" profile and with "Complete / PPM Orders Awaiting Invoice With Existing Invoice and Documents" PPM Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is created and uploaded for the assigned company
      And the invoice template is updated with the information fields
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user deletes the invoice documents
      And the "Upload Invoice" button is clicked
     When the user uploads and saves an OCR invoice
      And a "Materials" line is added to the invoice
      And the "Submit Invoice" button is clicked
     Then the user will be presented with the submitted invoice details
     
  #@MP7-1028 
  @wip
  Scenario: Upload and submit an OCR Invoice to an order awaiting invoice with supplier profile and no existing invoices - read from ocr
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is created and uploaded for the assigned company
      And the invoice template is updated with the information fields
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
      And the user is taken to the upload documents page
     When the user uploads a "Supply Only" ocr invoice to the order
     Then the "invoice" page is displayed