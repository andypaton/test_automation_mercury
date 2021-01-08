@portal @portal_invoices @portal_invoices_all_orders
@mcp
Feature: Portal - Invoices - All Orders

  Scenario: Verify as a Supply Only user the All Orders page displays the headers and search box correctly
    Given a user with profile "Supply Only" and with Submitted Invoices and Credits
      And the user logs in
     When the "All Orders" sub menu is selected from the "Orders" top menu
      And a search box is present on the "All Orders" page
      And a search is run on "All Orders" table for "Order Ref"
     Then the "All Orders" table on the "All Orders" page displays correctly   
      And the show entries dropdown is displayed on the "All Orders" page
      And the grid page navigation buttons are displayed on the "All Orders" page
      
  Scenario: Verify a Supply Only user can click the Back radio button on the All Orders page
    Given a user with profile "Supply Only" and with Submitted Invoices and Credits
      And the user logs in
     When the "All Orders" sub menu is selected from the "Orders" top menu
      And the "All Orders" page is displayed
      And the "Back" button is clicked
     Then the "Home" page is displayed
     
  Scenario: Verify as a Supply Only user the All Orders Grid displays the data correctly
    Given a user with profile "Supply Only" and with Submitted Invoices and Credits
      And the user logs in
     When the "All Orders" sub menu is selected from the "Orders" top menu
      And a search box is present on the "All Orders" page
     Then the "All Orders" table on the "All Orders" page displays each row correctly