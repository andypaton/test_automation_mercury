@helpdesk @helpdesk_jobs @helpdesk_jobs_auto_callout
@toggles @FaultPriorityDeferralsIncludeContractors @ShowJobDeferralQuestions
Feature: Helpdesk - Jobs - Auto callout deferred jobs

  Auto callout after deferral date passes for deferred jobs

  #environment tag removed because all environments have FaultPriorityDeferralsIncludeContractors set to disabled   
  Background: 
    Given the system feature toggle "FaultPriorityDeferral" is "enabled" 
      And the system sub feature toggle "FaultPriorityDeferralsIncludeContractors" is "enabled" 
      And the system feature toggle "JobDeferrals" is "enabled"
      And the system sub feature toggle "Show Job Deferral Questions" is "enabled"

  Scenario: call out City Tech resource with iPad
    Given a "Helpdesk Operator" has logged in
      And a deferred job assigned to a City Tech resource "with" an iPad
      And the Manage Resources panel is viewed
     When the deferral date passes
      And the resources working hours commence
      And the "Process Deferred Jobs" job runs
     Then the resource status is now "New Job Notification Sent"
      And the timeline displays a "Notification" event logged by "System User"
      And the timeline displays a "Job Undeferred" event logged by "System User"
      And if Helpdesk is IN HOURS then the job is now sitting in the "Awaiting Acceptance" monitor, else the "Deferred jobs" monitor
      
  Scenario: call out City Tech resource without iPad
    Given a "Helpdesk Operator" has logged in
      And a deferred job assigned to a City Tech resource "without" an iPad
      And the Manage Resources panel is viewed
     When the deferral date passes
      And the resources working hours commence
      And the "Process Deferred Jobs" job runs
     Then the resource status is now "Call Required"
      And the timeline does not display a "Notification" event
      And the timeline displays a "Job Undeferred" event logged by "System User"
      
  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoApproveContractorFundingRequests
  Scenario: call out Contractor resource with an email address
    Given the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Operator" has logged in
      And a deferred job assigned to a Contractor resource "with" an email address
      And the Manage Resources panel is viewed
     When the deferral date passes
      And the resources working hours commence
      And the "Process Deferred Jobs" job runs
     Then the resource status is now "New Job Notification Sent"
      And the timeline displays an "Email notification sent" event logged by "System User"
      And the timeline displays a "Job Undeferred" event logged by "System User"
      