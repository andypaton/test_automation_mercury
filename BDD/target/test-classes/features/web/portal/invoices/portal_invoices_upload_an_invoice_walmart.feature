@portal @portal_invoices @portal_invoices_upload
@toggles @Invoicing @InvoicingLineFulfilled @ConsolidatedInvoicing
Feature: Portal - Invoices - Upload an invoice

#Background: 
 #   Given the system feature toggle "Invoicing" is "disabled" 
  #    And the system sub feature toggle "InvoicingLineFulfilled" is "disabled" 
   #   And the system sub feature toggle "ConsolidatedInvoicing" is "disabled"
  
  @uswm
  Scenario: Upload and submit an Invoice to an order awaiting invoice with "Contractor Admin" profile and no existing invoices
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user is taken to the upload documents page
     When an invoice is uploaded with job sheet to the order with tax amount "0.00"
      And a new invoice is added to the invoice page
     Then a new line of "random" type is added to the invoice
      And the user submits the invoice
  
  @uswm
  Scenario: Check validation for an invoice without an invoice line for an order awaiting invoice with Contractor Admin profile
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user is taken to the upload documents page
     When an invoice is uploaded with job sheet to the order with tax amount "0.00"
      And the "Submit Invoice" button is clicked
     Then the following error is displayed: "The total net value of the lines should equal the net value of the invoice header"
   
  @uswm @usad
  Scenario: Check validation for an invoice without an invoice line for an order awaiting invoice with Supply Only profile
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user is taken to the upload documents page
     When an invoice is uploaded with job sheet to the order with tax amount "0.00"
      And the "Submit Invoice" button is clicked
     Then the following error is displayed: "The total net value of the lines should equal the net value of the invoice header"
  
  @uswm
  Scenario: Delete an invoice from an order awaiting invoice with "Contractor Admin" profile and existing invoices
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice without documents" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
     When the user has deleted an invoice
     Then there is no invoice present on the invoice page
  
  @uswm
  Scenario: Delete a job sheet from an order awaiting invoice with "Contractor Admin" profile and existing invoices
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice without documents" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
     When the user has deleted a job sheet
     Then there is no job sheet present on the invoice page
  
  @uswm
  Scenario: Validate the tax amount for the new line of an invoice above the allowed percent is not accepted
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
     When a user attempts to add a new line of "Diesel" type with a tax amount greater than standard rate
     Then the following error is displayed: "The unit tax should not be more than the allowed % of the unit net value"
  
  @uswm
  Scenario: Validate the input fields for the new line of an invoice modal
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
     When the user select add new line to invoice
     Then the user is not able to add the invoice line without completing the compulsory fields
  
  @uswm
  Scenario: Check validation for an invoice being uploaded by a Contractor Admin profile without specifying a file location
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice without documents" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user deletes an invoice if it exists
     When the user uploads an invoice without a document
     Then an upload invoice error message is displayed
  
  @uswm
  Scenario: Check validation for a job sheet being uploaded by a Contractor Admin profile without specifying a file location
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice without documents" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user deletes a job sheet if it exists
     When the user uploads a job sheet without a document
     Then an upload invoice error message is displayed
  
  @uswm
  Scenario: Check validation for the tax amount on an invoice being uploaded
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice without documents" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user uploads a valid invoice
     When the user attempts to add a tax amount greater than standard rate
     Then the following error is displayed: "The invoice tax amount can't be more than the allowed % of the invoice net value"
  
  @uswm @usad
  Scenario: Upload and submit an Invoice to an order awaiting invoice with supplier profile and no existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user is taken to the upload documents page
      And the user is able to upload an invoice to the order
     When an order line is added to the uploaded invoice and submitted
     Then the user is taken to the invoice submitted page
  
  @uswm
  Scenario: Upload an OCR invoice with correct information - job status Complete
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the user uploads and saves an OCR invoice that has a tax amount "0.00"
     Then the user is taken to the invoice page
  
  @uswm @usad
  Scenario: Upload an OCR invoice with correct information - job status In Progress
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the user uploads and saves an OCR invoice that has a tax amount "0.00"
     Then the user is taken to the invoice page

  @uswm @usad
  Scenario: Upload, edit and submit an Invoice to an order awaiting invoice with supplier profile and no existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user is taken to the upload documents page
      And the user uploads a correct OCR invoice to the order with a tax amount "0.00"
     When the order line is added to the invoice
      And the total net value of the order lines equals the invoice net value
      And the user submits the invoice
     Then the invoice status is set to "Awaiting AP Review"
  
  @uswm @smoke1 
  Scenario: Upload, edit and submit an Invoice to an order awaiting invoice with contractor profile and no existing invoices
        * using dataset "portal_invoices_upload_001"
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the user uploads and saves an OCR invoice that has a tax amount "0.00"
      And a new line of "Parts" type is added to the invoice
      And the user submits the invoice
     Then the user will be presented with the submitted invoice details 
      And the invoice status is set to "Awaiting AP Review"
 
  @uswm @usad 
  Scenario: Check validation for the tax amount on an invoice being uploaded as a supplier
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads an OCR invoice
      And an invoice number is entered
      And an Invoice date is entered which is after the Job date
     When the user attempts to add a tax amount greater than standard rate
      And the "Save Changes" button is clicked
     Then the following invoice tax error message is displayed: "The invoice tax amount can't be more than the allowed % of the invoice net value"
  
  @uswm @usad @wip
  Scenario: Delete an invoice document for an order awaiting invoice with supplier profile and has existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
     When the user has deleted a supplier invoice
     Then there is no invoice present on the invoice page
  
  @uswm @usad
  Scenario: Check validation for the tax amount on the order line modal as a supplier
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user is taken to the upload documents page
      And the user is able to upload an invoice to the order
     When an order line with a tax amount greater than standard rate is added
     Then the following error is displayed: "The unit tax amount can't be more than the allowed % of the unit price"
  
  @uswm @usad
  Scenario: Delete an Order Line to an order awaiting invoice with supplier profile and no existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
     When an invoice is uploaded and an order line is added
      And the user deletes an order line
     Then there is no order line present on the invoice
  
  @uswm
  Scenario: Validate that invoice details can be corrected if the OCR review displays incorrect information - Contractor Admin profile
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs  
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the user uploads incorrect OCR Documents
     Then the invoice details "can be" corrected
     
  @uswm @usad
  Scenario: Validate that invoice details can be corrected if the OCR review displays incorrect information - Supply Only profile
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs  
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the user uploads incorrect OCR Documents
     Then the invoice details "can be" corrected

  @uswm
  Scenario Outline: Validate that invoice details page is shown when the invoice amount is "<Invoice Amount>" the authorised amount
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads incorrect OCR Documents
      And the invoice details "are" corrected
      And the invoice amount is "<Invoice Amount>" the authorised amount
     When the "Save" button is clicked
      And a new line type with net value equal to order value is added to the invoice
     Then the user selects the submit button
      And the invoice status is set to one of "Awaiting AP Review/Awaiting Approval"
    Examples: 
      | Invoice Amount | 
      | less than      | 
      | equal to       | 
  
  @uswm
  Scenario: Validate that an error message is shown when the invoice amount is greater than the authorised amount - Contractor Admin profile
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads incorrect OCR Documents
      And the invoice details "are" corrected
     When the invoice amount is "greater than" the authorised amount
      And the "Save" button is clicked
     Then the following error is displayed: "The invoice net value can't be greater than order value"
     
  @uswm @usad
  Scenario: Validate that an error message is shown when the invoice amount is greater than the authorised amount - Supply Only profile 
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads incorrect OCR Documents
      And the invoice details "are" corrected
     When the invoice amount is "greater than" the authorised amount
      And the "Save" button is clicked
     Then the following error is displayed: "The invoice net value can't be greater than order value"
 
  @uswm
  Scenario: Verify that the user can return back to the orders awaiting invoice screen without submitting an invoice
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads incorrect OCR Documents
      And the invoice details "are" corrected
      And the invoice amount is "equal to" the authorised amount
      And the "Save" button is clicked
      And a new line type with net value equal to order value is added to the invoice
     When the user clicks the back button without submitting the invoice
     Then the orders awaiting invoice page is displayed
   
  @uswm @usad  
  Scenario: Verify that a "Supply Only" user can return back to the orders awaiting invoice screen without submitting an invoice
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads incorrect OCR Documents
      And the invoice details "are" corrected
      And the invoice amount is "equal to" the authorised amount
      And the "Save" button is clicked
      And the order line is added to the invoice
     When the user clicks the back button without submitting the invoice
     Then the orders awaiting invoice page is displayed
  
  @uswm @usad   
  Scenario: Verify that an error message is displayed when the sum of values from the lines do not match net invoice value - "Supply Only" profile  
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads incorrect OCR Documents
      And the invoice details "are" corrected
      And the invoice amount is "equal to" the authorised amount
      And the "Save" button is clicked
     When an order line with net value less than order value is added to the invoice
      And the user selects the submit button
     Then the following error is displayed: "The total net value of the lines should equal the net value of the invoice header"
  
  @uswm   
  Scenario: Verify that an error message is displayed when the sum of values from the lines do not match net invoice value
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the user uploads incorrect OCR Documents
      And the invoice details "are" corrected
      And the invoice amount is "equal to" the authorised amount
      And the "Save" button is clicked
     When a new line type with net value less than order value is added to the invoice
      And the user selects the submit button
     Then the following error is displayed: "The total net value of the lines should equal the net value of the invoice header"
