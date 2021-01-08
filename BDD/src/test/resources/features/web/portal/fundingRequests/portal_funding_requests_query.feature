@portal @portal_funding_requests @portal_funding_requests_query
@mcp
Feature: Portal - Funding Requests - Query

  @bugRainbow
  Scenario: RFM views Funding Requests with Queries [bug: MCP-13654]
    Given a "RFM" with a "single" "Quote" in state "Funding Request Queries"
      And the user logs in
     When the "Funding Request Query" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Request Query" table on the "Funding Request Query" page displays correctly
     And a search box is present on the "Funding Request Query" page
     And the "Funding Request Query" table can be sorted on all columns

  Scenario: Verify as a RFM the Funding Request Query page - Send Response button is disabled until form completed
    Given a "RFM" with a "single" "Quote" in state "Funding Request Queries"
      And the user logs in
      And the "Funding Request Query" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Query"
     When the "Respond to query" radio button is clicked
     Then the "Send Response" button in the "Funding Request Query Response" page is disabled
  
  Scenario: RFM responds to a Funding Request Query - verify
    Given a "RFM" with a "single" "Quote" in state "Funding Request Queries"
      And the user logs in
      And the "Funding Request Query" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Query"
      And the potential insurance question is answered
     When the "Respond to query" radio button is clicked
      And the response to the query is submitted
     Then the quote will not appear on the "Quotes with Funding Request Queries" screen
      And the JobTimelineEvent table has been updated with "Initial Approver Responded to Funding Request Query"
      And the Job is updated with a "Awaiting Final Approval" status
      
  Scenario: RFM selects Edit Quote Job Recommendations for Funding Request Queries
    Given a "RFM" with a "single" "Quote" in state "Funding Request Queries"
      And the user logs in
      And the "Funding Request Query" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Query"
     When the "Edit Quote Job Recommendations" radio button is clicked
     Then the job details are displayed
      And the "Resource Quotes" table on the "Funding Request Query Response Required" page displays each row correctly without dates
      And the RFM will also have the ability to Query the quote with the contractor if required
      And the Quote details recommended by RFM are displayed
      
  Scenario: RFM edits quote job recommendations and responds to Funding request query - Verify
    Given a "RFM" with a "single" "Quote" in state "Funding Request Queries"
      And the user logs in
      And the "Funding Request Query" sub menu is selected from the "Funding Requests" top menu
      And the user "Views" a "Funding Request Query"
      And the potential insurance question is answered
     When the "Edit Quote Job Recommendations" radio button is clicked
      And all "Resource Quotes" are "Recommended"
      And the response to the query is submitted
     Then the quote will not appear on the "Quotes with Funding Request Queries" screen
      And the JobTimelineEvent table has been updated with "Quote Requires Final Approval"
      And the JobTimelineEvent table has been updated with "Initial Approver Responded to Funding Request Query"
      And the JobTimelineEvent table has been updated with "Funding Request Notification"
      And the Job is updated with a "Awaiting Final Approval" status
 
  Scenario: Verify as a RFM the Funding Request Query Response page displays internal job notes/queries and current budget route
    Given a "RFM" with a "single" "Quote" in state "Funding Request Queries"
      And the user logs in
      And the "Funding Request Query" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Funding Request Query"
     Then the internal job notes or queries are displayed
      And the current budget route is displayed
      And the "Budget Route" is editable
      