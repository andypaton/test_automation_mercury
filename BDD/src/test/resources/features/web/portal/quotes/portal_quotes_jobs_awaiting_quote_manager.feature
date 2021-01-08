@portal @portal_quotes @portal_quotes_jobs_awaiting_quote
@mcp
Feature: Portal - Quotes - Jobs Awaiting Quote - Manager
  
  #@MTA-318
  Scenario: Verify as a RFM the Jobs Awaiting Quote page displays the correct table information
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Quote"
     When the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
     Then the "My Quotes" table on the "Jobs Awaiting Quote" page displays correctly
      And the click functionality from the screen is not available on "Jobs Awaiting Quote"
      
  #@MTA-318
  Scenario: Verify as a RFM the Jobs Awaiting Quote page displays the correct information
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Jobs Awaiting Quote"
     Then the "Job Awaiting Quote" form displays correctly
      And the "Scope of Works" is editable on page "Jobs Awaiting Quote"

  #@MTA-318 @MTA-570
  Scenario: As a RFM change the Quote Priority and verify
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Jobs Awaiting Quote"
     When a new quote priority is selected
      And the quote resources are populated
      And the "Job Awaiting Quote" form is saved
     Then the Quote "Quote Priority" is updated 

  #@MTA-318 @MTA-570
  Scenario: As a RFM change the Quote Type and verify
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Jobs Awaiting Quote"
     When a new quote type is selected
      # Need to ensure all quote resources are populated
      And the quote resources are populated
      And the "Job Awaiting Quote" form is saved
     Then the Quote "Quote Type" is updated 
 
  #@MTA-318 @MTA-698
  Scenario: As a RFM change the Quote Resource and verify
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Jobs Awaiting Quote"
     When a new quote resource is selected
      And the "Job Awaiting Quote" form is saved
     Then the Quote "Quote Resource" is updated 
  
  @bugRainbow       
  Scenario: Verify the Jobs Awaiting Quote search box and headers sorting [bug: MCP-13767]
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
     When the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
     Then a search box is present on the "Jobs Awaiting Quote" page
      And the "Jobs Awaiting Quote" table can be sorted on all columns