@helpdesk @helpdesk_funding_requests @helpdesk_funding_requests_cancel
Feature: Helpdesk - Funding Requests - Cancel

  #Commented out the last step due to bug MCP-11778 as discussed with BT until bug is fixed
  @bugRainbow @mcp
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Cancel Approved Uplift - Toggle AutoApproveContractorFundingRequests - disabled [bug:MCP-21438]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
      And the initial funding request is authorised for "contractor"
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount  
      And the uplift funding request is authorised for "contractor"
     When the approved uplift is cancelled for the "contractor"
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
   #   And the timeline displays a "Confirmed Uplift Cancelled" event with "Info, Uplift cancellation authorized by, Reason, Notes"
  
  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  #Commented out the last step due to bug MCP-11778 as discussed with BT until bug is fixed
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Cancel Approved Uplift - Toggle AutoApproveContractorFundingRequests - enabled
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount  
      And the uplift funding request is authorised for "contractor"
     When the approved uplift is cancelled for the "contractor"
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
  #    And the timeline displays a "Confirmed Uplift Cancelled" event with "Info, Uplift cancellation authorized by, Reason, Notes"