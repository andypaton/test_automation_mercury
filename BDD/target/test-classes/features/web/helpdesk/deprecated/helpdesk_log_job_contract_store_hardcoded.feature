@wip @deprecated
@helpdesk
Feature: log a new job from the helpdesk for a contract store
      
  All scenarios have been replaced with dynamic tests
  Keeping these ones as they are useful for debugging specific test cases
  
  
  
  @hardcoded @wip @inHours
  Scenario: log a job for a contract store in hours for P1 fault
    Given a Helpdesk Operator has logged in
      And a search is run for site "2414" in brand hours
      And the "Log a job" button is clicked
    When the New Job form is completed with details:
      | subtype      | location  | fault   | 
      | Lobster Tank | Mezzanine | P2 chased out of hours Escalated by RFM |
      And the Job Contact is an existing contact
      And the job is saved
    Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed    
      And the job has been assigned to a contractor
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the job "is" red flagged for immediate callout

      
  @hardcoded @inHours
  Scenario: log a job for a contract store in hours for P2 fault
    Given a Helpdesk Operator has logged in
      And a search is run for site "0701" in brand hours
      And the "Log a job" button is clicked
    When the New Job form is completed with details:
      | caller                         | subtype          | location           | fault                   | assignTo |
      | John Urbaez (RHVAC Technician) | Ice Merchandiser | Department - Dairy | Corrigo Standard Repair | PIERCE REFRIGERATION INC |
      And the Job Contact is an existing contact
      And the job is saved
    Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed    
      And the job has been assigned to a Contractor
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Notification Window" monitor
      And the job "is not" red flagged for immediate callout
    
  @hardcoded @inHours
  Scenario: log a job for a contract store in hours for P3 fault
    Given a Helpdesk Operator has logged in
      And a search is run for site "0035" in brand hours
      And the "Log a job" button is clicked
    When the New Job form is completed with details:
      | caller                      | subtype              | location                      | fault           | assignTo  |
      | R-9-10-R (RHVAC Technician) | Refrigerant Recovery | Rest Room - Customer Handicap | Store Walkround | GLOBAL MECHANICAL SERVICES INC |
      And the Job Contact is an existing contact
      And the job is saved
    Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed    
      And the job has been assigned to a contractor
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Notification Window" monitor
      And the job "is not" red flagged for immediate callout

  @hardcoded @outOfHours
  Scenario: log a job for a contract store out of hours for P1 fault
    Given a Helpdesk Operator has logged in
      And a search is run for site "2611" out of brand hours
      And the "Log a job" button is clicked
    When the New Job form is completed with details:
      | caller        | subtype | location        | fault    | 
      | youngberg (-) | MT Rack | Electrical Room | Gas Leak | 
      And the Job Contact is an existing contact
      And the job is saved
    Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed    
      And the job has been assigned to a contractor
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the job "is" red flagged for immediate callout
          
  @hardcoded @outOfHours
  Scenario: log a job for a contract store out of hours for P2 fault
    Given a Helpdesk Operator has logged in
      And a search is run for site "0482" out of brand hours
      And the "Log a job" button is clicked
    When the New Job form is completed with details:
      | caller                      | subtype   | location          | fault                   | assignTo  |
      | R-9-10-R (RHVAC Technician) | Salad Bar | Department - Meat | Corrigo Standard Repair | GLOBAL MECHANICAL SERVICES INC |
      And the Job Contact is an existing contact
      And the job is saved
    Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed    
      And the job has been assigned to a contractor
      And the resource status is "Job Advise Deferred"
      And the job is sitting in the "Deferred jobs" monitor
      And the job "is not" red flagged for immediate callout
    
  @hardcoded @outOfHours
  Scenario: log a job for a contract store out of hours for P3 fault
    Given a Helpdesk Operator has logged in
      And a search is run for site "6272" out of brand hours
      And the "Log a job" button is clicked
    When the New Job form is completed with details:
      | caller                     | subtype   | location  | fault | assignTo  |
      | R-3-3-R (RHVAC Technician) | Olive Bar | Mezzanine | Noisy | ROK Mechanical |
      And the Job Contact is an existing contact
      And the job is saved
    Then a new "Reactive" job is saved to the database with "Logged" status
      And the Manage Resources page is displayed    
      And the job has been assigned to a contractor
      And the resource status is "Job Advise Deferred"
      And the job is sitting in the "Deferred jobs" monitor
      And the job "is not" red flagged for immediate callout

  