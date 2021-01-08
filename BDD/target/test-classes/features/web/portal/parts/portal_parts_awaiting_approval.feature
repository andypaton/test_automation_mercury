@portal @portal_parts
@mcp
Feature: Portal - Part requests awaiting approval

  @bugRainbow
  Scenario: RFM views Part requests awaiting approval [bug: MCP-13654 on test_ukrb]
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
     When the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu
     Then the "My Parts Orders Approval" table on the "Parts Awaiting Approval" page displays correctly
      And the "My Parts Orders Approval" table can be sorted on all columns
  
  Scenario: Reject a parts awaiting approval request
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu      
      And the user "Views" a "Parts Awaiting Approval"
     When the parts request is rejected
     Then the job will not appear on the "Parts Requests Awaiting Approval" screen    
      And the "Parts Order Rejected" notification has been updated
      And the "Update Returning ETA Notification" notification has been updated
      And the Timeline Event Summary has been updated with "Resource Returning - Awaiting Parts Review"
      And the Resource Assignment table has been updated with the status "Returning"
      And an email is sent for "Mercury Helpdesk - Parts Request Rejected"
  
  Scenario: Approve a parts awaiting approval request
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu
      And the user "Views" a "Parts Awaiting Approval"
     When the parts request is approved
     Then the job will not appear on the "Parts Requests Awaiting Approval" screen
      And the Resource Assignment table has been updated with the status "Awaiting Parts"   
       And the "Parts Order Approved" notification has been updated
       And the "Parts Order Issued" notification has been updated
  
  @grid
  Scenario: Delete a part from a parts awaiting approval request
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu
      And the user "Views" a "Parts Awaiting Approval"
      And there is an item that can be deleted/amended
     When an item is deleted
     Then the reduced total cost is displayed
      And the correct number of parts requested rows are displayed
  
  @grid
  Scenario: Verify that rejecting the parts order is the only available option when all parts listed are deleted
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu
      And the user "Views" a "Parts Awaiting Approval"
      And there is an item that can be deleted/amended
     When all items are deleted
     Then rejecting the parts order is the only available option
  
  @grid
  Scenario: Reduce the quantity of parts awaiting approval request
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu      
      And the user "Views" a "Parts Awaiting Approval"
      And there is an item that can be deleted/amended
     When the item quantity is reduced     
     Then the total item cost has been reduced
      And the total cost has been reduced
  
  Scenario: Capital Parts Awaiting Approval
    Given a portal user with a "RFM" profile and with "reactive" "Capital Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu      
     When the user "Views" a "Parts Awaiting Approval"
     Then the "Budget" is not editable
      And a message "The budget cannot be changed as the parts request contains one or more capital parts" is displayed below the Budget dropdown
  
  Scenario: Approve a Capital Parts Awaiting Approval request
    Given a portal user with a "RFM" profile and with "reactive" "Capital Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu      
      And the user "Views" a "Parts Awaiting Approval"
     When the parts request is approved
     Then the job will not appear on the "Parts Requests Awaiting Approval" screen
      And the Resource Assignment table has been updated with the status "Awaiting Parts"   
      And the JobTimelineEvent table has been updated with "Parts Order Approved"
  
  Scenario: Non Capital Parts Awaiting Approval
    Given a portal user with a "RFM" profile and with "reactive" "Non Capital Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu      
     When the user "Views" a "Parts Awaiting Approval"
     Then the "Budget" is editable
  
  # Remove without dates part after MCP-13139 is fixed
  @bugWalmart @bugRainbow
  Scenario: Verify if Parts Order Awaiting Approval page displays job details, parts order details and New Parts Request table correctly [bug: MCP-13139]
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
      And the user logs in    
      And the "Parts Awaiting Approval" sub menu is selected from the "Parts" top menu      
     When the user "Views" a "Parts Awaiting Approval"
     Then the job details of parts order are displayed without dates
      And the parts order details are displayed without dates
      And the "New Parts Request" table on the "Parts Order Awaiting Approval" page displays correctly