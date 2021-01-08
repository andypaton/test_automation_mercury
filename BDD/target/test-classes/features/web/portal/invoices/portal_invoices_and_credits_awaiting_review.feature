@portal @portal_invoices @portal_invoices_awaiting_review
@mcp
Feature: Portal - Invoices - Invoices And Credits Awaiting Review

  Scenario: Verify as a Accounts payable user the Invoices And Credits Awaiting Review page displays correctly
    Given a user with profile "Accounts Payable"
     When the user logs in
      And the "Invoices & Credits Awaiting Review" sub menu is selected from the "Invoices and Credits" top menu
     Then the "Invoices & Credits Awaiting Review" form displays correctly
  
  Scenario: Verify as a Accounts payable user the Invoices And Credits Awaiting Review Grid displays the headers and search box correctly
    Given a user with profile "Accounts Payable"
      And the "Invoices & Credits Awaiting Review" table has data
      And the user logs in
     When the "Invoices & Credits Awaiting Review" sub menu is selected from the "Invoices and Credits" top menu
     Then the "Invoices & Credits Awaiting Review" table on the "Invoices & Credits Awaiting Review" page displays correctly
      And a search box is present on the "Invoices & Credits Awaiting Review" page
      
  Scenario: Verify as a Accounts payable user the Invoices And Credits Awaiting Review Grid displays the data correctly
    Given a user with profile "Accounts Payable"
      And the "Invoices & Credits Awaiting Review" table has data
      And the user logs in
     When the "Invoices & Credits Awaiting Review" sub menu is selected from the "Invoices and Credits" top menu
     Then the "Invoices & Credits Awaiting Review" table on the "Invoices & Credits Awaiting Review" page displays the latest job row correctly
     