@portal @portal_multiquotes @portal_multiquotes_awaiting_bypass_review
@mcp
Feature: Portal - Multi Quotes - Awaiting Bypass Review - Manager
  
  Scenario: Verify as a RFM the Multi Quote awaiting Bypass review page displays correctly
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Bypass Approval"
     When the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Multi-Quotes" top menu
     Then the "Multi-Quotes Awaiting Bypass Approval" form displays correctly
  
  @grid
  Scenario: Verify as a RFM the Multi Quote awaiting Bypass review My Multi-Quotes Grid displays the headers correctly
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Bypass Approval"
     When the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Multi-Quotes" top menu
     Then the "My Multi-Quotes" table on the "Awaiting Bypass Approval" page displays correctly
     
  Scenario: Verify as a RFM the Multi Quote awaiting Bypass review My Multi-Quotes Grid displays each row correctly
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Bypass Approval"
     When the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Multi-Quotes" top menu
     Then the "My Multi-Quotes" table on the "Awaiting Bypass Approval" page displays each row correctly
    
  Scenario: Verify as a RFM the Multi Quote Awaiting Bypass Review edit page is displayed correctly
    * using dataset "portal_multi_quotes_awaiting_bypass_001"
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Bypass Approval"
      And the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Bypass Review" 
     Then  the "Scope of Works" is not editable
      And the "Number of Quotes Required" is editable
      And the "Bypass information" is not editable  
      
  Scenario: As a RFM the cancel a Multi Quote Awaiting Bypass Review request
    Given a "RFM" with a "Multi" "Quote" in state "Awaiting Bypass Approval"
     When the user logs in
      And the "Awaiting Bypass Review" sub menu is selected from the "Multi-Quotes" top menu
     When the user "Views" a "Multi-Quote Bypass Review" 
      And the "Number of Quotes Required" is increased
      And the quote resources are populated
      And the "Multi-Quote Awaiting Bypass Review" form is saved
     Then the "Resources Invited to Quote" notification has been updated
      And the "Invitation To Quote Notification" notification has been updated
      And the Job is updated with a "ITQ Awaiting Acceptance" status      