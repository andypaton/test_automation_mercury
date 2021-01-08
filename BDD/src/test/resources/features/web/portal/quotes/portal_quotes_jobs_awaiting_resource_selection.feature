@portal @portal_quotes @portal_quotes_jobs_awaiting_resource
@mcp
Feature: Portal - Quotes - Jobs Awaiting Resource Selection
  
  Scenario: Verify as a RFM the Quotes Awaiting Resource Selection page displays the correct table information
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Resource Selection"
     When the user logs in
      And the "Jobs Awaiting Resource Selection" sub menu is selected from the "Quotes" top menu
     Then the "My Quotes" table on the "Jobs Awaiting Resource Selection" page displays correctly
      And the click functionality from the screen is not available on "Jobs Awaiting Resource Selection"
      
  Scenario: Verify as a RFM the Quotes Awaiting Resource Assignment page displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Resource Selection"
     When the user logs in
      And the "Jobs Awaiting Resource Selection" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Jobs Awaiting Resource Selection"
     Then the "Job Awaiting Resource Assignment" form displays correctly
      And the "Scope of Works" is editable on page "Jobs Awaiting Resource Selection"
      And the "Declined Invitation  History" table on the "Job Awaiting Resource Assignment" page displays correctly
  
  @smoke
  Scenario: Verify as a RFM the Quotes Awaiting Resource Assignment can be updated with new resource
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Resource Selection"
     When the user logs in
      And the "Jobs Awaiting Resource Selection" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Jobs Awaiting Resource Selection"
      And the quote scope of works is entered
      And the quote priority is selected
      And the number of quotes required is set to Funding Route minimum
      And the quote resources are populated
      And the "Job Awaiting Resource Assignment" form is saved
     Then the Job is updated with a "ITQ Awaiting Acceptance" status
      And the "Resources Invited to Quote" notification has been updated
      And the "Invitation To Quote Notification" notification has been updated
  
  Scenario: Verify that the job details is view only
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Resource Selection"
     When the user logs in
      And the "Jobs Awaiting Resource Selection" sub menu is selected from the "Quotes" top menu
      And the user "Views" a "Jobs Awaiting Resource Selection"
     Then the job details displayed is view only
  
  @bugRainbow 
  Scenario: Verify the Jobs Awaiting Resource Selection search box and sorting [bug: MCP-13767]
    Given a "RFM" with a "single" "Quote" in state "Jobs Awaiting Resource Selection"
     When the user logs in
      And the "Jobs Awaiting Resource Selection" sub menu is selected from the "Quotes" top menu
     Then a search box is present on the "Jobs Awaiting Resource Selection" page
      And the "Jobs Awaiting Resource Selection" table can be sorted on all columns