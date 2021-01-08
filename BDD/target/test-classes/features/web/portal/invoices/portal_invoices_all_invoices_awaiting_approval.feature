@portal @portal_invoices @portal_invoices_all_invoices_awaiting_approval
@mcp
Feature: Portal - Invoices - All Invoices Awaiting Approval

  Scenario: Verify as a Accounts payable user the All Invoices Awaiting Approval page displays correctly
    Given a user with profile "Accounts Payable"
     When the user logs in
      And the "All Invoices Awaiting Approval" sub menu is selected from the "Invoices and Credits" top menu
     Then the "All Invoices Awaiting Approval" form displays correctly
  
  Scenario: Verify as a Accounts payable user the All Invoices Awaiting Approval Grid displays the headers and search box correctly
    Given a user with profile "Accounts Payable"
      And the "All Invoices Awaiting Approval" table has data
      And the user logs in
     When the "All Invoices Awaiting Approval" sub menu is selected from the "Invoices and Credits" top menu
     Then the "All Invoices Awaiting Approval" table on the "All Invoices Awaiting Approval" page displays correctly
      And a search box is present on the "All Invoices Awaiting Approval" page
      
  Scenario: Verify as a Accounts payable user the All Invoices Awaiting Approval Grid displays the data correctly
    Given a user with profile "Accounts Payable"
      And the "All Invoices Awaiting Approval" table has data
      And the user logs in
     When the "All Invoices Awaiting Approval" sub menu is selected from the "Invoices and Credits" top menu
     Then the "All Invoices Awaiting Approval" table on the "All Invoices Awaiting Approval" page displays the latest job row correctly
