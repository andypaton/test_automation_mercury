@wip @deprecated
@helpdesk
Feature: Helpdesk - Log a Job for a Contract City Tech store

FEATURE SET TO @wip :  MCP-755 The BTFM contract has come to an end so no All Inclusive stores exist anymore

# new steps for asserting timeline events not signed off. added for Triage tests      
  @notsignedoff
  Scenario: log job for contract store - P1 fault
    Given a "Helpdesk Operator" has logged in
      And a contract store with an available resource to work on asset types with priority "P1"
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a contractor
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the job "is" red flagged for immediate callout
      And the timeline displays an "Awaiting Funding Authorisation" event
      And the timeline displays an "Email notification sent" event
      And the timeline displays a "Funding Approved" event with "Notes"
  
  # new steps for asserting timeline events not signed off. added for Triage tests      
  @notsignedoff @inHours
  Scenario Outline: log job for contract store - "<priority>" fault, brand in hours
    Given a "Helpdesk Operator" has logged in
      And a contract store with an available resource to work on asset types with priority "<priority>"
      And the call is within the store brand hours
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a contractor
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Notification Window" monitor
      And the job "is not" red flagged for immediate callout
      And the timeline displays an "Awaiting Funding Authorisation" event
      And the timeline displays an "Email notification sent" event
      And the timeline displays a "Funding Approved" event with "Notes"
    Examples: 
      | priority | 
      | P2       | 
      | P3       | 
  
  # new steps for asserting timeline events not signed off. added for Triage tests      
  @notsignedoff @outOfHours 
  Scenario Outline: log job for contract store - "<priority>" fault, brand out of hours
    Given a "Helpdesk Operator" has logged in
      And a contract store with an available resource to work on asset types with priority "<priority>"
      And the call is outside the store brand hours
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a contractor
      And the resource status is "Job Advise Deferred"
      And the job is sitting in the "Deferred jobs" monitor
      And the job "is not" red flagged for immediate callout
      And the timeline displays an "Awaiting Funding Authorisation" event
      And the timeline does not display an "Email notification sent" event
      And the timeline displays a "Funding Approved" event with "Notes"
    Examples: 
      | priority | 
      | P2       | 
      | P3       | 
  
