@helpdesk @helpdesk_funding_requests @helpdesk_funding_requests_approve
Feature: Helpdesk - Funding Requests - Auto Approve
  
  #@mcp - tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Auto Approve Contractor Funding Requests enabled - Initial funding request auto approved, Uplift NOT auto approved
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Operator" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the job is accepted for the resource and an ETA is not advised to site
     When a new uplift funding request is created with known amount  
     Then the timeline displays a "Funding Approved" event with "Notes"
      And the timeline displays a "Awaiting Funding Authorization" event for the uplift with "Notes"
     
  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Auto Approve Contractor Funding Requests disabled - Initial funding request NOT auto approved
        * using dataset "helpdesk_funding_requests_auto_approve_001"
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing City Tech caller
     When the "Log a job" button is clicked
      And a City Tech caller is entered
      And an asset, description and fault type are entered
      And the Job Contact is the same as caller
      And a contractor is assigned to the job
      And the job is saved
     Then a new "Reactive" job is saved to the database
      And the timeline does not display a "Funding Approved" event
      And the timeline displays a "Awaiting Funding Authorization" event
  
  @ukrb
  @toggles @AutoAssign @AutoApproveContractorFundingRequests 
  Scenario: Auto Approve Contractor Funding Requests disabled - Initial funding request auto approved for chargeable contractor with zero price in the pricebook
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Operator" has logged in
     When a job assigned to chargeable contractor with zero price in the pricebook at a vendor store
     Then the job resource status is one of "New Job Notification Sent"
      And the client status is now "Logged"
      And the timeline displays a "Awaiting Funding Authorization" event
      And the timeline displays a "Funding Approved" event with "Notes"
      And the timeline displays a "Email notification sent" event
  
  @ukrb
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Auto Approve Contractor Funding Requests disabled - Initial funding request auto approved for non-chargeable contractor with or without zero price in the pricebook 
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Operator" has logged in
     When a job assigned to non-chargeable contractor with or without zero price in the pricebook at a vendor store
     Then the job resource status is one of "New Job Notification Sent"
      And the client status is now "Logged"
      And the timeline displays a "Resource Added" event with note "Status: New Job Notification Sent"
      And the timeline displays a "Email notification sent" event