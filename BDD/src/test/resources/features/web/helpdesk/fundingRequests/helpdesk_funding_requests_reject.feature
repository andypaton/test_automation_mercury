@helpdesk @helpdesk_funding_requests @helpdesk_funding_requests_reject
Feature: Helpdesk - Funding Requests - Reject

  @bugWalmart @bugAdvocate @mcp @toggles @AutoAssign
  Scenario Outline: Job for additional Contractor - Funding above agreed call out rate - Rejected - Rejection Reason - "<Rejection Reason>" [bug: MCP-20571]
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
      And an additional "contractor" resource is added
      And the callout rate is increased
     When the initial funding request is rejected for "additional contractor" resource with reason "<Rejection Reason>" and notes
     Then the job resource status is now "Funding Request Rejected"
      And the client status is now "Logged"
      And the timeline displays a "Funding Declined" event with "Notes"
    Examples: 
      | Rejection Reason            | 
      | Duplicate Job               | 
      | Works No Longer Required    | 
      | Warranty Job                | 
      | Select Alternative Resource | 
  

  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Uplift/further funding request for contractor - Rejecting - "<Rejection Reason>" - Toggle AutoApproveContractorFundingRequests - enabled
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource     
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount
     When the uplift funding request is rejected for "contractor" resource with reason "<Rejection Reason>" and notes
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Declined" event with "Notes"
    Examples: 
      | Rejection Reason         | 
      | Duplicate Job            | 
      | Works No Longer Required | 
      | Warranty Job             |
 
  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - Rejecting - Select Alternative Resource - Toggle AutoApproveContractorFundingRequests - enabled
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource      
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount
     When the uplift funding request is rejected for "contractor" resource with reason "Select Alternative Resource" and notes
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Declined" event with "Notes"
      And the Additional Resource Required section is shown where user will be able to select an Alternative Resource to pass the job to
  
  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Uplift/further funding request for contractor - Rejecting - Request a "<Quote Type>" Quote - Toggle AutoApproveContractorFundingRequests - enabled
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource      
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount
     When the uplift funding request is rejected for "contractor" resource with reason "Request a Quote" and notes
      And the resultant draft quote job is saved after selecting "<Quote Type>" as the quote type
     Then a linked quote job is created
      And the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver"
    Examples: 
      | Quote Type | 
      | OPEX       | 
      | CAPEX      | 
  
  @bugWalmart @bugAdvocate @mcp
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Uplift/further funding request for contractor - Rejecting - "<Rejection Reason>" - Toggle AutoApproveContractorFundingRequests - disabled [bug: MCP-20583]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
      And the initial funding request is authorised for "contractor"
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount 
     When the uplift funding request is rejected for "contractor" resource with reason "<Rejection Reason>" and notes
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Declined" event with "Notes"
    Examples: 
      | Rejection Reason            | 
      | Duplicate Job               | 
      | Warranty Job                | 
      | Works No Longer Required    | 

  @bugWalmart @bugAdvocate @mcp
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - Rejecting - Select Alternative Resource - Toggle AutoApproveContractorFundingRequests - disabled [bug: MCP-20583]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
      And the initial funding request is authorised for "contractor"
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount     
     When the uplift funding request is rejected for "contractor" resource with reason "Select Alternative Resource" and notes
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Funding Declined" event with "Notes"
      And the Additional Resource Required section is shown where user will be able to select an Alternative Resource to pass the job to
  
  @mcp @bugWalmart
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Uplift/further funding request for contractor - Rejecting - Request a "<Quote Type>" Quote - Toggle AutoApproveContractorFundingRequests - disabled [bug: MCP-20321]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
      And the initial funding request is authorised for "contractor"
      And the job is accepted for the resource and an ETA is not advised to site
      And a new uplift funding request is created with known amount     
     When the uplift funding request is rejected for "contractor" resource with reason "Request a Quote" and notes
      And the resultant draft quote job is saved after selecting "<Quote Type>" as the quote type
     Then a linked quote job is created
      And the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver"
    Examples: 
      | Quote Type | 
      | OPEX       | 
      | CAPEX      | 
  
  @mcp @bugWalmart
  @toggles @AutoAssign
  Scenario Outline: ARR Vendor requires Funding above agreed call out rate rejected with request a quote reason - "<Quote Type>" [bug: MCP-20321]
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Manager" has logged in      
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
      And an additional "contractor" resource is added
      And the callout rate is increased
     When the initial funding request is rejected for "additional contractor" resource with reason "Request a Quote" and notes
      And the resultant draft quote job is saved after selecting "<Quote Type>" as the quote type
     Then a linked quote job is created
      And the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver"
    Examples: 
      | Quote Type | 
      | OPEX       | 
      | CAPEX      | 
  
  # Scenario not valid for Advocate as all contractors have an agreed callout rate
  @bugWalmart @uswm @ukrb @usah
  @toggles @AutoAssign
  Scenario Outline: Job for Contractor - No Pre Approved Funding amount and rejected - "<Rejection Reason>" [bug: MCP-20571]
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with no configured rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "<Rejection Reason>" and notes
     Then the job resource status is now "Funding Request Rejected"
      And the client status is now "Logged"
      And the timeline displays a "Funding Declined" event with "Notes"
      And the Additional Resource Required section is shown where user will be able to select an Alternative Resource to pass the job to
    Examples: 
      | Rejection Reason            | 
      | Select Alternative Resource | 
      | Warranty Job                | 
  
  # The Additional Resource Required section is not shown for the following rejection reasons
  @uswm @ukrb @usah
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Job for Contractor - No Pre Approved Funding amount and rejected - "<Rejection Reason>"
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with no configured rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "Works No Longer Required" and notes
     Then the job resource status is now "Funding Request Rejected"
      And the client status is now "Cancelled"
      And the "Manage Resources" action should not be available
      And the timeline displays a "Funding Declined" event with "Notes"
      And the timeline displays a "Job cancelled" event
    Examples: 
      | Rejection Reason         | 
      | Duplicate Job            | 
      | Works No Longer Required |
  
  # Scenario not valid for Advocate as all contractors have an agreed callout rate
  @uswm @ukrb @usah
  @toggles @AutoAssign
  Scenario Outline: Job for Contractor - No Pre Approved Funding amount and rejected - Request a Quote - Quote Type - "<Quote Type>"
    Given the system feature toggle "AutoAssign" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with no configured rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "Request a Quote" and notes
      And the resultant draft quote job is saved after selecting "<Quote Type>" as the quote type
      And the reason for changes made to the job is confirmed
     Then the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Raised" event
      And the timeline displays a "Job Type changed to Quote" event with "Change, Notes, Reason"
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver"
    Examples: 
      | Quote Type | 
      | CAPEX      | 
      | OPEX       |
  
  @bugWalmart @mcp
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Contractor Callout - Agreed callout rate > Rejected - "<Rejection Reason>" [bug: MCP-20571]
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with agreed call out rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "<Rejection Reason>" and notes
     Then the job resource status is now "Funding Request Rejected"
      And the client status is now "Logged"
      And the timeline displays a "Funding Declined" event with "Notes"
      And the Additional Resource Required section is shown where user will be able to select an Alternative Resource to pass the job to
    Examples: 
      | Rejection Reason            | 
      | Warranty Job                | 
      | Select Alternative Resource | 
  
  # The Additional Resource Required section should not be shown for the following rejection reasons
  @mcp
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Contractor Callout - Agreed callout rate > Rejected - "<Rejection Reason>"
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"     
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with agreed call out rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "Works No Longer Required" and notes
     Then the job resource status is now "Funding Request Rejected"
      And the client status is now "Cancelled"
      And the "Manage Resources" action should not be available
      And the timeline displays a "Funding Declined" event with "Notes"
      And the timeline displays a "Job cancelled" event
    Examples: 
      | Rejection Reason         | 
      | Duplicate Job            | 
      | Works No Longer Required |
  
  #This test is for Walmart only as BMI quote option is disabled in Rainbow as part of the Budget Review functionality
  @uswm
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Contractor Callout - Agreed callout rate > Rejected - Request a Quote - "<Quote Type>"
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"        
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with agreed call out rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "Request a Quote" and notes
      And the resultant draft quote job is saved after selecting "<Quote Type>" as the quote type
      And the reason for changes made to the job is confirmed
     Then the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Raised" event
      And the timeline displays a "Job Type changed to Quote" event with "Change, Notes, Reason"
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver"
    Examples: 
      | Quote Type | 
      | BMI        | 
      
  @mcp
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Contractor Callout - Agreed callout rate > Rejected - Request a Quote - "<Quote Type>"
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"        
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with agreed call out rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "Request a Quote" and notes
      And the resultant draft quote job is saved after selecting "<Quote Type>" as the quote type
      And the reason for changes made to the job is confirmed
     Then the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Raised" event
      And the timeline displays a "Job Type changed to Quote" event with "Change, Notes, Reason"
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver"
    Examples: 
      | Quote Type | 
      | OPEX       | 
      | CAPEX      | 
     
  # OOC funding route is not available for Advocate 
  @uswm @ukrb @usah
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario Outline: Contractor Callout - Agreed callout rate > Rejected - Request a Quote - "<Quote Type>"
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"        
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor with agreed call out rate at a vendor store
      And the "Manage Resources" action is selected
     When the initial funding request is rejected for "contractor" resource with reason "Request a Quote" and notes
      And the resultant draft quote job is saved after selecting "<Quote Type>" as the quote type
      And the reason for changes made to the job is confirmed
     Then the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Raised" event
      And the timeline displays a "Job Type changed to Quote" event with "Change, Notes, Reason"
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver"
    Examples: 
      | Quote Type | 
      | OOC        | 
  