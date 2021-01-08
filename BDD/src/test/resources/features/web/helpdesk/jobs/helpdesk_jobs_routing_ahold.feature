@usah 
@helpdesk @helpdesk_jobs @helpdesk_jobs_routing
@toggles @JobDeferrals @NewRoutingRules @AutoAssign @AutoApproveContractorFundingRequests
@wip @deprecated
Feature: Helpdesk - Jobs - Routing - system feature toggles set for AHOLD

  Background: System Feature Toggles are set for AHOLD
    Given the system feature toggle "JobDeferrals" is "disabled"
      And the system feature toggle "NewRoutingRules" is "enabled"
      And the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
  
  #@MTA-276
  Scenario: log a job - P1 - City Tech - In Hours
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is" configured for the site
      And there "is" a City Tech resource available within working hours
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the Manage Resources page is displayed
      And the job has been assigned to a City Technician
      And the resource status is one of "New Job Notification Sent, Call Required"
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event

  #@MTA-317
  Scenario: log a job - P1 - City Tech - Out Of Hours - Resource On Call
    Given a "Helpdesk Operator" has logged in
     When a new job for a "P1" fault is being logged
      And a City resource "is" configured for the site
      And there "is not" a City Tech resource available within working hours
      And the Rota "has" a resource available On Call
      And the job is logged
     Then a new job is saved to the database with "Logged" status
      And the job is sitting in the "Awaiting Acceptance" monitor
      And the Manage Resources page is displayed
      And the resource status is one of "New Job Notification Sent, Call Required"
      And the job "is" red flagged for immediate callout
      And the timeline displays a "Resource Added" event 