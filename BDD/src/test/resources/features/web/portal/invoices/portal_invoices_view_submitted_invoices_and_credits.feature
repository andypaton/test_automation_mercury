@portal @portal_invoices @portal_invoices_view_submitted_invoices_and_credits
@mcp @wip
Feature: Portal - Invoices - View Submitted Invoices and Credits

  Scenario: Verify as "Accounts Payable" profile and existing invoices
    Given a user with profile "Accounts Payable"
      And the "PAGELOAD_TIMEOUT" timeout is set to "60" seconds
      And the user logs in
     When the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu 
      And the user selects an invoice from the Submitted Invoices and Credits grid view