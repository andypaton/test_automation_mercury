@portal @portal_funding_requests @portal_funding_requests_awaiting_bypass_review
Feature: Portal - Funding Requests - Awaiting Bypass Review - Manager

  @uswm @ukrb @usah @bugRainbow
  Scenario Outline: "<profile>" views Funding Requests Awaiting Bypass Review [bug: MCP-13654]
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
     When the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Multi Quotes Awaiting Bypass Review" table on the "Multi Quotes Awaiting Bypass Review" page displays correctly
     And a search box is present on the "Multi Quotes Awaiting Bypass Review" page
     And the "Multi Quotes Awaiting Bypass Review" table can be sorted on all columns
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @mcp @bugRainbow
  Scenario Outline: "<profile>" views Funding Requests Awaiting Bypass Review [bug: MCP-13654]
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
     When the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     Then the "Multi Quotes Awaiting Bypass Review" table on the "Multi Quotes Awaiting Bypass Review" page displays correctly
     And a search box is present on the "Multi Quotes Awaiting Bypass Review" page
     And the "Multi Quotes Awaiting Bypass Review" table can be sorted on all columns
    Examples: 
      | profile             | 
      | Additional Approver | 

  @uswm @ukrb @usah
  Scenario Outline: Verify as a "<profile>" the Funding Requests Awaiting Bypass Review page displays the job details correctly
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
     Then the "Senior Manager Funding Request Decision" form displays correctly 
      And the job details are displayed
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
   
  @mcp   
  Scenario Outline: Verify as a "<profile>" the Funding Requests Awaiting Bypass Review page displays the job details correctly
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
     Then the "Senior Manager Funding Request Decision" form displays correctly 
      And the job details are displayed
    Examples: 
      | profile             |  
      | Additional Approver |

  @uswm @ukrb @usah
  Scenario Outline: As a "<profile>" reject a Funding Requests Awaiting Bypass review
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Rejected"
      And the "Funding Request" is saved
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Rejected"
      And the Job is updated with a "Awaiting Approval - Funding Request Rejected" status
  # Next line will be enabled after business team confirm
  #      And the JobTimelineEvent table has been updated with "Funding Request Declined Notification" 
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
   
  @mcp   
  Scenario Outline: As a "<profile>" reject a Funding Requests Awaiting Bypass review
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Rejected"
      And the "Funding Request" is saved
     Then the JobTimelineEvent table has been updated with "Quote Funding Request Rejected"
      And the Job is updated with a "Awaiting Approval - Funding Request Rejected" status
  # Next line will be enabled after business team confirm
  #      And the JobTimelineEvent table has been updated with "Funding Request Declined Notification" 
    Examples: 
      | profile             | 
      | Additional Approver |

  @uswm @ukrb @usah
  Scenario Outline: As a "<profile>" approve a Funding Requests Awaiting Bypass review
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Accepted"
      And the "Funding Request" is saved
     Then the Job is updated with a "Quote Approved" status
      And the Resource Assignment table has been updated with the status "New Job Notification Sent - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Approved"
      And the Timeline Event Summary has been updated with "Resource Added - Bypass Review"
      And the Timeline Event Summary has been updated with "Notification and text message sent to - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Senior Manager - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Caller - Bypass Review"
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @mcp
  Scenario Outline: As a "<profile>" approve a Funding Requests Awaiting Bypass review
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
      And the potential insurance question is answered
      And all "Resource Quotes" are "Accepted"
      And the "Funding Request" is saved
     Then the Job is updated with a "Quote Approved" status
      And the Resource Assignment table has been updated with the status "New Job Notification Sent - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Approved"
      And the Timeline Event Summary has been updated with "Resource Added - Bypass Review"
      And the Timeline Event Summary has been updated with "Notification and text message sent to - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Senior Manager - Bypass Review"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Caller - Bypass Review"
    Examples: 
      | profile             |  
      | Additional Approver |
    
  @uswm @ukrb @usah
  Scenario Outline: "<profile>" views a Funding Request Awaiting Bypass review
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
     Then the job details are displayed without dates
      And the Multi-Quote Bypass section is displayed
      And the current budget route is displayed
      And the "Budget Route" is editable
      And the "Resource Quotes" table on the "Senior Manager Funding Request Decision" page displays each row correctly without dates
      And the Quote details recommended by RFM are displayed
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
   
  @mcp   
  Scenario Outline: "<profile>" views a Funding Request Awaiting Bypass review
    Given a "<profile>" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Requests Awaiting Bypass Review"
     Then the job details are displayed without dates
      And the Multi-Quote Bypass section is displayed
      And the current budget route is displayed
      And the "Budget Route" is editable
      And the "Resource Quotes" table on the "Senior Manager Funding Request Decision" page displays each row correctly without dates
      And the Quote details recommended by RFM are displayed
    Examples: 
      | profile             | 
      | Additional Approver |