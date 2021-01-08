@helpdesk @helpdesk_jobs @helpdesk_jobs_last_viewed
@mcp
Feature: Helpdesk - Jobs - Last Viewed

  # Note: these tests might fail if run between 00:00 - 05:00 due to Last Viewed list not being displayed (to be confirmed and bug to be raised)

  Scenario: Recently viewed job list - content
    Given a "Helpdesk Operator" has logged in
     When a search is run for "11" jobs
     Then the Last Viewed list displays upto the last "10" job numbers with store names
      And the Last Viewed job list is updated immediately
  
  Scenario: Recently viewed job list - job selection
    Given a "Helpdesk Operator" has logged in
     When a job is selected from the Last Viewed list
     Then a new tab is opened with the job details

  Scenario: Recently viewed job list - new session
    Given a "Helpdesk Operator" has logged in
     When a search is run for "1" job
     Then the Last Viewed list displays the last "1" job number with store names on subsequent logins

