@helpdesk @helpdesk_jobs @helpdesk_jobs_request_quote
Feature: Helpdesk - Jobs - Request Quote

  @uswm @ukrb @usah
  Scenario: log a quote job - non CAPEX Urgent Critical
    Given a "Helpdesk Operator" has logged in
      And a search is run for a site with a resource capable of working on an asset
      And the "Log a job" button is clicked
      And the "Request Quote" button is clicked
     When the New Job form is completed for a non CAPEX Urgent Critical quote
      And the Job Contact is the same as caller
      And the save button is clicked
     Then a new "Quote" job is saved to the database with "Awaiting Quote Request Review" status
      And the quote panel is displayed with approver
  
  @usah
  Scenario: log a quote job - CAPEX Urgent Critical
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" site
      And a new job is being logged
      And the "Request Quote" button is clicked
     When a "CAPEX" quote with "Urgent - Critical" priority is entered
     Then a "CAPEX - Urgent Critical" alert is displayed 
     
  @uswm
  Scenario Outline: log a quote job - "<QUOTE TYPE>"
    Given a "Helpdesk Operator" has logged in
      And a search is run for a site with a resource capable of working on an asset
      And the "Log a job" button is clicked
      And the "Request Quote" button is clicked
     When the New Job form is completed for a "<QUOTE TYPE>" quote
      And the Job Contact is the same as caller
      And the save button is clicked
     Then a new "Quote" job is saved to the database with "Awaiting Quote Request Review" status
      And the quote panel is displayed with approver
     Examples:
       | QUOTE TYPE |
       | CAPEX      |
       | OPEX       |
       | OOC        |
  
  @usah
  Scenario: create linked quote job - CAPEX Urgent Critical
    Given a "Helpdesk Operator" has logged in
      And a "Reactive" job with status "Logged"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the "Quotes" action is selected
     When the "Create linked quote job" button is clicked
      And the quote type is set to "CAPEX"
      And the job detail priority is set to "Urgent - Critical"
      And the save button is clicked
     Then a new "Quote" job is saved to the database with "ITQ Awaiting Acceptance" status
      And the jobs are linked
  
  @uswm @ukrb @usah
  Scenario: create linked quote job - non CAPEX Urgent Critical
    Given a "Helpdesk Operator" has logged in
      And a "Reactive" job with status "Logged"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the "Quotes" action is selected
     When the "Create linked quote job" button is clicked
      And a non CAPEX Urgent Critical quote are selected
      And any Job Questions are answered
      And the Job Contact is the same as caller
      And the save button is clicked
     Then a new "Quote" job is saved to the database with "Awaiting Quote Request Review" status
      And the jobs are linked
      
  @usah
  Scenario: convert to quote job - CAPEX Urgent Critical
    Given a "Helpdesk Operator" has logged in
      And a "Reactive" job with status "Logged"
      And no resource is assigned
      And a search is run for the job reference
      And the "Quotes" action is selected
     When the "Convert to quote job" button is clicked
      And the quote type is set to "CAPEX"
      And the job detail priority is set to "Urgent - Critical"
     Then a "CAPEX - Urgent Critical" alert is displayed 
