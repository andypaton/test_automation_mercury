@portal @portal_invoices @portal_invoices_view_awaiting_approval
@mcp
Feature: Portal - Invoices - View invoices and credits awaiting review

  Scenario: View invoices and credits which are awaiting review
    Given a user with profile "Accounts Payable"
     When the user logs in
      And the "Invoices & Credits Awaiting Review" sub menu is selected from the "Invoices and Credits" top menu
     Then the "Invoices & Credits Awaiting Review" form displays correctly

  @smoke
  Scenario: Approve an invoice awaiting review - Action - Values Changed
    Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Invoices & Credits Awaiting Review" sub menu is selected from the "Invoices and Credits" top menu
      And the user opens an invoice awaiting review with "Values Changed" action
     When the user approves the invoice pending review
     Then the invoice status is set to "Awaiting Approval" status
  
  #Need to make this test more robust by creating a general order on desktop before invoice is assigned to RFM
  @wip
  Scenario: Approve an invoice awaiting review - Action - No Approver
    Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Invoices & Credits Awaiting Review" sub menu is selected from the "Invoices and Credits" top menu
      And the user opens an invoice awaiting review with "No Approver" action
     When the invoice is re-assigned to a new approver
     Then the invoice is not displayed on the "Invoices & Credits Awaiting Review" table
      And the invoice status is still "Awaiting Approval"
  
  Scenario: Reject an invoice awaiting review - Action - Values Changed
    Given a user with profile "Accounts Payable"
      And the user logs in
      And the "Invoices & Credits Awaiting Review" sub menu is selected from the "Invoices and Credits" top menu
      And the user opens an invoice awaiting review with "Values Changed" action
     When the user rejects the invoice pending review
     Then the "Invoices & Credits Awaiting Review" page is displayed
      And the invoice status is set to "Rejected" status