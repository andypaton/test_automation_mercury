@portal @portal_funding_requests @portal_funding_requests_rejected
@mcp
Feature: Portal - Funding Requests - Rejected

  @bugRainbow
  Scenario: RFM views Quotes With Funding Request Rejected [bug: MCP-13654 on ukrb]
    Given a "RFM" with a "single" "Quote" in state "Funding Request Rejected"
      And the user logs in
     When the "Funding Request Rejected" sub menu is selected from the "Funding Requests" top menu
     Then the "Quotes With Funding Request Rejected" table on the "Funding Request Rejected" page displays correctly
      And a search box is present on the "Quotes With Funding Request Rejected" page
      And the "Quotes With Funding Request Rejected" table can be sorted on all columns
 
  @notsignedoff  
  Scenario: RFM views Funding Request Rejected
    Given a "RFM" with a "single" "Quote" in state "Funding Request Rejected"
      And the user logs in
      And the "Funding Request Rejected" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Rejected"
     Then the internal job notes or queries are displayed
      And the Quote Job Rejection Summary is displayed
      And the current budget route is displayed
      And the "Budget Route" is editable
      
  @bugAdvocate @bugWalmart @bugRainbow @notsignedoff
  Scenario: RFM confirms quote job rejection with rejection action - Work No Longer Required [bug: MCP-21805]
    Given a "RFM" with a "single" "Quote" in state "Funding Request Rejected"
      And the user logs in
      And the "Funding Request Rejected" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Rejected"
      And the potential insurance question is answered
     When the "Confirm Quote Job Rejection" radio button is clicked
      And the Rejection action "Work No Longer Required" is selected
      And the quote rejection is saved
     Then the quote will not appear on the "Quotes With Funding Request Rejected" screen
      And the JobTimelineEvent table has been updated with "Quote Rejected"
      And the JobTimelineEvent table has been updated with "Job cancellation requested"
      And the JobTimelineEvent table has been updated with "Job canceled"
      And the JobTimelineEvent table has been updated with "Quote Rejection email sent to"
      And the JobTimelineEvent table has been updated with "Quote Rejected Notification"
      And the Job is updated with a "Cancelled" status
  
  @bugAdvocate @bugWalmart @bugRainbow @notsignedoff
  Scenario: RFM confirms quote job rejection with rejection action - Request Alternative Quote [bug: MCP-21805]
    Given a "RFM" with a "single" "Quote" in state "Funding Request Rejected"
      And the user logs in
      And the "Funding Request Rejected" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Rejected"
      And the potential insurance question is answered
     When the "Confirm Quote Job Rejection" radio button is clicked
      And the Rejection action "Request Alternative Quote" is selected
      And the quote rejection is saved
     Then the quote will not appear on the "Quotes With Funding Request Rejected" screen
      And the JobTimelineEvent table has been updated with "Quote Rejected"
      And the JobTimelineEvent table has been updated with "Alternative Quote Requested"
      And the JobTimelineEvent table has been updated with "Quote Rejection email sent to"
      And the JobTimelineEvent table has been updated with "Quote Rejected Notification"
      And the Job is updated with a "Awaiting Resource Assignment" status
  
  @bugAdvocate @bugWalmart @bugRainbow @notsignedoff
  Scenario: RFM confirms quote job rejection with rejection action - Fund As Reactive [bug: MCP-21805]
    Given a "RFM" with a "single" "Quote" in state "Funding Request Rejected"
      And the user logs in
      And the "Funding Request Rejected" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Rejected"
      And the potential insurance question is answered
     When the "Confirm Quote Job Rejection" radio button is clicked
      And the Rejection action "Fund As Reactive" is selected
      And the quote rejection is saved
     Then the quote will not appear on the "Quotes With Funding Request Rejected" screen
      And the JobTimelineEvent table has been updated with "Quote Rejected"
      And the JobTimelineEvent table has been updated with "Job Type changed"
      And the JobTimelineEvent table has been updated with "Quote Rejection email sent to"
      And the JobTimelineEvent table has been updated with "Quote Rejected Notification"
      And the Job is updated with a "Awaiting Approval - Funding Request Rejected" status
  
  Scenario: RFM selects Edit Quote Job Recommendations for Funding Request Rejected
    Given a "RFM" with a "single" "Quote" in state "Funding Request Rejected"
      And the user logs in
      And the "Funding Request Rejected" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Rejected"
     When the "Edit Quote Job Recommendations" radio button is clicked
     Then the job details are displayed
      And the "Resource Quotes" table on the "Funding Request Rejected" page displays each row correctly
      And the RFM will also have the ability to Query the quote with the contractor if required
      And the Quote details recommended by RFM are displayed
  
  Scenario: RFM edits quote job recommendations and rerecommends quotes to Senior Manager
    Given a "RFM" with a "single" "Quote" in state "Funding Request Rejected"
      And the user logs in
      And the "Funding Request Rejected" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Rejected"
      And the potential insurance question is answered
      And the "Edit Quote Job Recommendations" radio button is clicked
     When all "Resource Quotes" are "Recommended"
      And the "Funding Request" is saved
     Then the quote will not appear on the "Quotes With Funding Request Rejected" screen
      And the JobTimelineEvent table has been updated with "Quote Requires Final Approval"
      And the JobTimelineEvent table has been updated with "Funding Request Notification"
      And the Job is updated with a "Awaiting Final Approval" status