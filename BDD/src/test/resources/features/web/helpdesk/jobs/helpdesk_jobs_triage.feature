#@MCP-2889
@usah 
@helpdesk @helpdesk_jobs @helpdesk_jobs_triage
@toggles @JobDeferrals @NewRoutingRules @AutoAssign @AutoApproveContractorFundingRequests
Feature: Helpdesk - Jobs - Triage

  Background: System Feature Toggles are set for AHOLD
    Given the system feature toggle "JobDeferrals" is "disabled"
      And the system feature toggle "NewRoutingRules" is "enabled"
      And the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      
  #@MTA-89
  Scenario: Cancel Job
    Given a "Helpdesk Operator" has logged in
      And a job in Triage
      And the Technical Bureau Triage panel is viewed
     When hours, minutes, notes and outcome reason "Cancel Job" are saved
     Then the Cancellation panel is displayed for the job 
      And the job still displays Resource status "None assigned" and Client status "Tech Bureau Triage"
      And the timeline displays an "Tech-Bureau marked job for cancellation" event with "Time Spent, Notes"
 
  #@MTA-89
  Scenario: Stay in Triage
    Given a "Helpdesk Operator" has logged in
      And a job in Triage
      And the Technical Bureau Triage panel is viewed
     When hours, minutes, notes and outcome reason "Stay in Triage" are saved
     Then the job still displays Resource status "None assigned" and Client status "Tech Bureau Triage"
      And the timeline displays an "Tech-Bureau triage update" event with "Time Spent, Notes"
     
  #@MTA-89
  Scenario: Callout Resource
    Given a "Helpdesk Operator" has logged in
      And a job in Triage
      And the Technical Bureau Triage panel is viewed
     When hours, minutes, notes and outcome reason "Callout Resource" are saved
     Then the Manage Resources page is displayed
      And the resource panel is blank
      And the job still displays Resource status "None assigned" and Client status "Tech Bureau Triage"
      And the timeline displays an "Tech-Bureau assigning resources" event with "Time Spent, Notes"
     
  #@MTA-89
  Scenario: Complete Job
    Given a "Helpdesk Operator" has logged in
      And a job in Triage
      And the Technical Bureau Triage panel is viewed
     When hours, minutes, notes, outcome reason "Complete Job" and cause are saved
     Then the job still displays Resource status "None assigned" and Client status "Tech Bureau Triage"
      And the timeline displays an "Tech-Bureau marked job for completion" event with "Time Spent, Reason, Notes"
      And the timeline displays an "Job completed" event
      And the client status is now "Fixed"
