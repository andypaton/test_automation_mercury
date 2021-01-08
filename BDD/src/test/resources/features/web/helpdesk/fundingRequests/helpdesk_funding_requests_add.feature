@helpdesk @helpdesk_funding_requests @helpdesk_funding_requests_add
Feature: Helpdesk - Funding Requests - Add
 
  @mcp
  Scenario: Prevent user adding a funding request to a Fixed job - chargeable contractor
    Given a "Helpdesk Manager" has logged in
      And a search is run for a "Reactive" job in "Fixed" status assigned to a "Contractor who is always chargeable and without zero price in the pricebook" resource
      And the "Manage Resources" action is selected
     When the "Funding requests" action is selected
     Then the Amount and Description fields are not displayed in the funding request panel
      And the save button is greyed out in the funding request panel
      And the cancel button is enabled
      
  @ukrb
  Scenario: Prevent user adding a funding request to a Fixed job - non chargeable contractor
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "Reactive" job in "Fixed" status assigned to a "Contractor who is non-chargeable" resource
      When the "Manage Resources" action is selected
      Then the "Funding requests" action should not be available
   
  # Removing mcp tag as BT confirmed this is not valid when there is no resource assigned and AutoAssign toggle is ON for all environments
  @toggles @AutoAssign
  Scenario: Verify that the Convert to quote job panel will be closed when a user selects the Cancel button
    Given a "Helpdesk Manager" has logged in
      And a "Reactive" job with status "Logged"
      And no resource is assigned
      And a search is run for the job reference
      And the "Quotes" action is selected
     When the "Cancel" button is clicked
     Then the convert to quote screen panel will not be visible
      And the convert to quote job button is not displayed
 
  # Removing mcp tag as BT confirmed this is not valid when there is no resource assigned and AutoAssign toggle is ON for all environments
  @toggles @AutoAssign
  Scenario Outline: Add a "<quoteType>" quote request to an existing job with no assignment - job type not changed
    Given a "Helpdesk Manager" has logged in
      And a "Reactive" job with status "Logged"
      And no resource is assigned
      And the job type is not changed
      And a search is run for the job reference
      And the "Quotes" action is selected
     When the "Convert to quote job" button is clicked
      And the resultant draft quote job is saved after selecting "<quoteType>" as the quote type
      And the reason for changes made to the job is confirmed
     Then the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Raised" event
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver" 
      And the timeline displays a "Job Type changed to Quote" event with "Change, Notes, Reason"
    Examples: 
      | quoteType | 
      | OPEX      | 
      | CAPEX     |

  # Removing mcp tag as BT confirmed this is not valid when there is no resource assigned and AutoAssign toggle is ON for all environments
  @toggles @AutoAssign
  Scenario Outline: Add a "<quoteType>" quote request to an existing job with no assignment - job type changed
    Given a "Helpdesk Manager" has logged in
      And a "Reactive" job with status "Logged"
      And no resource is assigned
      And the job type is changed
      And a search is run for the job reference
      And the "Quotes" action is selected
     When the "Convert to quote job" button is clicked
      And the resultant draft quote job is saved after selecting "<quoteType>" as the quote type
      And the reason for changes made to the job is confirmed
     Then the client status is now "Awaiting Quote Request Review"
      And the timeline displays a "Quote Request Raised" event
      And the timeline displays a "Quote Request Approver Set" event with "Quote request approver" 
      And the timeline displays a "Job Type changed to Quote" event with "Change, Notes, Reason"
    Examples: 
      | quoteType | 
      | OPEX      | 
      | CAPEX     |  
  
  # Removing mcp tag as BT confirmed this is not valid when there is no resource assigned and AutoAssign toggle is ON for all environments
  @toggles @AutoAssign
  Scenario: Adding a quote request to an existing job with no assignment - Text to display on convert to quote job screen
    Given a "Helpdesk Manager" has logged in
      And a "Reactive" job with status "Logged"
      And no resource is assigned
      And the job type is not changed
      And a search is run for the job reference
     When the "Quotes" action is selected
     Then "This job has no resources assigned to it, so it can be converted into a quote job." text is shown on the convert to quote job screen
   
  #@mcp - tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - User should be shown previous funding request details - AutoApproveContractorFundingRequests enabled
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
      And a "Helpdesk Manager" has logged in
      And a job assigned to contractor at a vendor store with an additional contractor resource
      And the "Manage Resources" action is selected
     When the "Funding requests" action is selected
     Then the previous funding request details are displayed for that resource for that job
      And the cancel button is disabled for the initial funding request
  
  @mcp @toggles @AutoApproveContractorFundingRequests
  Scenario: Uplift/further funding request for contractor - User should be shown previous funding request details - AutoApproveContractorFundingRequests disabled
    Given the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Manager" has logged in
      And a job accepted by a contractor
     When the "Manage Resources" action is selected
      And the "Funding requests" action is selected
     Then the previous funding request details are displayed for that resource for that job
      And the cancel button is disabled for the initial funding request   