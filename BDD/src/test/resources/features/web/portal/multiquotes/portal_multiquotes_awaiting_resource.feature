@portal @portal_multiquotes @portal_multiquotes_awaiting_resource
Feature: Portal - Multi Quotes - Awaiting Resource Selection - Manager
  
  @mcp
  Scenario: Verify as a RFM the Multi Quote Awaiting Resource Selection page displays correctly
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection"
     When the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     Then the "Multi-Quotes Awaiting Resource" form displays correctly
  
  @mcp
  Scenario: Verify as a RFM the Multi Quote Awaiting Resource Selection My Multi-Quotes Grid displays the headers correctly
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection"
     When the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     Then the "My Multi-Quotes" table on the "Awaiting Resource Selection" page displays correctly
     
  @mcp
  Scenario: Verify as a RFM the Multi Quote Awaiting Resource Selection My Multi-Quotes Grid displays each row correctly
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection"
     When the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     Then the "My Multi-Quotes" table on the "Awaiting Resource Selection" page displays each row correctly
  
  @mcp
  Scenario: RFM views a Multi-Quote Awaiting Resource
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
     Then the "Multi-Quote Awaiting Resource" form displays correctly      
     
  @sanity @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Selection - Edit and update all resources
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
      And the quote resources are populated
      And the "Multi-Quote Awaiting Resource" form is saved
     Then the Job is updated with a "ITQ Awaiting Acceptance" status
      And the "Resources Invited to Quote" notification has been updated
      And the "Invitation To Quote Notification" notification has been updated

  @bugRainbow @mcp
  Scenario: RFM views a Multi-Quote Awaiting Resource with Bypass [bug: MCP-15883 - test_ukrb]
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection with Bypass"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
     Then the "Scope of Works" warning is displayed
      And the "Quote Priority" warning is displayed
      And the "Bypass Multi-Quote" button is displayed
      
  @bugRainbow @mcp
  Scenario Outline: RFM views a Multi-Quote Awaiting Resource with Bypass - "<fundingroute>" funding route greater than budget [bug: MCP-15883 - test_ukrb]
    Given an RFM with a Multi Quote in state Awaiting Resource Selection with Bypass with a "<fundingroute>" funding route greater than budget
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
     Then the "Scope of Works" warning is displayed
      And the "Quote Priority" warning is displayed
      And the "Bypass Multi-Quote" button is displayed
    Examples: 
      | fundingroute  |
      | OPEX          |
      | CAPEX         |
      
  @uswm @usah
  Scenario: RFM views a Multi-Quote Awaiting Resource with Bypass - "BMI/INSURANCE" funding route greater than budget
    Given an RFM with a Multi Quote in state Awaiting Resource Selection with Bypass with a "BMI/INSURANCE" funding route greater than budget
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
     Then the "Scope of Works" warning is displayed
      And the "Quote Priority" warning is displayed
      And the "Bypass Multi-Quote" button is displayed
      
  @uswm @usah @ukrb
  @bugRainbow
  Scenario: RFM views a Multi-Quote Awaiting Resource with Bypass - "OOC" funding route greater than budget [bug: MCP-15883 - test_ukrb]
    Given an RFM with a Multi Quote in state Awaiting Resource Selection with Bypass with a "OOC" funding route greater than budget
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
     Then the "Scope of Works" warning is displayed
      And the "Quote Priority" warning is displayed
      And the "Bypass Multi-Quote" button is displayed

  @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Selection that has 1 quote submitted and verify warnings
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
     Then the "Scope of Works" warning is displayed
      And the "Quote Priority" warning is displayed
      
  @bugRainbow @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Selection that has 1 quote submitted over threshold and bypass Multi Quote [bug: MCP-15883 - test_ukrb]
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection with Bypass"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
      And Bypass Multi-Quote is clicked
      And a Multi-Quote Bypass Reason is selected
      And a Multi-Quote Bypass Note is entered
      And the confirmation for recommending the quote is given
      And the Quote Recommendation note is entered
      And the "Multi-Quote Awaiting Resource" form is saved
     Then the JobTimelineEvent table has been updated with "Multi Quote bypass requested"
      And the JobTimelineEvent table has been updated with "Quote Requires Final Approval"
    
  @bugRainbow @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Selection that has 1 quote submitted over threshold and bypass Multi Quote omitting the Multi-Quote bypass reason [bug: MCP-15883 - test_ukrb]
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection with Bypass"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
      And Bypass Multi-Quote is clicked
      And a Multi-Quote Bypass Note is entered
      And the confirmation for recommending the quote is given
      And the Quote Recommendation note is entered
      And the "Multi-Quote Awaiting Resource" form is saved
     Then the following error is displayed: "A Multi-Quote bypass reason must be selected"

  @bugRainbow @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Selection that has 1 quote submitted over threshold and bypass Multi Quote omitting the Multi-Quote bypass notes [bug: MCP-15883 - test_ukrb]
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection with Bypass"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
      And Bypass Multi-Quote is clicked
      And a Multi-Quote Bypass Reason is selected
      And the confirmation for recommending the quote is given
      And the Quote Recommendation note is entered
      And the "Multi-Quote Awaiting Resource" form is saved
     Then the following error is displayed: "A note of at least 20 characters must be supplied when bypassing Multi-Quote"

  @bugRainbow @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Selection that has 1 quote submitted over threshold and bypass Multi Quote omitting the Quote recommendation [bug: MCP-15883 - test_ukrb]
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection with Bypass"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
      And Bypass Multi-Quote is clicked
      And a Multi-Quote Bypass Reason is selected
      And a Multi-Quote Bypass Note is entered
      And the Quote Recommendation note is entered
      And the "Multi-Quote Awaiting Resource" form is saved
     Then the following error is displayed: "A quote recommendation must be confirmed for approval when bypassing Multi-Quote"

  @bugRainbow @mcp
  Scenario: As a RFM Edit a Multi Quote Awaiting Resource Selection that has 1 quote submitted over threshold and bypass Multi Quote omitting the Quote recommendation notes [bug: MCP-15883 - test_ukrb]
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Resource Selection with Bypass"
      And the user logs in
      And the "Awaiting Resource Selection" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Awaiting Resource"
      And Bypass Multi-Quote is clicked
      And a Multi-Quote Bypass Reason is selected
      And a Multi-Quote Bypass Note is entered
      And the confirmation for recommending the quote is given
      And the "Multi-Quote Awaiting Resource" form is saved
     Then the following error is displayed: "A quote recommendation note of at least 20 characters must be supplied when bypassing Multi-Quote"    
      
            