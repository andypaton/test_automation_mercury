@portal @portal_invoices @portal_invoices_view_order_awaiting_invoice
@mcp
Feature: Portal - Invoices - View an order awaiting invoice

  Scenario: View an order awaiting invoice with "Contractor Admin" profile and existing invoices
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the order awaiting invoice is searched for and opened
     Then the user is taken to the invoice page

  Scenario: View an order awaiting invoice with "Contractor Admin" profile and no existing invoices
    Given a portal user with a "Contractor Admin" profile and with "Complete / Orders Awaiting Invoice With No Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
     When the order awaiting invoice is searched for and opened
     Then the user is taken to the upload documents page