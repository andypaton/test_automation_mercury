@helpdesk @helpdesk_jobs @helpdesk_jobs_add_note
@mcp
Feature: Helpdesk - Jobs - Add Note

  Scenario: Add a public note to a job via edit
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
     When the "Edit" action is selected
      And the "Add note" button is clicked
      And notes are entered on the page
      And the "Save" button is clicked
     Then the notes timeline displays a new "Public note added" event
      And the "Notes" counter is increased

  Scenario: Add a public note to a job via Job View
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
     When the "Add note" button is clicked
      And notes are entered on the page
      And the "Save" button is clicked
     Then the job timeline displays a new "Public note added" event

  Scenario: Add a private note to a job via Edit View
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
     When the "Edit" action is selected
     When the "Add note" button is clicked
      And notes are entered on the page
      And the private checkbox is clicked
      And the "Save" button is clicked
     Then the notes timeline displays a new "Private note added" event
      And the "Notes" counter is increased
          
  Scenario: Add a private note to a job via Job View
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
     When the "Add note" button is clicked
      And notes are entered on the page
      And the private checkbox is clicked
      And the "Save" button is clicked
     Then the job timeline displays a new "Private note added" event
