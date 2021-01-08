@portal @portal_funding_requests @portal_funding_requests_awaiting_review
Feature: Portal - Funding Requests - Awaiting Review

  @bugRainbow @uswm @ukrb @usah
  Scenario Outline: "<profile>" views Funding Requests Awaiting Review [bug: MCP-13654]
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
     When the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests" table on the "Funding Requests Awaiting Review" page displays correctly
      And a search box is present on the "Funding Requests Awaiting Review" page
      And the "Funding Requests Awaiting Review" table can be sorted on all columns
    Examples: 
      | profile                   | 
      | Additional Final Approver |
      
  @bugRainbow @mcp
  Scenario Outline: "<profile>" views Funding Requests Awaiting Review [bug: MCP-13654]
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
     When the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests" table on the "Funding Requests Awaiting Review" page displays correctly
      And a search box is present on the "Funding Requests Awaiting Review" page
      And the "Funding Requests Awaiting Review" table can be sorted on all columns
    Examples: 
      | profile             |  
      | Additional Approver | 
  
  @uswm @ukrb @usah
  Scenario Outline: Verify as a "<profile>" the Funding Requests Decision page displays the form correctly
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Review"
     Then the "Senior Manager Funding Request Decision" form displays correctly 
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @mcp
  Scenario Outline: Verify as a "<profile>" the Funding Requests Decision page displays the form correctly
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Review"
     Then the "Senior Manager Funding Request Decision" form displays correctly 
    Examples: 
      | profile             |  
      | Additional Approver |

  @uswm @ukrb @usah
  Scenario Outline: Verify as a "<profile>" the Funding Requests Awaiting Review Grid displays the data correctly
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
     When the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Awaiting Review" table on the "Funding Requests Awaiting Review" page displays the latest job row correctly
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
  
  @mcp
  Scenario Outline: Verify as a "<profile>" the Funding Requests Awaiting Review Grid displays the data correctly
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
     When the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Awaiting Review" table on the "Funding Requests Awaiting Review" page displays the latest job row correctly
    Examples: 
      | profile             | 
      | Additional Approver | 
  
  @grid @uswm @ukrb @usah
  Scenario Outline: As a "<profile>" Reject a Funding Requests from the Funding Requests Decision page
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
      And  the user "Views" a "Funding Requests Awaiting Review"
      And the potential insurance question is answered
     When all "Resource Quotes" are "Rejected"
      And the "Funding Request" is saved
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Rejected"
      And the JobTimelineEvent table has been updated with "Funding Request Declined Notification"
      And the Job is updated with a "Awaiting Approval - Funding Request Rejected" status
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @grid @mcp
  Scenario Outline: As a "<profile>" Reject a Funding Requests from the Funding Requests Decision page
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
      And  the user "Views" a "Funding Requests Awaiting Review"
      And the potential insurance question is answered
     When all "Resource Quotes" are "Rejected"
      And the "Funding Request" is saved
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Rejected"
      And the JobTimelineEvent table has been updated with "Funding Request Declined Notification"
      And the Job is updated with a "Awaiting Approval - Funding Request Rejected" status
    Examples: 
      | profile             | 
      | Additional Approver | 
  
  @uswm @ukrb @usah
  Scenario Outline: As a "<profile>" Recommend a Funding Request
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
      And  the user "Views" a "Funding Requests Awaiting Review"
      And the potential insurance question is answered
     When all "Resource Quotes" are "Accepted"
      And the "Funding Request" is saved
     Then the JobTimelineEvent table has been updated with "Quote Approved"
  #     And the JobTtimelineEvent table has been updated with "Parts Requested"
  # Need to write logic to determine if this is checked.  Will raise a Jira ticket for this as it will most likely require a custom query and will not be suitable for this step to be part of this step
  #     And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Caller"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Senior Manager"
      And the Job is updated with a "Quote Approved" status
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @mcp
  Scenario Outline: As a "<profile>" Recommend a Funding Request
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
      And  the user "Views" a "Funding Requests Awaiting Review"
      And the potential insurance question is answered
     When all "Resource Quotes" are "Accepted"
      And the "Funding Request" is saved
     Then the JobTimelineEvent table has been updated with "Quote Approved"
  #     And the JobTtimelineEvent table has been updated with "Parts Requested"
  # Need to write logic to determine if this is checked.  Will raise a Jira ticket for this as it will most likely require a custom query and will not be suitable for this step to be part of this step
  #     And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Caller"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Senior Manager"
      And the Job is updated with a "Quote Approved" status
    Examples: 
      | profile             | 
      | Additional Approver | 

  @uswm @ukrb @usah
  Scenario Outline: As a "<profile>" Query a Funding Requests from the Funding Requests Decision page
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
      And  the user "Views" a "Funding Requests Awaiting Review"
      And the potential insurance question is answered
     When the user "Queries" a "Funding Request Awaiting Review"
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Queried with Initial Approver"
      And the Job is updated with a "Query with Initial Approver" status
    Examples: 
      | profile                   | 
      | Additional Final Approver |
      
  @mcp
  Scenario Outline: As a "<profile>" Query a Funding Requests from the Funding Requests Decision page
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
      And  the user "Views" a "Funding Requests Awaiting Review"
      And the potential insurance question is answered
     When the user "Queries" a "Funding Request Awaiting Review"
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Queried with Initial Approver"
      And the Job is updated with a "Query with Initial Approver" status
    Examples: 
      | profile             |  
      | Additional Approver |  
   
  @uswm @ukrb @usah
  Scenario Outline: "<profile>" views a Funding Request Awaiting review
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Review"
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
  Scenario Outline: "<profile>" views a Funding Request Awaiting review
    Given a portal user with a "<profile>" permission and with "AwaitingSeniorManagerApproval" Quote Jobs
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Review"
     Then the internal job notes or queries are displayed
      And the job details are displayed without dates
      And the current budget route is displayed
      And the "Budget Route" is editable
      And the "Resource Quotes" table on the "Senior Manager Funding Request Decision" page displays each row correctly without dates
      And the Quote details recommended by RFM are displayed
    Examples: 
      | profile             | 
      | Additional Approver |
      