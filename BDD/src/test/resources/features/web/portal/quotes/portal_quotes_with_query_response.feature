@portal @portal_quotes @portal_quotes_query_response
@mcp
Feature: Portal - Quotes - Quotes with Query Response
  
  #@MTA-299
  @bugRainbow
  Scenario: Verify as a RFM the Quotes with Query Response page displays the correct table information [bug : MCP-13767 ukrb]
    Given a "RFM" with a "single" "Quote" in state "Quotes with Query Response"
     When the user logs in
      And the "Quotes with Query Response" sub menu is selected from the "Quotes" top menu
     Then the "My Quotes" table on the "Quotes with Query Response" page displays correctly
      And the "Quotes with Query Response" table can be sorted on all columns
      And the click functionality from the screen is available on "Quotes with Query Response"
      
  #@MTA-303
  Scenario: Verify as a RFM the Quotes with Query Response page displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "Quotes with Query Response"
      And the user logs in
      And the "Quotes with Query Response" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Quotes with Query Response"
     Then the "Quote Managers Decision" form displays correctly
      And the job details displayed on "Quote Managers Decision" is view only

  Scenario: Query a Quote with Query Response as a RFM
    Given a "RFM" with a "single" "Quote" in state "Quotes with Query Response"
      And the user logs in
      And the "Quotes with Query Response" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quotes with Query Response"
     When the "Quote Approval" is "queried"    
     Then the JobTimelineEvent table has been updated with "In Query"     
      And the Job is updated with a "In Query" status
      And a "Quote - Query Response" alert is displayed
      And the JobTimelineEvent table has been updated with "Invitation To Quote Query Notification"

  #@MTA-303 @MTA-527
  Scenario: View Quote Query history for Quote with Query Response as a RFM
    Given a "RFM" with a "single" "Quote" in state "Quotes with Query Response"
      And the user logs in
      And the "Quotes with Query Response" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Quotes with Query Response"
     When show Quote Query history is clicked 
     Then the query request and response timeline will be displayed correctly

  Scenario: Verify as a RFM the Resource Quote and the Quote details are shown correctly
    Given a "RFM" with a "single" "Quote" in state "Quotes with Query Response"
      And the user logs in
      And the "Quotes with Query Response" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Quotes with Query Response"
     Then the Resource Quote details are displayed correctly 