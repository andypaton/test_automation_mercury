@portal @portal_quotes @portal_quotes_approve
@mcp
Feature: Portal - Quotes - Approve awaiting quote request jobs
  
  Scenario: Verify the Approval form displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the approval form for a job "Awaiting Quote Request Approval"
     Then the Job Details on the Jobs Awaiting Quote Request Approval form displays correctly
      And the Approve Quote Request Form displays correctly 
      And the "Scope of Works" is editable on page "Approve Quote Request"

  Scenario: Verify all "Contractor" are shown when Show all Contractor is selected
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the approval form for a job "Awaiting Quote Request Approval"
      And "Contractor" is selected from the resource picker
      And show all "Contractor" is selected
     Then all "Contractor" will be available
   
   Scenario: Verify all "Technician" are shown when Show all Technician is selected
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the approval form for a job "Awaiting Quote Request Approval"
      And "Technician" is selected from the resource picker
      And show all "Technician" is selected
     Then all "Technician" will be available
  
  #@MTA-386
  @smoke
  Scenario: RFM Approves an Open Quote Request
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the approval form for a job "Awaiting Quote Request Approval"
      And the quote scope of works is entered
      And the quote priority is selected
      And the number of quotes required is set to Funding Route minimum
      And the quote resources are populated
      And the "Awaiting Quote Request Approval" form is saved
     Then the Job is updated with a "ITQ Awaiting Acceptance" status
      And the "Resources Invited to Quote" notification has been updated
      And the "Invitation To Quote Notification" notification has been updated
    
#  Scenario Outline: Verify the Open Quote Request page grid can be sorted
#  Scenario Outline: Verify the Open Quote Request page grid displays the correct data

  @bugRainbow
  Scenario: Verify the Open Quote Requests grid headers, search box and sorting - [bug: MCP-13767]
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
     Then the "Open Quote Requests" table on the "Open Quote Requests" page displays correctly
      And a search box is present on the "Open Quote Requests" page
      And the "Open Quote Requests" table can be sorted on all columns
      And the click functionality from the screen is not available on "Open Quote Requests"