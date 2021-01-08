@portal @portal_invoices @portal_invoices_ocr_template
@mcp
Feature: Portal - Invoices - OCR Template

  Scenario: Enter all Invoice Template Details with Tax Amount
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
     When an OCR invoice template is uploaded
      And Legal Entity details are selected
      And Invoice Number details are selected
      And Invoice Date details are selected
      And Net Amount details are selected
      And Tax Amount details are selected
      And Gross Amount details are selected
     Then the following alert is displayed: "All of the required values have been provided. You can now save your template"
      And the Save Template button is displayed
   
  Scenario: Exempt Tax Amount
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
     When an OCR invoice template is uploaded
      And Legal Entity details are selected
      And Invoice Number details are selected
      And Invoice Date details are selected
      And Net Amount details are selected
      And Tax Amount details are exempt
      And Gross Amount details are selected
     Then the following alert is displayed: "All of the required values have been provided. You can now save your template"
      And the Save Template button is displayed
  
  Scenario: Save Invoice Template
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
     When an OCR invoice template is uploaded
      And Legal Entity details are selected
      And Invoice Number details are selected
      And Invoice Date details are selected
      And Net Amount details are selected
      And Tax Amount details are exempt
      And Gross Amount details are selected
      And the Invoice Template is saved
     Then the following toast message is displayed: "Your Invoice Template was successfully saved and is now ready to use."
  
  Scenario Outline: Delete an OCR template with "<Profile>" profile
    Given a portal user with a "<Profile>" profile and with "<Jobs>" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
     When an OCR invoice template is uploaded
     Then the user is able to delete the uploaded template
    Examples:
      | Profile          | Jobs                                                             |
      | Contractor Admin | Complete / Orders Awaiting Invoice With Existing Invoice         |
      | Supply Only      | In Progress / Parts Requested / Awaiting Invoice With No Invoice |
  
  Scenario: Validate an OCR template is not saved without entering template fields
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
     When an OCR invoice template is uploaded
     Then the save template button is disabled
  
  Scenario Outline: Validate no incorrect information is submitted for an OCR invoice with "<Profile>" profile
    Given a portal user with a "<Profile>" profile and with "<Jobs>" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
      And an OCR invoice template is uploaded
      And the invoice template is updated with the information fields with tax amount
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the user uploads incorrect OCR Documents
      And the "Save" button is clicked
     Then the following error is displayed: "Invoice Number is required"
      And the following error is displayed: "Net Amount must be greater than zero"   
    Examples:
      | Profile          | Jobs                                                             |
      | Contractor Admin | Complete / Orders Awaiting Invoice With No Invoice               |
      | Supply Only      | In Progress / Parts Requested / Awaiting Invoice With No Invoice |
  
  Scenario: Upload and Save an OCR template with "Supply Only" profile
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Invoices Template" sub menu is selected from the "Invoices and Credits" top menu
     When an OCR invoice template is uploaded
      And Legal Entity details are selected
      And Invoice Number details are selected
      And Invoice Date details are selected
      And Net Amount details are selected
      And Tax Amount details are exempt
      And Gross Amount details are selected
      And the following alert is displayed: "All of the required values have been provided. You can now save your template"
      And the Invoice Template is saved
     Then the following toast message is displayed: "Your Invoice Template was successfully saved and is now ready to use."
  
