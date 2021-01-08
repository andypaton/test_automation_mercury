@helpdesk @helpdesk_jobs @helpdesk_jobs_reopen
Feature: Helpdesk - Jobs - Reopen

  @uswm @ukrb @usah
  Scenario: reopen a cancelled job - Engineer completed in error
    Given an "IT" user has logged in
      And a search is run for a cancelled job assigned to a "City Resource"
     When the "Reopen Job" action is selected
      And reopen reason "Engineer completed in error", notes and existing resource are entered
      And Re-Open is selected
     Then the client status will change back to the status before the job was cancelled
      And the timeline displays a "Job reopened" event with "Reason and Notes"
      And the timeline displays a "Resource Added" event
      
  @uswm @ukrb @usah
  Scenario Outline: reopen a cancelled job - "<reason>"
    Given an "IT" user has logged in
      And a search is run for a cancelled job
     When the "Reopen Job" action is selected
      And reopen reason "<reason>" and notes are entered
      And Re-Open is selected
     Then the client status will change back to the status before the job was cancelled
      And the timeline displays a "Job reopened" event with "Reason and Notes"
    Examples: 
      | reason                        | 
      | Site advised works incomplete | 
      | Additional resource required  | 
      | Closed/Canceled in error      | 
      
  @uswm @ukrb @usah
  Scenario: cancel reopening a cancelled job
    Given an "IT" user has logged in
      And a search is run for a cancelled job
     When the "Reopen Job" action is selected
      And all reopen details requested are entered
      And Re-Open pop up is canceled
     Then the client status is still "Cancelled"

  @uswm @ukrb @usah
  Scenario: reopen a fixed job
    Given an "IT" user has logged in
      And a search is run for a fixed job
     When the "Reopen Job" action is selected
      And reopen reason "Engineer completed in error", notes and existing resource are entered
      And Re-Open is selected
     Then the client status is now "In Progress"
      And the timeline displays a "Job reopened" event with "Reason and Notes"
      And the timeline displays a "Resource Added" event
      
  @uswm @ukrb @usah
  Scenario: cancel reopening a fixed job
    Given an "IT" user has logged in
      And a search is run for a fixed job
     When the "Reopen Job" action is selected
      And all reopen details requested are entered
      And Re-Open pop up is canceled
     Then the client status is still "Fixed"
     