@portal @portal_invoices @portal_invoices_submitted
@mcp
Feature: Portal - Invoices - Submitted Invoices and Credits

  Background: Set page timeout to compensate for bug MCP-7340
    Given the "PAGELOAD_TIMEOUT" timeout is set to "60" seconds

  Scenario: Verify as a Accounts payable user the Submitted Invoices and Credits page displays correctly
    Given a user with profile "Accounts Payable"
     When the user logs in
      And the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
     Then the "Submitted Invoices and Credits" form displays correctly
  
  Scenario: Verify as a Accounts payable user the Submitted Invoices and Credits Grid displays the headers and search box correctly
    Given a user with profile "Accounts Payable"
      And the user logs in
     When the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
      And a search is run on "Submitted Invoices and Credits" table for "OrderRef"
     Then the "Submitted Invoices and Credits" table on the "Submitted Invoices and Credits" page displays correctly
      And a search box is present on the "Submitted Invoices and Credits" page
  
  Scenario: Verify as a Accounts payable user the Submitted Invoices and Credits Grid displays the data correctly
    Given a user with profile "Accounts Payable"
      And the user logs in
     When the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
      And a search is run on "Submitted Invoices and Credits" table for "OrderRef"
     Then the "Submitted Invoices and Credits" table on the "Submitted Invoices and Credits" page displays each row correctly

  Scenario: Verify as a Accounts payable user the Submitted Invoices and Credits form elements are displayed
    Given a user with profile "Accounts Payable"
    And the "PAGELOAD_TIMEOUT" timeout is set to "60" seconds
     When the user logs in
      And the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
     Then the "Date From", "Date Until" calendar buttons, "Supplier" dropdown and "Find" button are displayed 
      And the grid page navigation buttons are displayed on the "Submitted Invoices and Credits" page

  Scenario: Verify as a Supply Only user the Submitted Invoices and Credits Grid displays the headers and search box correctly
    Given a user with profile "Supply Only" and with Submitted Invoices and Credits
      And the user logs in
     When the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
      And a search box is present on the "Submitted Invoices and Credits" page
      And a search is run on "Submitted Invoices and Credits" table for "Order Reference"
     Then the "Submitted Invoices and Credits" table on the "Submitted Invoices and Credits" page displays correctly
  
  Scenario: Verify as a Supply Only user the Submitted Invoices and Credits form elements are displayed
    Given a user with profile "Supply Only" and with Submitted Invoices and Credits
      And the user logs in
     When the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
     Then the "Submitted Invoices and Credits" table on the "Submitted Invoices and Credits" page displays correctly
      And the grid page navigation buttons are displayed on the "Submitted Invoices and Credits" page
      And the show entries dropdown is displayed on the "Submitted Invoices and Credits" page
  
  Scenario: Verify as a Supply Only user the Submitted Invoices and Credits Grid displays the data correctly
    Given a user with profile "Supply Only" and with Submitted Invoices and Credits
      And the user logs in
     When the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
      And a search box is present on the "Submitted Invoices and Credits" page
     Then the "Submitted Invoices and Credits" table on the "Submitted Invoices and Credits" page displays each row correctly
  
  Scenario: Verify as a Accounts payable user the Submitted Invoices and Credits pagination elements are displayed
    Given a user with profile "Accounts Payable"
      And the "PAGELOAD_TIMEOUT" timeout is set to "60" seconds
      And the user logs in
      And the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu
     When the "Back" button is clicked
     Then the "Home" page is displayed
    
  Scenario: Verify that Accounts payable user can view an invoice pdf
    Given a user with profile "Accounts Payable"
      And the "PAGELOAD_TIMEOUT" timeout is set to "60" seconds
      And the user logs in
     When the "Submitted Invoices and Credits" sub menu is selected from the "Invoices and Credits" top menu 
      And the user selects an invoice from the Submitted Invoices and Credits grid view
     Then the PDF view of invoice is displayed