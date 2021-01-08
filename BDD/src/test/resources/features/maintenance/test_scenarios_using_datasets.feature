@wip
Feature: test scenarios using data sets

  Scenario Outline: Upload and submit an Invoice to an order awaiting invoice for "<type>" line with "Contractor Admin" profile and no existing invoices
    Given using dataset "portal_invoices_upload_001"
      And a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the order awaiting invoice is searched for and opened
      And the user is taken to the upload documents page
     When an invoice is uploaded with job sheet to the order with tax amount "0.00"
      And a "<type>" line is added to the invoice
      And the "Submit Invoice" button is clicked
     Then the user will be presented with the submitted invoice details
      And the invoice status is set to "Awaiting AP Review"
    Examples: 
      | type        | 
      | Materials   |  