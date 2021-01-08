@portal @portal_quotes @portal_quotes_awaiting_review
Feature: Portal - Quotes - Quotes Awaiting Review
  
  @mcp
  @bugRainbow
  Scenario: Verify as a RFM the Quotes Awaiting Review page displays the correctly [bug: MCP-13767]
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review"
     When the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
     Then the "My Quotes" table on the "Quotes Awaiting Review" page displays correctly
      And the "Quotes Awaiting Review" table can be sorted on all columns
      And the click functionality from the screen is available on "Quotes Awaiting Review"

  #@MTA-117
  @mcp
  Scenario: Verify as a RFM the Quote Managers Decision page displays the correctly
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review"
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Quote Managers Decision"
     Then the "Quote Managers Decision" form displays correctly
      And the job details displayed on "Quote Approval Decision" is view only

  #@MTA-117 @MTA-281 @MTA-810
  @mcp
  Scenario: Reject a quote as a RFM from the Quote Approval Decision page - Work No Longer Required
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review"
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "Rejected"
      And the Rejection action "Work No Longer Required" is selected
      And the quote rejection is saved
     Then the JobTimelineEvent table has been updated with "Quote Rejected"
      And the JobTimelineEvent table has been updated with "Job cancellation requested"
      #And the JobTimelineEvent table has been updated with "Job canceled"
      And the JobTimelineEvent table has been updated with "Quote Rejection email sent to"
      And the JobTimelineEvent table has been updated with "Quote Rejected Notification"
      And the Job is updated with a "Canceled" status

  #@MTA-117 @MTA-691
  @mcp
  Scenario: Reject a quote as a RFM from the Quote Approval Decision page - Request Alternative Quote
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review"
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "Rejected"
      And the Rejection action "Request Alternative Quote" is selected
      And a random Resource is selected to Quote
      And the quote rejection is saved
     Then the JobTimelineEvent table has been updated with "Quote Rejected"
      And the JobTimelineEvent table has been updated with "Alternative Quote Requested"
      And the JobTimelineEvent table has been updated with "Quote Rejection email sent to"
      And the JobTimelineEvent table has been updated with "Invitation To Quote Notification"
      And the JobTimelineEvent table has been updated with "Quote Rejected Notification"
      And the Job is updated with a "Awaiting Resource Quote" status

  #@MTA-117 @MTA-761 @MCP-6426
  @mcp
  Scenario: Reject a quote as a RFM from the Quote Managers Decision page - Fund As Reactive
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review"
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "Rejected"
      And the Rejection action "Fund As Reactive" is selected
      And a random Resource is selected to Quote
      And the quote rejection is saved
     Then the JobTimelineEvent table has been updated with "Quote Rejected"
      And the JobTimelineEvent table has been updated with "Job Type changed"
      And the JobTimelineEvent table has been updated with "Quote Rejection email sent to"
      And the JobTimelineEvent table has been updated with "Quote Rejected Notification"
      And the Job is updated with a "Logged" status
  
  #@MTA-117
  @mcp
  Scenario: Recommend a Quote Awaiting Approval as a RFM with funding route "OPEX"
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review" with a "OPEX" funding route less than budget
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "Accepted"
     And the "Quote Approval" is submitted 
     And the "Quote Approval" is saved
    Then the "Quotes Awaiting Review" page is displayed
     And the JobTimelineEvent table has been updated with "Quote Approved"
#     And the JobTtimelineEvent table has been updated with "Parts Requested"
# Need to write logic to determine if this is checked.  Will raise a Jira ticket for this as it will most likely require a custom query and will not be suitable for this step to be part of this step
#     And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Caller"
     And the JobTimelineEvent table has been updated with "Quote Job Approved Notification Manager"
     And the Job is updated with a "Quote Approved" status
      
  #@MTA-117 @MTA-281 
  @rework @usah
  Scenario: Recommend a Quote Awaiting Approval as a RFM with funding route "OPEX"
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review" with a "OPEX" funding route greater than budget
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "Accepted"
      And the "Quote Approval" RFM Approval notes are added
      And the "Quote Approval" is submitted
      And the "Quote Approval" Senior Manager notes are added
      And the "Quote Approval" is saved
     Then the JobTimelineEvent table has been updated with "Quote Requires Final Approval"
      And the JobTimelineEvent table has been updated with "Funding Request Notification"
      And the Job is updated with a "Awaiting Final Approval" status
      
  #@MTA-155
  @mcp
  Scenario: Query a Quote Awaiting Approval as a RFM with any funding route less than budget
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review" with "any" funding route less than budget
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "Queried"    
     Then the JobTimelineEvent table has been updated with "In Query"
      And the JobTimelineEvent table has been updated with "Invitation To Quote Query Notification"
      And the Job is updated with a "In Query" status
      And a "Quote - Query Response" alert is displayed
  
  @mcp
  Scenario: Verify that the resource receives a notification by email that a submitted quote has been queried
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review" with "any" funding route less than budget
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "Queried"    
     Then an email is sent for "Invitation To Quote Query Notification" 
      
  #This scenario is not valid.  It is automatically converted to a multiquote as its greater than budget
  #@MTA-155 
  @wip
  @mcp
  Scenario: Query a Quote Awaiting Approval as a RFM with funding route "OPEX" greater than budget
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review" with a "OPEX" funding route greater than budget
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "queried"    
     Then the JobTimelineEvent table has been updated with "In Query"
      And the JobTimelineEvent table has been updated with "Invitation To Quote Query Notification"
      And the Job is updated with a "In Query" status
      And a "Quote - Query Response" alert is displayed
  
  #@MTA-155
  @usah
  Scenario: Query a Quote Awaiting Approval as a RFM with funding route "CAPEX" greater than budget
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review" with a "CAPEX" funding route greater than budget
      And the user logs in
      And the "Quotes Awaiting Review" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quote Managers Decision"
     When the "Quote Approval" is "queried"    
     Then the JobTimelineEvent table has been updated with "In Query"
      And the JobTimelineEvent table has been updated with "Invitation To Quote Query Notification"
      And the Job is updated with a "In Query" status
      And a "Quote - Query Response" alert is displayed

      