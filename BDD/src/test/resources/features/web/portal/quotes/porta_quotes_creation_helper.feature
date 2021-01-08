#@toggles @QuoteJob @BudgetDriven @AssetDriven @QuoteDocOptionalForTechnicians @QuoteDocOptionalForContractors @ResourceQuoteQuestions @HighRiskWorks
@portal @portal_quotes @wip
@mcp
Feature: Portal - Quotes - Creation Helper

# This is a play around feature to test things

#  Background: System Feature Toggles are set for WALMART
#    Given the system feature toggle "QuoteJob" is "enabled"
#      And the system sub feature toggle "BudgetDriven" is "enabled"
#      And the system sub feature toggle "AssetDriven" is "disabled"
#      And the system sub feature toggle "QuoteDocOptionalForTechnicians" is "enabled"
#      And the system sub feature toggle "QuoteDocOptionalForContractors" is "disabled"
#      And the system feature toggle "ResourceQuoteQuestions" is "enabled"
#      And the system sub feature toggle "HighRiskWorks" is "enabled"
              
  Scenario: Verify the Approval form displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"

  Scenario: Verify the Approval form displays the correct job information
    Given a "RFM" with a "multi" "Quote" in state "ITQ Awaiting Acceptance"   
    
  #this fails for some reason need to rework
  Scenario: Verify the Approval form displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "ITQ Awaiting Acceptance"
    
  Scenario: Verify the Approval form displays the correct job information
    Given a "RFM" with a "multi" "Quote" in state "Quotes Awaiting Review"      
    When the user logs in
   
  Scenario: Verify the Approval form displays the correct job information
    Given a "RFM" with a "multi" "Quote" in state "Jobs Awaiting Quote" 
        
  Scenario: Verify the Approval form displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "Quotes with Query Pending"     
    When the user logs in
    
  Scenario: Verify the Approval form displays the correct job information
    Given a "City Resource" with a "single" "Quote" in state "Quotes with Query Pending"     
    When the user logs in    
    
  Scenario: Verify the Approval form displays the correct job information
    Given a "City Resource" with a "multi" "Quote" in state "Awaiting Bypass Approval"
    When the user logs in
    
  Scenario: Verify the Approval form displays the correct job information
    Given a "Client Approver" with a "multi" "Quote" in state "Funding Requests Awaiting Approval"
    When the user logs in