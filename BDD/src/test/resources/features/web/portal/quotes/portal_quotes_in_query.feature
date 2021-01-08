@portal @portal_quotes @portal_quotes_in_query
@mcp
Feature: Portal - Quotes - Quotes in Query

  Scenario: Verify as a RFM the Quotes in Query page displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "Quotes in Query"
     When the user logs in
      And the "Quotes in Query" sub menu is selected from the "Quotes" top menu
     Then the "My Quotes" table on the "Quotes in Query" page displays correctly
      And the click functionality from the screen is available on "Quotes in Query"

  @sanity
  Scenario: Verify as a RFM the Quote Managers Decision page displays the correctly for a Quote in Query of type OPEX
    Given a "RFM" with a "single" "Quote" in state "Quotes in Query" with a "OPEX" funding route
      And the user logs in
      And the "Quotes in Query" sub menu is selected from the "Quotes" top menu
     When the user "Views" a "Quote in Query"
     Then the "Quote Managers Decision" form displays correctly 

      
 