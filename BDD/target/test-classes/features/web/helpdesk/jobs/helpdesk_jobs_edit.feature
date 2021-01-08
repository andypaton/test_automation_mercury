@helpdesk @helpdesk_jobs @helpdesk_jobs_edit
@mcp
Feature: Helpdesk - Jobs - Edit 

  Scenario: Update job priority from non-immediate to immediate callout
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Operator" has logged in
      And a search is run for an unallocated job with "non-immediate" callout
     When the "Edit" action is selected
      And the fault type is updated to a priority with "immediate" callout
      And the caller is shown in the caller field
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
     Then the Manage Resources panel is displayed
      And the job "is" red flagged for immediate callout
      
  Scenario: Update job priority from immediate to non-immediate callout
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Operator" has logged in
      And a search is run for an unallocated job with "immediate" callout
     When the "Edit" action is selected
      And the fault type is updated to a priority with "non-immediate" callout
      And the caller is shown in the caller field
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
     Then the Manage Resources panel is displayed
      And the job "is not" red flagged for immediate callout
   
  @bugWalmart
  Scenario: Update job priority - deferred job to immediate callout [bug: MCP-19659]
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Operator" has logged in
      And a search is run for a deferred job
     When the "Edit" action is selected
      And the fault type is updated to a priority with "immediate" callout
      And the caller is shown in the caller field
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
     Then the Manage Resources panel is displayed
      And the job "is" red flagged for immediate callout
      And the job is sitting in the "Awaiting Assignment / Awaiting Acceptance" monitor
   
  Scenario: Update job priority - don't confirm
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Operator" has logged in
      And a search is run for an unallocated job with "non-immediate" callout
     When the "Edit" action is selected
      And the fault type is updated to a priority with "immediate" callout
      And the caller is shown in the caller field
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the "Back" button is clicked
     Then the Edit Job page is displayed
   
  Scenario: Update a job contact
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a single site contact
     When the "Edit" action is selected
     And the job contact is updated
     And the caller is shown in the caller field
     And the job is saved
     And the reason for changes is entered in the summary pop up
     And the changes are confirmed
    Then the Manage Resources panel is displayed
     And the job contact has been updated on the Contacts tab
    
  Scenario: Set a job contact to inactive
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job
     When the "Edit" action is selected
      And the job contact is set to inactive
      And the caller is shown in the caller field
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
     Then the Manage Resources panel is displayed
      And the job contact is displayed as inactive on the Contacts tab
    
  Scenario: Add a job contact - No outstanding Parts Order
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job "No outstanding Parts Order"
     When the "Edit" action is selected
      And a job contact is added
      And the caller is shown in the caller field
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
     Then the Manage Resources panel is displayed
      And the job contact has been added to the Contacts tab
  
  Scenario: Add a job contact - With outstanding Parts Order
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job "With outstanding Parts Order"
     When the "Edit" action is selected
      And a job contact is added
      And the caller is shown in the caller field
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
      And an additional resource is added
     Then the Manage Resources panel is displayed
      And the job contact has been added to the Contacts tab
    
  Scenario: Update a job contact - Cancel
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a single site contact
     When the "Edit" action is selected
      And the job contact is updated
      And the "Cancel" button is clicked
      And Cancel changes are confirmed
     Then the Job Details page is displayed
      And the job contact is not updated on the Contacts tab
   
  Scenario: Change caller
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job
     When the "Edit" action is selected
      And the caller is changed
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
     Then the Manage Resources panel is displayed
      And the caller card has been updated for the new caller
      And the jobs Core Details display the new caller
  
  Scenario: Edit caller
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a Client caller
     When the "Edit" action is selected
      And the caller is shown in the caller field
      And the caller is edited
      And the job is saved
      And the reason for changes is entered in the summary pop up
      And the changes are confirmed
     Then the Manage Resources panel is displayed
      And the caller card has been updated for the new caller
  
  Scenario: Edit caller - invalid phone number
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a Client caller
      And the "Edit" action is selected
      And the caller is shown in the caller field
      And Edit Caller is clicked
     When an invalid telephone number is entered
     Then an error for invalid phone number format is displayed
      And the "Update and identify as caller" button is disabled
      
  Scenario: Editing Job Details - Verify the fields can be edited
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a single site contact
     When the "Edit" action is selected
     Then user is able to make the changes to all available fields
      And the Deferral Question is not present
     
  Scenario: Editing Job Details - Verify the site contact can be updated
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a single site contact
     When the "Edit" action is selected
      And user adds a new site contact
     Then the site contact is updated
     
  Scenario: Editing Job Details - Verify that the job can be saved
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a single site contact
     When the "Edit" action is selected
      And user adds a new site contact
      And the site contact is updated
      And the job is saved
     Then a confirmation summary pop up is displayed
      And user can navigate back to the edit job page

  Scenario: Editing Job Details - Verify that the added contact is displayed in the Contacts tab
    Given a "Helpdesk Operator" has logged in
      And a search is run for a logged job with a single site contact
     When the "Edit" action is selected
      And a new site contact is added to the job and saved
     Then the new site contact is displayed on the Contacts tab
