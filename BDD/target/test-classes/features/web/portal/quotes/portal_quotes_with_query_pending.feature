@portal @portal_quotes @portal_quotes_query_pending
@mcp
Feature: Portal - Quotes - Quotes with Query Pending
  
  #@MTA-282
  Scenario: View all Quotes with Query Pending
    Given a "Contractor Admin" with a "single" "Quote" in state "Quotes with Query Pending"
     When the user logs in
      And the "Quotes with Query Pending" sub menu is selected from the "Quotes" top menu
     Then the "My Quotes" table on the "Quotes with Query Pending" page displays correctly
      
  #@MTA-292
  Scenario: View a Quote with Query Pending
    Given a "Contractor Admin" with a "single" "Quote" in state "Quotes with Query Pending"
     When the user logs in
      And the "Quotes with Query Pending" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quotes with Query Pending"
     Then the "Quote Query" form displays correctly

  Scenario: Verify as a "Contractor Admin" can respond to a Quotes with Query
    Given a "Contractor Admin" with a "single" "Quote" in state "Quotes with Query Pending"
      And the user logs in
      And the "Quotes with Query Pending" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quotes with Query Pending"
     When the "Quote Query" notes are added
      And the "Quote Query" is submitted 
     Then the Job is updated with a "Awaiting Approval" status
      And the JobTimelineEvent table has been updated with "Resource responded to query"
      And the JobTimelineEvent table has been updated with "Quote Query Response Notification"
  
  Scenario: Verify as a "Contractor Admin" can view the query reason and comments input by the initial approver
    Given a "Contractor Admin" with a "single" "Quote" in state "Quotes with Query Pending"
      And the user logs in
      And the "Quotes with Query Pending" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Quotes with Query Pending"
     Then the user can view the query reason and comments input by the initial approver
     
  Scenario: Check validation for a response being submitted by a Contractor Admin profile without entering a response text
    Given a "Contractor Admin" with a "single" "Quote" in state "Quotes with Query Pending"
      And the user logs in
      And the "Quotes with Query Pending" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Quotes with Query Pending"
      And the "Send response" button is clicked
     Then the following error is displayed: "A response is required"
     
  Scenario: Verify as a "Contractor Admin" can edit the Quote from Quote Query page
    Given a "Contractor Admin" with a "single" "Quote" in state "Quotes with Query Pending"
      And the user logs in
      And the "Quotes with Query Pending" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Quotes with Query Pending"
      And the "Edit Quote" button is clicked
      And the "Edit" button is clicked
     Then all the elements on page "Edit Quote Details" are editable
     
  Scenario: Verify as a "Contractor Admin" can update the submitted quote
    Given a "Contractor Admin" with a "single" "Quote" in state "Quotes with Query Pending"
      And the user logs in
      And the "Quotes with Query Pending" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quotes with Query Pending"
      And the "Edit Quote" button is clicked
      And the "Edit" button is clicked
     When the description of works is updated
      And the "Update Submitted Quote" button is clicked
     Then the "Quote Query" form displays correctly