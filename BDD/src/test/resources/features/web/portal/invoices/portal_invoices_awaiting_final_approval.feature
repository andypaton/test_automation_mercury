@portal @portal_invoices @portal_invoices_awaiting_final_approval
Feature: Portal - Invoices - Invoices Awaiting Final Approval

  # Invoices do NOT go to 'Awaiting Final Approval' state on UKRB
  
  @uswm @usad @notsignedoff @bugWalmart
  Scenario: Invoices Awaiting Final Approval table actions [bug: MCP-17941]
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
     When the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
     Then the "Invoices Awaiting Final Approval" table is displayed
      And the "Invoices Awaiting Final Approval" table can be sorted on all columns
      And a search can be run on the "Invoices Awaiting Final Approval" table
     
  @uswm @usad   
  Scenario: View all Invoices Awaiting Final Approval
    Given a final approver with invoices awaiting final approval
      And the user logs in
     When the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
     Then the "Invoices Awaiting Final Approval" table on the "Invoices Awaiting Final Approval" page displays expected headers
      And the "Invoices Awaiting Final Approval" table on the "Invoices Awaiting Final Approval" page displays each row correctly
               
  @uswm @usad @bugWalmart
  Scenario: Final Approver views an Invoice Awaiting Final Approval [bugs: MCP-8898 - uat_uswm, MCP-16221 - test_uswm, MCP-16261 - test_uswm]
    Given a final approver with invoices awaiting final approval
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Final Approval"
     Then the invoice funding route is displayed at the top right hand corner of the grid
      And the following Invoice Final Approval tabs are displayed "Invoice, Closedown, Job Notes, Uplifts, Job Costs"
      And the "Invoice" tab on the Invoice Final Approval page displays "Approver 1, Original Budget, Extreme Weather, Order Ref, Order Value ($), Job Ref, Logged Date, Site, Inv Num, Inv Date, Supplier, Net ($), Tax ($), Gross ($), Total Job Cost ($)"
      And the "Closedown" tab on the Invoice Final Approval page displays "Job Type, First Arrival Date/Time, Job Completed Date/Time, Total Onsite Hours, Supplier Notes, Arrival Date/Time, Onsite Hours"
      And the "Job Notes" tab on the Invoice Final Approval page displays "Job Description, Job Closedown"
      And the "Uplifts" tab on the Invoice Final Approval page displays "Any uplifts attached to the job"
      And the "Job Costs" tab on the Invoice Final Approval page displays "Total Job Cost, PO Number, PO Type, Amount, Budget, Status"
      And Documents to Check are displayed
      And the Approve button is not visible until 'Next >>' is clicked

  @uswm @usad
  Scenario: Final Approver views Invoice Lines
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the View Invoice Lines button is clicked
     Then the Invoice Lines table is displayed
      
  @uswm @usad    
  Scenario: Final Approver cancels invoice rejection
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the user selects reject on the invoice awaiting final approval
     Then the user has option to cancel the rejection
      And the Invoice Final Approval page is displayed
      
  @uswm @usad    
  Scenario: Final Approver rejects an Invoice Awaiting Final Approval
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the Invoice Final Approval is rejected
     Then the invoice status is set to Awaiting Approval
      And the invoice is displayed in red on the RFMs Invoice Approval table
      
  @wip @uswm @usad @notsignedoff
  Scenario: Final Approver approves a previously rejected Invoice Awaiting Final Approval
    Given a final approver and with a previously rejected invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the "Approve" button is clicked
      And the invoice funding approval Confirm button is clicked
     Then the Invoices Awaiting Final Approval table is displayed
      And the invoice is not displayed on the Invoices Awaiting Final Approval table
     
  @uswm
  Scenario Outline: Final Approver approves a "<FUNDING_ROUTE>" invoice
    Given a final approver and with an invoice awaiting final approval for "<FUNDING_ROUTE>" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the "Approve" button is clicked
      And the invoice funding approval Confirm button is clicked
     Then the Invoices Awaiting Final Approval table is displayed
      And the invoice is not displayed on the Invoices Awaiting Final Approval table
    Examples:
      | FUNDING_ROUTE |
      | OOC           |
      | BMI           |
      
  @uswm @usad
  Scenario: Final Approver approves a "CAPEX" invoice
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the "Approve" button is clicked
      And the invoice funding approval Confirm button is clicked
     Then the Invoices Awaiting Final Approval table is displayed
      And the invoice is not displayed on the Invoices Awaiting Final Approval table
  
  @uswm @usad
  Scenario: Final approver cancels approving an invoice awaiting final approval
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Final Approval"
      And the user checks the documents attached and selects approve on the Invoice Final Approval page
      And the invoice funding approval Back button is clicked
     Then the Invoice Final Approval page is displayed
  
  @uswm
  Scenario Outline: Final Approver changes invoice funding route "<FROM>" to "<TO>"
    Given a final approver and with an invoice awaiting final approval for "<FROM>" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the "Approve" button is clicked
      And the invoice funding route is changed to "<TO>" 
      And the reason for funding route change is entered
      And the Extreme Weather question is answered
      And the invoice funding approval Confirm button is clicked
     Then the Invoices Awaiting Final Approval table is displayed
      And the invoice is not displayed on the Invoices Awaiting Final Approval table
    Examples:
      | FROM  | TO    |
      | OOC   | OPEX  |
      | OPEX  | BMI   |
      
  @uswm @usad
  Scenario: Final Approver changes invoice funding route from "CAPEX" to "OPEX"
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the "Approve" button is clicked
      And the invoice funding route is changed to "OPEX" 
      And the reason for funding route change is entered
      And the Extreme Weather question is answered
      And the invoice funding approval Confirm button is clicked
     Then the Invoices Awaiting Final Approval table is displayed
      And the invoice is not displayed on the Invoices Awaiting Final Approval table
      
  @uswm @usad
  Scenario: Final Approver cancels changing invoice funding route
    Given a final approver and with an invoice awaiting final approval for "CAPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Final Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Final Approval"
     When the "Approve" button is clicked
      And the invoice funding route is changed to "OPEX" 
      And the reason for funding route change is entered
      And the Extreme Weather question is answered
      And the invoice funding approval Back button is clicked
     Then the Invoice Final Approval page is displayed
                             