@portal @portal_resources @portal_resources_on_call_summary
@mcp
Feature: Portal - Resources - On Call Summary
  
  #@MTA-327 Commented out last step until the bug is resolved 
  @smoke1 @bugWalmart
  Scenario: Verify as a RFM the Resources On Call Summary page loads correctly [bug: MCP-17776]
    Given a "RFM" with access to the "Resources > On Call Summary" menu
      And the user logs in
     When the "On Call Summary" sub menu is selected from the "Resources" top menu
     Then the "On Call Summary" form displays correctly
      And the "On Call Summary" table on the "On Call Summary" page displays expected headers
      And the user will have visibility of the next 52 weeks oncall summary records      
      #And the "Schedule" table on the "On Call Summary" page displays each row correctly 
      
  @grid
  Scenario: Verify as a RFM the Resources On Call Summary page has search functionality on screen
    Given a "RFM" with access to the "Resources > On Call Summary" menu
      And the user logs in
     When the "On Call Summary" sub menu is selected from the "Resources" top menu
     Then a search box is present on the "On Call Summary" page
      And entering search criteria filters the table correctly
  
  @grid
  Scenario: Verify as a RFM the Resources On Call Summary page has filter functionality on screen
    Given a "RFM" with access to the "Resources > On Call Summary" menu
      And the user logs in
     When the "On Call Summary" sub menu is selected from the "Resources" top menu
     Then the "On Call Summary" table can be sorted on all columns