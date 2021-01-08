@portal @portal_multiquotes @portal_multiquotes_awaiting_review
@toggles @BudgetReview
Feature: Portal - Multi Quotes - Awaiting Review - Manager

#Background: 
#    Given the BudgetReview toggle is enabled for Rainbow
  
  @bugRainbow @mcp
  Scenario: RFM views all Multi Quotes Awaiting Review [bug: MCP-13654]
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review"
     When the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
     Then the "Multi-Quotes Awaiting Review" form is displayed
      And the "My Multi-Quotes" table on the "Awaiting Review" page displays expected headers
      And the "My Multi-Quotes - Awaiting Review" table can be sorted on all columns
      And the "My Multi-Quotes" table on the "Awaiting Review" page displays expected job details

  @mcp
  Scenario: RFM views a Multi-Quote Awaiting Review
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review"
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Review"
     Then the "Multi-Quote Awaiting Review" form displays correctly  

  @mcp
  Scenario: RFM edits a Multi-Quote Awaiting Review
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review"
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Review"
      And the "Number of Quotes Required" is increased
      And the quote resources are populated
      And the "Multi-Quote Awaiting Review" form is saved
     Then the JobTimelineEvent table has been updated with "Resources Invited to Quote"
      And the JobTimelineEvent table has been updated with "Invitation To Quote Notification"
      And the Job is updated with a "ITQ Awaiting Acceptance" status         
   
  @mcp
  Scenario: As a RFM review a Multi Quote Awaiting Resource Selection and the Quote Approval Decision page displays correctly
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review"
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Reviews" a "Multi-Quote Awaiting Review"
     Then the "Quote Managers Decision" form displays correctly
     
  @mcp
  Scenario: As a RFM review a Multi Quote Awaiting Resource Selection and reject all Quotes
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review"
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
      And the user "Reviews" a "Multi-Quote Awaiting Review"
     When all "Quote Approval" are "Rejected"     
      And the Rejection action "Work No Longer Required" is selected
      And the quote rejection is saved
     Then the JobTimelineEvent table has been updated with "Quote Rejected"
      And the JobTimelineEvent table has been updated with "Job cancellation requested"
      And the JobTimelineEvent table has been updated with "Job canceled"
      And the JobTimelineEvent table has been updated with "Quote Rejection email sent to"
      And the JobTimelineEvent table has been updated with "Quote Rejected Notification"
      And the Job is updated with a "Cancelled" status
    
  @mcp
  Scenario: As a RFM review a Multi Quote Awaiting Resource Selection and query all Quotes
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review"
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
      And the user "Reviews" a "Multi-Quote Awaiting Review"
     When all "Quote Approval" are "Queried"     
     Then the "Resources Invited to Quote" notification has been updated
      And the "In Query" notification has been updated
      And the Job is updated with a "In Query" status
      And a "Quote - Query Response" alert is displayed
     
  @ukrb
  Scenario: RFM approves Multi Quote Awaiting Review - "any" funding route greater than budget
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review" with "any" funding route greater than budget
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
      And the user "Reviews" a "Multi-Quote Awaiting Review"
     When all "Quote Approval" are "Accepted"
      And the "Quote Approval" is submitted
      And the "Quote Approval" Senior Manager notes are added
      And the "Quote Approval" is saved
     Then the "Quote Requires Final Approval" notification has been updated
      And the "Funding Request Notification" notification has been updated
      And the Job is updated with a "Awaiting Final Approval" status
 
  @mcp @smoke
  Scenario: As a RFM review a Multi Quote Awaiting Resource Selection and accept all Quotes where RFM is final approver and quote less than approver limit
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review" with a "any" funding route less than budget
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
      And the user "Reviews" a "Multi-Quote Awaiting Review"
     When all "Quote Approval" are "Accepted"
      And the "Quote Approval" is submitted
      And the "Quote Approval" Senior Manager notes are added
      And the "Quote Approval" is saved
     Then the JobTimelineEvent table has been updated with "Quote Approved"
      And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager"
      
  @ukrb
  Scenario: RFM approves Multi Quote Awaiting Review - "OPEX" funding route greater than budget
  As a RFM review a Multi Quote Awaiting Resource Selection and accept all Quotes where RFM is not final approver and quote greater than approver limit
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review" with a "OPEX" funding route greater than budget
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
      And the user "Reviews" a "Multi-Quote Awaiting Review"
     When all "Quote Approval" are "Accepted"
      And the "Quote Approval" is submitted
      And the "Quote Approval" Senior Manager notes are added
      And the "Quote Approval" is saved
     Then the "Quote Requires Final Approval" notification has been updated
      And the "Funding Request Notification" notification has been updated
      And the Job is updated with a "Awaiting Final Approval" status
    
  @usah
  Scenario: RFM approves Multi Quote Awaiting Review - "OOC" funding route greater than budget
  As a RFM review a Multi Quote Awaiting Resource Selection and accept all Quotes where RFM is not final approver and quote greater than approver limit
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Review" with a "OOC" funding route greater than budget
      And the user logs in
      And the "Awaiting Review" sub menu is selected from the "Multi-Quotes" top menu
      And the user "Reviews" a "Multi-Quote Awaiting Review"
     When all "Quote Approval" are "Accepted"
      And the "Quote Approval" is submitted
      And the "Quote Approval" Senior Manager notes are added
      And the "Quote Approval" is saved
     Then the "Quote Requires Final Approval" notification has been updated
      And the "Funding Request Notification" notification has been updated
      And the Job is updated with a "Awaiting Final Approval" status