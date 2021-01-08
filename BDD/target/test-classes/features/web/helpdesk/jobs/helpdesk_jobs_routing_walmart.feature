@helpdesk @helpdesk_jobs @helpdesk_jobs_routing
@toggles @JobDeferrals @NewRoutingRules @AutoAssign @AutoApproveContractorFundingRequests
@wip
Feature: Helpdesk - Jobs - Routing - system feature toggles set for WALMART

##### NOTE : set to wip as functionality has changed and we have been asked to park these tests!

  Background: System Feature Toggles are set for WALMART
    Given the system feature toggle "JobDeferrals" is "disabled"
      And the system feature toggle "NewRoutingRules" is "disabled"
      And the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"    
      And the TestAutomationSite exists
    
  #@MTA-301
  Scenario: log P1 job - permanent City Tech available
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is" configured for the site
      And there "is" a City Tech resource available
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is one of "New Job Notification Sent, Call Required"
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event 
      And the timeline displays an "Notification" event

  #@MTA-309 @MCP-3269
  Scenario: log P1 job - permanent City Tech not available - resource On Call
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is" configured for the site
      And there "is not" a City Tech resource available
      And an On Call City Tech resource is available
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is one of "New Job Notification Sent, Call Required"
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event
      
  #@MTA-312
  Scenario: log P1 job - permanent City Tech not available - resource not On Call - Contractor is configured
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is" configured for the site
      And there "is not" a City Tech resource available
      And an On Call resource is not available
      And a Contractor "is" configured for the site
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Funding Requests" monitor
      And the Manage Resources page is displayed
      And the job has been assigned to a Contractor
      And the resource status is "Awaiting Funding Authorisation"
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with note "Awaiting Funding Authorisation" 
      And the timeline displays an "Awaiting Funding Authorisation" event
      
  #@MTA-476 @MCP-4037
  Scenario: log P1 job - permanent City Tech not available - resource not On Call - Contractor not configured
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is" configured for the site
      And there "is not" a City Tech resource available
      And an On Call resource is not available
      And a Contractor "is not" configured for the site
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has not been assigned
      And the job resource status is "None assigned"
      And the job is sitting in the "Awaiting Assignment" monitor
      And the job "is" red flagged for immediate callout
      And the resource panel displays "No matching configured resources"
      And the timeline displays a "Job logged" event
      
  #@MTA-417
  Scenario: log P1 job - only Contractor configured for site
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is not" configured for the site
      And a Contractor "is" configured for the site
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Funding Requests" monitor
      And the Manage Resources page is displayed
      And the job "is" red flagged for immediate callout
      And the job has been assigned to a Contractor
      And the resource status is "Awaiting Funding Authorisation"
      And the timeline displays a "Awaiting Funding Authorisation" event
      
  #@MTA-384 @MCP-4037
  Scenario: log P1 job - no resources configured for site
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is not" configured for the site
      And a Contractor "is not" configured for the site
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Awaiting Assignment" monitor
      And the Manage Resources page is displayed
      And the job "is" red flagged for immediate callout
      
  #@MTA-498
  Scenario: log P2/P3 job - IN HOURS - permanent City Tech available
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "IN HOURS"
      And there "is" a City Tech resource available
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is "New Job Notification Sent"
      And the job is sitting in the "Notification Window" monitor
      And the job "is not" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with note "New Job Notification Sent"
      And the timeline displays an "Notification and text message sent" event
       
  #@MTA-498
  Scenario: log P2/P3 job - IN HOURS - permanent City Tech not available - Contractor is configured
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "IN HOURS"
      And there "is not" a City Tech resource available
      And a Contractor "is" configured for the site
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a Contractor
      And the job is sitting in the "Funding Requests" monitor
      And the job "is not" red flagged for immediate callout
      And the resource status is "Awaiting Funding Authorisation"
      And the timeline displays a "Awaiting Funding Authorisation" event
      
  #@MTA-498
  Scenario: log P2/P3 job - IN HOURS - permanent City Tech not available - Contractor not configured
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "IN HOURS"
      And there "is not" a City Tech resource available
      And a Contractor "is not" configured for the site
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Awaiting Assignment" monitor
      And the Manage Resources page is displayed
      And the job "is not" red flagged for immediate callout
 
  #@MTA-498 
  @notsignedoff @rework
  Scenario: log P2/P3 job - OUT OF HOURS - outwith Deferral time - City Tech On Call
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "OUT OF HOURS" and "outwith" the brands Deferral time
      And an On Call City Tech resource is available
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Notification Window" monitor
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is "New Job Notification Sent"
      And the job "is not" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with note "New Job Notification Sent"
      And the timeline displays an "Notification and text message sent" event
       
  #@MTA-498
  @rework @notsignedoff
  Scenario: log P2/P3 job - OUT OF HOURS - outwith Deferral time - City Tech not On Call - Contractor is configured
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "OUT OF HOURS" and "outwith" the brands Deferral time
      And an On Call resource is not available
      And a Contractor "is" configured for the site
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the Manage Resources page is displayed
      And the job has been assigned to a Contractor
      And the job is sitting in the "Funding Requests" monitor
      And the job "is not" red flagged for immediate callout
      And the resource status is "Awaiting Funding Authorisation"
      And the timeline displays a "Awaiting Funding Authorisation" event
      
  #@MTA-498
  @rework @notsignedoff
  Scenario: log P2/P3 job - OUT OF HOURS - during Deferral time - City Tech not On Call - Contractor is configured - defer until Now
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "OUT OF HOURS" and "during" the brands Deferral time
      And an On Call resource is not available
      And a Contractor "is" configured for the site
      And the job is logged with deferred until "Now"
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Funding Requests" monitor
      And the Manage Resources page is displayed
      And the job has been assigned to a Contractor
      And the resource status is "Awaiting Funding Authorisation"
      And the job "is not" red flagged for immediate callout
      And the timeline displays a "Resource Added" event with note "Awaiting Funding Authorisation" 
      And the timeline displays an "Awaiting Funding Authorisation" event

  #@MTA-498
  Scenario: log P2/P3 job - OUT OF HOURS - during Deferral time - City Tech On Call - Contractor is configured - defer until first available date
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "OUT OF HOURS" and "during" the brands Deferral time
      And an On Call City Tech resource is available
      And a Contractor "is" configured for the site
      And the job is logged with deferred until "first available date"
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Deferred Job" monitor
      And the Manage Resources page is displayed
      And a banner shows Deferred Until date
      And the job has not been assigned
      And the job resource status is "None assigned"
      
  # The result of the following scenario is the same whether IN HOURS or OUT OF HOURS 
  #@MTA-539
  Scenario Outline: log P1 job - Deferral Question - "<immediateCallout>" Immediate Callout
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault "<immediateCallout>" Immediate Callout is being logged "<deferralTime>" the brands Deferral time
     Then the Deferral Question "<presented>" presented
    Examples:
     | immediateCallout | deferralTime | presented |
     | with             | during       | is not    |
     | without          | during       | is        |
     | with             | outwith      | is not    |
     | without          | outwith      | is not    |
          
  # The result of the following scenario is the same whether IN HOURS or OUT OF HOURS 
  #@MTA-525
  Scenario Outline: log P2/P3 job - Deferral Question - "<deferralTime>" the brands Deferral time
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P2/P3" fault is being logged "<deferralTime>" the brands Deferral time
     Then the Deferral Question "<presented>" presented
    Examples:
     | deferralTime | presented |
     | during       | is        |
     | outwith      | is not    |