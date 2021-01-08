@wip @deprecated
@helpdesk
Feature: Helpdesk - Log a Job for a City Tech store
  
  NOTE: that only tests that are currently in hours / out of hours will run at anytime
  
  @notsignedoff
  Scenario: log job for city tech store - P1 fault, resource in working hours
    Given a "Helpdesk Operator" has logged in
     When a "P1" fault is logged against a City Tech store within the resource working hours
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with additional details:
      | Notes | Status: New Job Notification Sent | 
      And the timeline displays an "Notification and text message sent" event
  
  @inHours
  Scenario Outline: log job for city tech store - "<priority>" fault, brand in hours, resource in working hours
    Given a "Helpdesk Operator" has logged in
     When a "<priority>" fault is logged against a City Tech store within the brand and resource working hours
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Notification Window" monitor
      And the job "is not" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with additional details:
      | Notes | Status: New Job Notification Sent | 
      And the timeline displays an "Notification and text message sent" event
    Examples: 
      | priority | 
      | P2       | 
      | P3       | 
      
  Scenario Outline: log job for city tech store - "<priority>" fault, brand in hours, resource out of working hours
    Given a "Helpdesk Operator" has logged in
     When a "<priority>" fault is entered against a City Tech store outwith the resource working hours
      And the call is within the store brand hours
      And the job form is saved
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has not been assigned
      And the job resource status is "None assigned"
      And the job is sitting in the "Awaiting Assignment" monitor
      And the job "is not" red flagged for immediate callout
      And the timeline displays a "Job logged" event
    Examples: 
      | priority | 
      | P2       | 
      | P3       | 
  
  @outOfHours
  Scenario: log job for city tech store - P1 fault, resource out of working hours, City Tech on call
    Given a "Helpdesk Operator" has logged in
     When a "P1" fault is entered against a City Tech store outwith the resource working hours but with City Tech on call
      And both a City Tech and a Contractor are capable of fixing the fault
      And the job form is saved
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is "Call Required"
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with additional details:
      | Notes | Status: Call Required | 

  
  @outOfHours
  Scenario: log job for city tech store - P1 fault, resource out of working hours, City Tech not on call, contractor configured
    Given a "Helpdesk Operator" has logged in
     When a "P1" fault is entered against a City Tech store outwith the resource working hours
      And a contractor is configured to the store
      And the job form is saved
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a Contractor
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with additional details:
      | Notes | Status: Awaiting Funding Authorisation | 
      And the timeline displays an "Email notification sent" event
  
  @outOfHours
  Scenario: log job for city tech store - P1 fault, resource out of working hours, City Tech not on call, contractor not configured
    Given a "Helpdesk Operator" has logged in
     When a "P1" fault is entered against a City Tech store outwith the resource working hours
      And a contractor is not configured to the store
      And the job form is saved
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has not been assigned
      And the job resource status is "None assigned"
      And the job is sitting in the "Awaiting Assignment" monitor
      And the job "is" red flagged for immediate callout
      And the resource panel displays "No matching configured resources"
      And the timeline displays a "Job logged" event
  
  @outOfHours
  Scenario Outline: log job for city tech store - "<priority>" fault, brand out of working hours, not Store walkround
    Given a "Helpdesk Operator" has logged in
     When a "<priority>" fault is entered against a City Tech store outwith the brand working hours  
      And the fault type is not Store Walkround
      And the caller has not been advised to asign the job
      And the job form is saved
     Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And a banner shows Deferred Until date
      And the resource panel is blank
      And the job has not been assigned
      And the job resource status is "None assigned"
      And the job is sitting in the "Deferred jobs" monitor
    Examples: 
      | priority | 
      | P2       | 
      | P3       | 
