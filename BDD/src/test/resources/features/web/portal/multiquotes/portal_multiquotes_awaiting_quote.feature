@portal @portal_multiquotes @portal_multiquotes_awaiting_quote
Feature: Portal - Multi Quotes - Awaiting Quote - Manager
  
  @bugRainbow
  @mcp
  Scenario: RFM views all Multi Quotes Awaiting Quote [bug: MCP-13654]
    Given a "RFM" with a "Multi" "Quote" in state "Jobs Awaiting Quote"
     When the user logs in
      And the "Awaiting Quote" sub menu is selected from the "Multi-Quotes" top menu
     Then the "Multi-Quotes Awaiting Quote" form is displayed
      And the "My Multi-Quotes" table on the "Awaiting Quote" page displays expected headers
      And the "My Multi-Quotes - Awaiting Quote" table can be sorted on all columns
      And the "My Multi-Quotes" table on the "Awaiting Quote" page displays expected job details
      
  @mcp
  Scenario: As a RFM View a Multi Quote Awaiting Resource Quote
    Given a "RFM" with a "Multi" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
      And the "Awaiting Quote" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a Multi-Quote Awaiting Quote
     Then the Quote Awaiting Resource Quote page is displayed
     
  @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Quote
    Given a "RFM" with a "Multi" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
      And the "Awaiting Quote" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Edits" a Multi-Quote Awaiting Quote
     Then the "Multi-Quote Awaiting Quote" form displays correctly
     
   @uswm
   Scenario Outline: Check the minimum No of Quotes Required is 2 - Multi Quote Awaiting Resource Quote
     Given a "RFM" with a "Multi" "Quote" in state "Jobs Awaiting Quote" with a "<fundingroute>" funding route
       And the user logs in
       And the "Awaiting Quote" sub menu is selected from the "Multi-Quotes" top menu
      When the user "Views" a "Multi-Quote Awaiting Quote"
      Then the minimum number of quotes required is "2"
     Examples: 
      | fundingroute  |
      | CAPEX         |
      | BMI           |
     
  @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Quote - Edit and update all resources
    Given a "RFM" with a "Multi" "Quote" in state "Jobs Awaiting Quote"
      And the user logs in
      And the "Awaiting Quote" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Edits" a Multi-Quote Awaiting Quote
      And the quote resources are populated
      And the "Multi-Quote Awaiting Quote" form is saved
     Then the Job is updated with a "ITQ Awaiting Acceptance" status
      And the "Resources Invited to Quote" notification has been updated
      And the "Invitation To Quote Notification" notification has been updated
      And an email is sent for "Invitation To Quote Notification"

  @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Quote that has 1 quote submitted and verify warnings
    Given an RFM with a Multi Quote in state Jobs Awaiting Quote with "1 or more" quote submitted
      And the user logs in
      And the "Awaiting Quote" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Edits" a Multi-Quote Awaiting Quote
     Then the "Scope of Works" warning is displayed
      And the "Quote Priority" warning is displayed

  @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Quote that has 0 quote submitted and verify warnings not displayed
    Given an RFM with a Multi Quote in state Jobs Awaiting Quote with "0" quote submitted
      And the user logs in
      And the "Awaiting Quote" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Edits" a Multi-Quote Awaiting Quote
     Then the "Scope of Works" warning is not displayed
      And the "Quote Priority" warning is not displayed