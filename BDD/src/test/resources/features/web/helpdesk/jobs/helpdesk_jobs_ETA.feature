@helpdesk @helpdesk_jobs @helpdesk_jobs_edit
Feature: Helpdesk - Jobs - ETA

  @usah
  Scenario: ETA Provided - ETA not Advised to Site - Resource has no iPad
    Given a "Helpdesk Operator" has logged in
     When they are accepting a job for a resource with no iPad
     Then the job is now sitting in the "ETA not advised to site" monitor
      And the timeline displays an "Job Provided With ETA" event with "ETA"
      And the ETA date and time provided are displayed for the resource
      
  @uswm @ukrb @usad
  Scenario: ETA Provided - ETA not Advised to Site - Resource has no iPad
    Given a "Helpdesk Operator" has logged in
     When they are accepting a job for a resource with no iPad
     Then the timeline displays an "Job Provided With ETA" event with "ETA"
      And the ETA date and time provided are displayed for the resource   
      
  @mcp
  Scenario: ETA Advised to contact  - Call Answered
    Given a "Helpdesk Operator" has logged in
      And a job with ETA not advised to site and "single" contact assigned is viewed
      And the ETA panel is displayed
     When the "Call Job Contact" button is clicked
      And the phone number to call is selected
      And the call is answered
      And the details are saved
     Then the job resource status is now "ETA Advised To Site"
      And the timeline displays a "Outbound call successful" event
      And the timeline displays a "ETA advised to site" event

  @mcp
  Scenario: ETA Advised to contact  - Call Not Answered
    Given a "Helpdesk Operator" has logged in
      And a job with ETA not advised to site and "single" contact assigned is viewed
      And the ETA panel is displayed
     When the "Call Job Contact" button is clicked
      And the phone number to call is selected
      And the call is not answered
      And the details are saved
     Then the job resource status is still "ETA Provided"
      And the timeline displays a "Outbound call unsuccessful" event
      
  @uswm @ukrb @usah
  Scenario: ETA Provided - Call answered with multiple contacts assigned
    Given a "Helpdesk Operator" has logged in
      And a job with ETA not advised to site and "multiple" contacts assigned is viewed
      And the ETA panel is displayed
     When the "Call Job Contact" button is clicked
      And the phone number to call is selected
      And the call is answered
      And the details are saved
     Then the job resource status is now "ETA Advised To Site"
      And the timeline displays a "Outbound call successful" event
      And the timeline displays a "ETA advised to site" event
      
  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoApproveContractorFundingRequests
  Scenario: ETA Provided - Call answered with single contact and multiple resources assigned - assigned to Contractor
    Given the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Operator" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the job is accepted and an additional resource added
     When the "Call Job Contact" button is clicked
      And the phone number to call is selected
      And the call is answered
      And the details are saved
     Then each resource status is now "ETA Advised To Site"
      And the job resource status is now "ETA Advised To Site"
      And the timeline displays a "Outbound call successful" event
      And the timeline displays a "ETA advised to site" event
      
  @mcp
  Scenario: ETA Provided - Call answered with single contact and multiple resources assigned - assigned to City Resource
    Given a "Helpdesk Operator" has logged in
      And they are accepting a job for a resource with no iPad
      And an additional "random" resource accepts the job
     When the "Call Job Contact" button is clicked
      And the phone number to call is selected
      And the call is answered
      And the details are saved
     Then each resource status is now "ETA Advised To Site"
      And the job resource status is now "ETA Advised To Site"
      And the timeline displays a "Outbound call successful" event
      And the timeline displays a "ETA advised to site" event
      
  @uswm @ukrb @usah
  Scenario: Acknowledge ETA - ETA is greater than SLA
    Given a "Helpdesk Operator" has logged in
      And a job where the ETA is greater than the SLA
     When the ETA is acknowledged
     Then the Job is removed from the "ETA greater than response/repair time" monitor
      And the timeline displays a "ETA Acknowledged" event
      
  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoApproveContractorFundingRequests
  Scenario: Acknowledge ETA - ETA is within SLA
    Given the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Operator" has logged in
      And a search is made for a Priority "3" job with contractor resource
      And the "Manage Resources" action is selected
      And the initial funding request is authorised for "contractor"
     When the job is accepted for the resource and an ETA is not advised to site
     Then the "Acknowledge ETA" action should not be available
     
  #Steps commented out until bug MCP-18615 is resolved
  #Business team have agreed that they are happy for bug to be in live
  @uswm @ukrb @usah
  @bugWalmart
  Scenario: Acknowledge ETA - ETA is greater than SLA, ETA Acknowledged then updated again [bug: MCP-18615]
    Given a "Helpdesk Operator" has logged in
      And a job where the ETA is greater than the SLA
      And the ETA is acknowledged
      And the "Acknowledge ETA" action is not now available
#      And the Job has been removed from the "ETA greater than response/repair time" monitor
      And the timeline displays a "ETA Acknowledged" event
      And the "ETA" action is selected
     When the resource ETA is updated and an ETA is not advised to site
     Then the "Acknowledge ETA" action should now be available
#      And the Job "is" added to the "ETA greater than response/repair time" monitor
        
                           