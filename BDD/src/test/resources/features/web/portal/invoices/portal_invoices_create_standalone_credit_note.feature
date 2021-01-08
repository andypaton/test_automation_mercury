@portal @portal_invoices @portal_invoices_create_standalone_credit_note
@mcp
Feature: Portal - Invoices - Create Standalone Credit Note

  #@MTA-637
  Scenario: Accounts Payable creating Standalone Credit Note - Gross Amount is autocalculated by system
    Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And a supplier or vendor to create credit note for is selected
      And the "Next" button is clicked
     When the Net Amount is entered
      And the Tax amount either less or greater than the allowed percent of the net amount is entered
     Then the Gross Amount is autocalculated by system
  
  #@MTA-644
  Scenario: Accounts Payable creating Standalone Credit Note - Credit Note Date is autopopulated with current date
    Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
     When a supplier or vendor to create credit note for is selected
      And the "Next" button is clicked
     Then the Credit Note Date is autopopulated with current date
     
  #@MTA-649 @MTA-886
  Scenario: Accounts Payable creating Standalone Credit Note - The credit tax amount can't be more than the allowed % of the credit net amount
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And a supplier or vendor to create credit note for is selected
      And the "Next" button is clicked
      And the Credit Note Number is entered
      And the SUN Reference is entered
      And all the SUN codes are selected
      And the Net Amount is entered
     When the Tax amount greater than the allowed percent of the net amount is entered
      And the "Next" button is clicked
     Then the following error is displayed: "The tax amount can't be more than the allowed % of the credit net amount"
     
  #@MTA-652
  Scenario: Accounts Payable creating Standalone Credit Note - Credit note number is required and Net Amount must be greater than zero
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And a supplier or vendor to create credit note for is selected
      And the "Next" button is clicked
     When the "Next" button is clicked
     Then the following error is displayed: "Credit note number is required"
      And the following error is displayed: "Net Amount must be greater than zero"
      
  #@MTA-653
  Scenario: Accounts Payable creating Standalone Credit Note - SUN reference and SUN tax codes are required
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And a supplier or vendor to create credit note for is selected
      And the "Next" button is clicked
     When the Credit Note Number is entered
      And the Net Amount is entered
      And the "Next" button is clicked
     Then the following error is displayed: "SUN Reference is required"
      And the following error is displayed: "Please select the SUN Contract Code"
      And the following error is displayed: "Please select the SUN Discipline code"
      And the following error is displayed: "Please select the SUN Store Code"
      And the following error is displayed: "Please select the SUN Tax Code"
      And the following error is displayed: "Please select the SUN Nominal Code"
      And the following error is displayed: "Please select the SUN Area Code"
      
  #@MTA-660 @MTA-911
  Scenario: Accounts Payable creating Standalone Credit Note - Credit note line value should match credit note header value and credit note document is required
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
     When the "Submit Credit Note" button is clicked
     Then the following error is displayed: "The total net value of the lines should equal the net value of the credit note header"
      And the following error is displayed: "The total tax value of the lines should equal the tax value of the credit note header"
      And the following error is displayed: "Credit note document is required."
      
  #@MTA-668
  @grid
  Scenario: Accounts Payable creating Standalone Credit Note -  Credit note details entered by the user are displayed in the credit note details table
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
     Then the credit note details entered by the user are displayed in the top row of the credit note details table
      And the credit note number is displayed as reference number in the top row of the credit note document table
 
  #@MTA-689
  Scenario: Accounts Payable creating Standalone Credit Note -  Validation errors in credit note line
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
      And the user selects add new line to credit note
      And the credit note line type is selected
      And the description is entered
     When the "Add" button is clicked
     Then the following error is displayed: "Validation error, please make sure quantity and price are set correctly"
       
  #@MTA-731
  @grid
  Scenario: Accounts Payable creating Standalone Credit Note -  Credit note line details entered by the user are displayed in the credit note line details table
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
     When the user adds new line to credit note
     Then the credit note line details entered by the user are displayed in the top row of the credit note line details table
     
  #@MTA-734 @MTA-888
  Scenario: Accounts Payable creating Standalone Credit Note - Validation errors in upload credit note document
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
     When the user selects upload credit note document
      And the "Upload File" button is clicked
     Then the following error is displayed: "Please specify the file to be uploaded"
     
  #@MTA-736 @MTA-888
  @bugWalmart
  Scenario: Accounts Payable creating Standalone Credit Note - [bug: MCP-13578 - test_uswm]
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
     When the user adds new line to credit note
      And uploads a credit note document
      And the "Submit Credit Note" button is clicked
     Then the credit note approval page is displayed with credit note details and embed credit note document
      And the Approve and Reject buttons are displayed in the credit note approval page
      And the credit note status in the database is "Awaiting Approval"
      
  #@MTA-736 @MTA-888
  Scenario: Accounts Payable creating Standalone Credit Note - Credit Note Lines modal in the Credit Note Approval page
   Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
      And the user adds new line to credit note
      And uploads a credit note document
      And the "Submit Credit Note" button is clicked
     When the "Credit Note Lines" button is clicked
     Then the credit note lines modal is displayed with credit note line details
  
  #@MTA-799  
  @smoke
  Scenario: Accounts Payable creating Standalone Credit Note - Approve
    Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
      And the user adds new line to credit note
      And uploads a credit note document
      And the "Submit Credit Note" button is clicked
     When the "Approve" button is clicked
     Then the credit note status in the database is "Approved"
  
  #@MTA-800   
  Scenario: Accounts Payable creating Standalone Credit Note - Reject
    Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Create Standalone Credit Note" sub menu is selected from the "Invoices and Credits" top menu
      And the standalone credit note is created with the tax amount less than the allowed percent of the net amount
      And the user adds new line to credit note
      And uploads a credit note document
      And the "Submit Credit Note" button is clicked
     When the "Reject" button is clicked
      And the user rejects the standalone credit note with reason and notes
     Then the credit note status in the database is "Rejected"
      And the rejection reason and notes are stored in the database