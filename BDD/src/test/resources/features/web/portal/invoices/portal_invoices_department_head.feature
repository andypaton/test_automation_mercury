@portal @portal_invoices @portal_invoices_department_head
@mcp
Feature: Department Head - Head of Finance

  Scenario: Head of Finance - Home Page
    Given a Head of Finance with "Invoices Awaiting Approval"
     When the user logs in
     Then all configured menus are displayed including:
       | Home     |
       | Invoices |
      And the "Invoices" top menu contains following sub menu options
       | Invoices Awaiting Approval |
      And outstanding activities are displayed
      And a user menu displays their username and a logout option
      And the portal "Home" page displays correctly

  @notsignedoff @bugWalmart @bugRainbow
  Scenario: Head of Finance - View Invoices Awaiting Approval table [bug: MCP-17941]
    Given a Head of Finance with "Invoices Awaiting Approval"
      And the user logs in
     When the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     Then the "Invoices Awaiting Approval" table is displayed
      And the "Invoices Awaiting Approval" table can be sorted on all columns
      And a search can be run on the "Invoices Awaiting Approval" table
      
  Scenario: Head of Finance - Invoice approval page
    Given a Head of Finance with "Invoices Awaiting Approval"
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" an "Invoice Awaiting Approval"
     Then the funding route is displayed at the top right hand corner of the grid
      And the following Invoice Approval tabs are displayed "Invoice, Closedown, Job Notes, Uplifts, Job Costs"
      And the "Invoice" tab displays "Invoice details headers"
      And the "Closedown" tab displays "Job Type, First Arrival Date/Time, Job Completed Date/Time, Total Onsite Hours, Supplier Notes, Arrival Date/Time, Onsite Hours"
      And the "Job Notes" tab displays "Job Description, Job Closedown"
      And the "Uplifts" tab displays "Any uplifts attached to the job"
      And the "Job Costs" tab displays "Total Job Cost, PO Number, PO Type, Amount, Budget, Status"
      And Documents to Check are displayed
      And the Approve button is not visible until 'Next >>' is clicked
      
  Scenario: Head of Finance - View Invoice Lines
    Given a Head of Finance with "Invoices Awaiting Approval"
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
      And the user "Views" an "Invoice Awaiting Approval"
     When the View Invoice Lines button is clicked
     Then the Invoice Lines table is displayed
     
  Scenario: Head of Finance approves an invoice
    Given a Head of Finance with "Invoices Awaiting Approval"
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the invoice is approved
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table
     
  Scenario: Head of Finance cancels approving an invoice
    Given a Head of Finance with "Invoices Awaiting Approval"
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the user checks the documents attached and selects approve
      And the invoice funding approval Back button is clicked
     Then the Invoice Approval page is displayed
     
  Scenario: Head of Finance rejects an invoice
    Given a Head of Finance with "Invoices Awaiting Approval"
      And the user logs in
      And the "Invoices Awaiting Approval" sub menu is selected from the "Invoices" top menu
     When the user "Views" a "Invoice Awaiting Approval"
      And the user rejects an invoice
     Then the "Invoices Awaiting Approval" table on the "Invoices Awaiting Approval" page displays correctly
      And the invoice is not displayed on the Invoices Awaiting Approval table