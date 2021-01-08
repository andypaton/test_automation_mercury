@portal @portal_invoices @portal_invoices_awaiting_approval
Feature: Portal - Invoices - Invoices Awaiting Approval

  @grid
  @sanity @mcp @bugRainbow @bugWalmart
  Scenario: Invoices Awaiting Approval table actions [bug: MCP-13767, MCP-17941]
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
     When the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     Then the "Invoices Awaiting Approval" table is displayed
      And the "Invoices Awaiting Approval" table can be sorted on all columns
      And a search can be run on the "Invoices Awaiting Approval" table

  @mcp 
  Scenario: Invoice approval page displayed as expected
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
     Then the user will be presented with the uploaded invoice and invoice details 
     
  @mcp
  Scenario: Invoice Approver views a "Invoice Awaiting Approval"
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
     Then the funding route is displayed at the top right hand corner of the grid
      And the following Invoice Approval tabs are displayed "Invoice, Closedown, Job Notes, Uplifts, Job Costs"
      And the "Invoice" tab displays "Invoice details headers"
      And the "Closedown" tab displays "Job Type, First Arrival Date/Time, Job Completed Date/Time, Total Onsite Hours, Supplier Notes, Arrival Date/Time, Onsite Hours"
      And the "Job Notes" tab displays "Job Description, Job Closedown"
      And the "Uplifts" tab displays "Any uplifts attached to the job"
      And the "Job Costs" tab displays "Total Job Cost, PO Number, PO Type, Amount, Budget, Status"
      And Documents to Check are displayed
      And the Approve button is not visible until 'Next >>' is clicked
     
  @smoke @mcp
  Scenario: Approve an invoice awaiting approval with Extreme weather conditions
        * using dataset "portal_invoices_awaiting_approval_001"
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Approval"
     When the user approves an invoice with "Extreme" weather conditions
     Then the invoice status is set to approved
      
  @mcp
  Scenario Outline: Approve an invoice awaiting approval with "<weatherCondition>" weather conditions
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" a "Invoice Awaiting Approval"
     When the user approves an invoice with "<weatherCondition>" weather conditions
     Then the invoice status is set to approved
    Examples:
      |weatherCondition  |
      |Non Extreme       |

  @mcp @bugAdvocate
  Scenario Outline: Approve and update budget type for an invoice awaiting approval with "<weatherCondition>" weather conditions [bug: MCP-21740]
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
     Then the user is able to update the budget type and approve an invoice with "<weatherCondition>" weather conditions
      And the invoice status is set to approved
    Examples:
      | weatherCondition  |
      | Extreme           |
      | Non Extreme       |

  @mcp
  Scenario: Invoice Approver - View Invoice Lines
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" an "Invoice Awaiting Approval"
     When the View Invoice Lines button is clicked
     Then the Invoice Lines table is displayed
     
  @mcp
  Scenario Outline: RFM approves a "<ROUTE>" invoice
    Given a "RFM" user profile with an invoice awaiting approval for "<ROUTE>" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
      And the Timeline Event Summary has been updated with "Invoice Approved"
    Examples:
      | ROUTE |
      | OPEX  |
      | CAPEX |
      
  @uswm @ukrb @usah
  Scenario: RFM approves a "OOC" invoice
    Given a "RFM" user profile with an invoice awaiting approval for "OOC" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
      And the Timeline Event Summary has been updated with "Invoice Approved"
  
  @notsignedoff @uswm
  Scenario: RFM approves a "BMI" invoice
    Given a "RFM" user profile with an invoice awaiting approval for "BMI" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
      And the Timeline Event Summary has been updated with "Invoice Approved"
      
  @mcp
  Scenario: Invoice Approver cancels approving an invoice
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the user checks the documents attached and selects approve
      And the invoice funding approval Back button is clicked
     Then the Invoice Approval page is displayed
     
  @mcp
  Scenario Outline: RFM changes invoice funding route from "<FROM>" to "<TO>"
    Given a "RFM" user profile with an invoice awaiting approval for "<FROM>" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice funding route is changed to "<TO>" and the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
      And the Timeline Event Summary has been updated with "Invoice Approved"
    Examples:
      | FROM  | TO    |
      | OPEX  | CAPEX |
      | CAPEX | OPEX  |
      
  @uswm @ukrb @usah
  Scenario Outline: RFM changes invoice funding route from "<FROM>" to "<TO>"
    Given a "RFM" user profile with an invoice awaiting approval for "<FROM>" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice funding route is changed to "<TO>" and the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
      And the Timeline Event Summary has been updated with "Invoice Approved"
    Examples:
      | FROM  | TO    |
      | OPEX  | OOC   |
      | CAPEX | OOC   |
      | OOC   | OPEX  |
      | OOC   | CAPEX |
      
  @notsignedoff @uswm
  Scenario Outline: RFM changes invoice funding route from "<ROUTE>" to BMI
    Given a "RFM" user profile with an invoice awaiting approval for "<ROUTE>" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice funding route is changed to "BMI" and the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
      And the Timeline Event Summary has been updated with "Invoice Approved"
    Examples:
      | ROUTE |
      | CAPEX |
      | OPEX  |
      | OOC   |
      
  @notsignedoff @uswm
  Scenario Outline: RFM changes invoice funding route from BMI to "<ROUTE>"
    Given a "RFM" user profile with an invoice awaiting approval for "BMI" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice funding route is changed to "<ROUTE>" and the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
      And the Timeline Event Summary has been updated with "Invoice Approved"
    Examples:
      | ROUTE |
      | CAPEX |
      | OPEX  |
      | OOC   |
  
  @mcp
  Scenario: RFM cancels changing invoice funding route
    Given a "RFM" user profile with an invoice awaiting approval for "OPEX" funding route
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice funding route is changed to "CAPEX" and mandatory fields are answered
      And the invoice funding approval Back button is clicked
     Then the Invoice Approval page is displayed
   