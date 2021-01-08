@portal @portal_funding_requests @portal_funding_requests_awaiting_response_review
Feature: Portal - Funding Requests - Awaiting Response Review

  @bugRainbow @uswm @ukrb @usah
  Scenario Outline: "<profile>" views Funding Requests Awaiting Response Review [bug: MCP-13654]
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
     When the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Awaiting Response Review" table on the "Funding Requests Awaiting Response Review" page displays correctly
      And a search box is present on the "Funding Requests Awaiting Response Review" page
      And the "Funding Requests Awaiting Response Review" table can be sorted on all columns
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @bugRainbow @mcp
  Scenario Outline: "<profile>" views Funding Requests Awaiting Response Review [bug: MCP-13654]
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
     When the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Awaiting Response Review" table on the "Funding Requests Awaiting Response Review" page displays correctly
      And a search box is present on the "Funding Requests Awaiting Response Review" page
      And the "Funding Requests Awaiting Response Review" table can be sorted on all columns
    Examples: 
      | profile             |  
      | Additional Approver | 
  
  @uswm @ukrb @usah
  Scenario Outline: Verify as a "<profile>" the Funding Requests Awaiting Response Review Grid displays the data correctly
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
     When the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Awaiting Response Review" table on the "Funding Requests Awaiting Response Review" page displays the latest job row correctly
    Examples: 
      | profile                   | 
      | Additional Final Approver |  
      
  @mcp
  Scenario Outline: Verify as a "<profile>" the Funding Requests Awaiting Response Review Grid displays the data correctly
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
     When the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Awaiting Response Review" table on the "Funding Requests Awaiting Response Review" page displays the latest job row correctly
    Examples: 
      | profile             |  
      | Additional Approver |
  
  @uswm @ukrb @usah
  Scenario Outline: Verify as a "<profile>", on opening the Funding Requests Awaiting Response Review page internal job notes/queries, job details and current budget route are displayed
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Awaiting Response Review"
     Then the internal job notes or queries are displayed
      And the job details are displayed without dates
      And the current budget route is displayed
      And the "Budget Route" is editable
      And the "Resource Quotes" table on the "Senior Manager Funding Request Decision" page displays each row correctly without dates
      And the Quote details recommended by RFM are displayed
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
  
  @mcp    
  Scenario Outline: Verify as a "<profile>", on opening the Funding Requests Awaiting Response Review page internal job notes/queries, job details and current budget route are displayed
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Awaiting Response Review"
     Then the internal job notes or queries are displayed
      And the job details are displayed without dates
      And the current budget route is displayed
      And the "Budget Route" is editable
      And the "Resource Quotes" table on the "Senior Manager Funding Request Decision" page displays each row correctly without dates
      And the Quote details recommended by RFM are displayed
    Examples: 
      | profile             | 
      | Additional Approver | 
  
  @uswm @ukrb @usah
  Scenario Outline: "<profile>" queries Funding Request Awaiting Response Review
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Awaiting Response Review"
      And the potential insurance question is answered
     When the user "Queries" a "Funding Request Awaiting Response Review"
     Then the job will not appear on the "Funding Request Awaiting Response Review" screen
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Queried with Initial Approver"
      And the Job is updated with a "Query with Initial Approver" status
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
  
  @mcp
  Scenario Outline: "<profile>" queries Funding Request Awaiting Response Review
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Awaiting Response Review"
      And the potential insurance question is answered
     When the user "Queries" a "Funding Request Awaiting Response Review"
     Then the job will not appear on the "Funding Request Awaiting Response Review" screen
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Queried with Initial Approver"
      And the Job is updated with a "Query with Initial Approver" status
    Examples: 
      | profile             | 
      | Additional Approver |  
  
  @uswm @ukrb @usah
  Scenario Outline: "<profile>" approves Funding Request Awaiting Response Review
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Awaiting Response Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Accepted"
      And the "Funding Request" is saved
     Then the job will not appear on the "Funding Request Awaiting Response Review" screen
     Then the Job is updated with a "Quote Approved" status
      And the Resource Assignment table has been updated with the status "New Job Notification Sent - Multiple"
      And the JobTimelineEvent table has been updated with "Quote Approved"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Senior Manager"
      And the Timeline Event Summary has been updated with "Resource Added - New Job Notification Sent - Multiple"
      And the Timeline Event Summary has been updated with "Notification and text message sent to - Multiple"
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @mcp
  Scenario Outline: "<profile>" approves Funding Request Awaiting Response Review
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Awaiting Response Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Accepted"
      And the "Funding Request" is saved
     Then the job will not appear on the "Funding Request Awaiting Response Review" screen
     Then the Job is updated with a "Quote Approved" status
      And the Resource Assignment table has been updated with the status "New Job Notification Sent - Multiple"
      And the JobTimelineEvent table has been updated with "Quote Approved"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Senior Manager"
      And the Timeline Event Summary has been updated with "Resource Added - New Job Notification Sent - Multiple"
      And the Timeline Event Summary has been updated with "Notification and text message sent to - Multiple"
    Examples: 
      | profile             | 
      | Additional Approver | 
   
  @uswm @ukrb @usah
  Scenario Outline: "<profile>" rejects Funding Request Awaiting Response Review
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Awaiting Response Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Rejected"
      And the "Funding Request" is saved
     Then the job will not appear on the "Funding Request Awaiting Response Review" screen
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Rejected"
      And the JobTimelineEvent table has been updated with "Funding Request Declined Notification"
      And the Job is updated with a "Awaiting Approval - Funding Request Rejected" status
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
    
  @mcp  
  Scenario Outline: "<profile>" rejects Funding Request Awaiting Response Review
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Awaiting Response Review"
      And the user logs in
      And the "Awaiting Response Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Awaiting Response Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Rejected"
      And the "Funding Request" is saved
     Then the job will not appear on the "Funding Request Awaiting Response Review" screen
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Rejected"
      And the JobTimelineEvent table has been updated with "Funding Request Declined Notification"
      And the Job is updated with a "Awaiting Approval - Funding Request Rejected" status
    Examples: 
      | profile             | 
      | Additional Approver | 
      