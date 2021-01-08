@helpdesk @helpdesk_funding_requests @helpdesk_funding_requests_authorise
Feature: Helpdesk - Funding Requests - Authorize
 
  #@mcp - tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @sanity
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - Amount known - Authorise the amount - Toggle AutoApproveContractorFundingRequests - enabled
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount  
     When the uplift funding request is authorised for "contractor"
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Approved" event for the uplift with "Notes"
      And a Works Order has been written to the database for the uplift amount
  
  #@mcp - tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - Amount Unknown - Authorise the amount - Toggle AutoApproveContractorFundingRequests - enabled
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with unknown amount  
     When the uplift funding request is authorised for "contractor"
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Approved" event for the uplift with "Notes"
      
  #Commented out the last step due to bug MCP-18532 as discussed with BT until bug is fixed
  @bugRainbow
  @mcp @toggles @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - Job cost exceeds FinanceNotificationValue  - Authorise the amount - Toggle AutoApproveContractorFundingRequests - disabled [bug: MCP-18532 ukrb]
    Given the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job accepted by a contractor
      And the "Manage Resources" action is selected
      And a new uplift funding request is created such that job cost exceeds FinanceNotificationValue  
     When the uplift funding request is authorised for "contractor"
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Approved" event for the uplift with "Notes" 
  #    And an email is sent for "City Help Desk - Job Cost Exceeds"
  
  @mcp @toggles @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - Amount known - Authorise the amount - Toggle AutoApproveContractorFundingRequests - disabled
    Given the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job accepted by a contractor
      And the "Manage Resources" action is selected
      And a new uplift funding request is created with known amount  
     When the uplift funding request is authorised for "contractor"
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Approved" event for the uplift with "Notes"
  
  @mcp @toggles @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - Amount Unknown - Authorise the amount - Toggle AutoApproveContractorFundingRequests - disabled
    Given the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job accepted by a contractor
      And the "Manage Resources" action is selected
      And a new uplift funding request is created with unknown amount  
     When the uplift funding request is authorised for "contractor"
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Approved" event for the uplift with "Notes"
 
  @uswm
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Uplift - modify funding request route - "<route>"
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
      And the initial funding request is authorised for "contractor" with funding route "<route>"
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount  
     When the uplift funding request is being authorised for the "contractor"
     Then the funding route will default to "<route>"
      And the funding route "<update>" be updated
    Examples:
      | route | update |
      | OPEX  | can    | 
      | OOC   | cannot |
      | CAPEX | cannot |
      | BMI   | cannot |
        
  @bugWalmart @mcp @toggles @AutoAssign
  Scenario: ARR - Resource is Vendor - Vendor requires Funding above agreed call out rate - Authorised [bug: MCP-20571]
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store
      And the "Manage Resources" action is selected
      And an additional "contractor" resource is added
      And the callout rate is increased
     When the initial funding request is authorised for "additional contractor"
     Then the job resource status is one of "New Job Notification Sent, Job Advise Deferred"
      And the client status is now "Logged"
      And the amount field in the funding request will show the new total approved amount for the "additional contractor"
      And the timeline displays a "Funding Approved" event with "Notes"
      And the timeline displays a "Email notification sent" event
  
  # Scenario not valid for Advocate as all contractors have an agreed callout rate
  #Commented out the last step due to bug MCP-18532 as discussed with BT until bug is fixed  
  @bugRainbow @bugWalmart
  @uswm @ukrb @usah @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Job for contractor - no pre-approved amount known and funding authorised - Toggle AutoApproveContractorFundingRequests - disabled [bug: MCP-18532 ukrb]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with no configured rate at a vendor store
      And the "Manage Resources" action is selected
     When the funding request is authorised for "contractor" resource with no pre-approved amount
     Then the job resource status is one of "New Job Notification Sent, Job Advise Deferred"
      And the client status is now "Logged"
      And the timeline displays a "Funding Approved" event with "Notes"
      And the timeline displays a "Email notification sent" event
   #   And an email is sent for "City Help Desk"
  
  #@mcp - tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  #Commented out the last step due to bug MCP-18532 as discussed with BT until bug is fixed
  @bugRainbow
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Job for contractor - no pre-approved amount known and funding authorised - Toggle AutoApproveContractorFundingRequests - enabled [bug: MCP-18532 ukrb]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with no configured rate at a vendor store
      And the "Manage Resources" action is selected
     When the funding request is authorised for "contractor" resource with no pre-approved amount
     Then the job resource status is one of "New Job Notification Sent, Job Advise Deferred"
      And the client status is now "Logged"
      And the timeline displays a "Funding Approved" event with "Notes"
      And the timeline displays a "Email notification sent" event
   #   And an email is sent for "City Help Desk"
     
  #Commented out the last step due to bug MCP-18532 as discussed with BT until bug is fixed
  @bugRainbow
  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Contractor Callout - Agreed callout rate  > Authorised - Toggle AutoApproveContractorFundingRequests - disabled [bug: MCP-18532 ukrb]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
     When the initial funding request is authorised for "contractor"
     Then the job resource status is one of "New Job Notification Sent, Job Advise Deferred"
      And the client status is now "Logged"
      And the timeline displays a "Funding Approved" event with "Notes"
      And the timeline displays a "Email notification sent" event
   #   And an email is sent for "City Help Desk"

  @mcp @toggles @AutoAssign
  Scenario: RFM name should prepopulate in Authorized By field
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store
      And the "Manage Resources" action is selected
     When the "Funding requests" action is selected
      And authorize action is selected for "contractor"
     Then the "RFM" name is prepopulated in the Authorized By field
     