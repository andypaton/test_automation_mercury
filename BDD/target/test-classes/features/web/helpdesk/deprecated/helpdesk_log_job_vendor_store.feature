@wip @deprecated
@helpdesk
Feature: Helpdesk - Log a Job for a Vendor store

  NOTES: 1. that only tests that are currently in hours / out of hours will run at anytime
         2. a Vendor store = a non contract store without a City Tech assigned 
         
  Background: System Feature Toggles are set for AHOLD
    Given the system feature toggle "JobDeferrals" is "disabled"
      And the system feature toggle "NewRoutingRules" is "enabled"
      And the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"

  @MTA-221
  Scenario Outline: log job for vendor store - P1 fault, contractor configured, helpdesk "<hours>"
    Given a "Helpdesk Operator" has logged in "<hours>"
      And a vendor store with a priority "P1" fault
      And the store does not have a city tech on a permanent assignment or on call 
      And a contractor is configured to the store
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
      And the timeline displays a "Funding Approved" event for "<amount>" with additional "Notes" details
    Examples: 
      | hours        | amount | 
      | in hours     | $500   | 
      | out of hours | $750   | 
  
  @notsignedoff
  Scenario: log job for vendor store - P1 fault, contractor configured, City Tech on on call
    Given a "Helpdesk Operator" has logged in
      And a vendor store with a priority "P1" fault
      And the store has a city tech on call 
      And a contractor is configured to the store
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is "Call Required"
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with additional details:
      | Notes | Status: Call Required | 
  
  Scenario: log job for vendor store - P1 fault, contractor not configured
    Given a "Helpdesk Operator" has logged in
      And a vendor store with a priority "P1" fault
      And a contractor is not configured to the store
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job is sitting in the "Awaiting Assignment" monitor
      And the job "is" red flagged for immediate callout
      
  Scenario Outline: log job for vendor store - "<priority>" fault, contractor not configured, brand in hours
    Given a "Helpdesk Operator" has logged in
      And a vendor store with a priority "<priority>" fault
      And the call is within the store brand hours
      And a contractor is not configured to the store
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job is sitting in the "Awaiting Assignment" monitor
      And the job "is not" red flagged for immediate callout
    Examples: 
      | priority | 
      | P2       | 
      | P3       | 
  
  Scenario Outline: log job for vendor store - "<priority>" fault, contractor not configured, brand out of hours
    Given a "Helpdesk Operator" has logged in
      And a vendor store with a priority "<priority>" fault
      And the call is outside the store brand hours
      And a contractor is not configured to the store
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job is sitting in the "Deferred jobs" monitor
      And the job "is not" red flagged for immediate callout
    Examples: 
      | priority | 
      | P2       | 
      | P3       | 
      
  Scenario: log job for vendor store - P2 fault, contractor configured
    Given a "Helpdesk Operator" has logged in
      And a vendor store with a priority "P2" fault
      And a contractor is configured to the store
      And the store does not have a city tech on a permanent assignment or on call 
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Tech Bureau Triage" status
      And the Job Details page is displayed
      And the job resource status is "None assigned"
      And the job is sitting in the "Tech Bureau Triage" monitor
      And a blue banner with "This job has been passed for Triage" is displayed 
      And the timeline displays a "Job in Technical Bureau triage" event
        
  Scenario: log job for vendor store - P3 fault, contractor configured, not Store walkround
    Given a "Helpdesk Operator" has logged in
      And a vendor store with a priority "P3" fault
      And the fault type is not Store Walkround
      And a contractor is configured to the store
      And the store does not have a city tech on a permanent assignment or on call 
      And a search is run for that site
      And the "Log a job" button is clicked
     When the New Job form is completed for a matching asset and priority    
      And the Job Contact is an existing contact
      And the job is saved    
     Then a new "Reactive" job is saved to the database with "Tech Bureau Triage" status
      And the Job Details page is displayed
      And the job resource status is "None assigned"
      And the job is sitting in the "Tech Bureau Triage" monitor
      And a blue banner with "This job has been passed for Triage" is displayed 
      And the timeline displays a "Job in Technical Bureau triage" event
  
