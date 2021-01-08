@portal @portal_invoices @portal_invoices_reject
@mcp
Feature: Portal - Invoices - Reject an invoice awaiting approval

  Scenario: Reject an invoice awaiting approval with "RFM" profile
    Given a portal user with a "RFM" profile and with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the user rejects an invoice
     Then the invoice status is set to Rejected

  Scenario: Validate the Save button on Reject invoice modal is disabled when mandatory fields are not entered
    Given a portal user with a "RFM" profile and with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the user selects reject on the invoice
     Then the Save button on the reject invoice modal is disabled
      And the user has option to cancel the rejection

  Scenario: Validate reject reason dropdown list 
    Given a portal user with a "RFM" profile and with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the user selects reject on the invoice
     Then the rejection reason list is validated
       