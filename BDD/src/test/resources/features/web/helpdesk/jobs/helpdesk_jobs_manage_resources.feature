@helpdesk @helpdesk_manage_resource
Feature: Helpdesk - Jobs - Manage a Resource
    
  #@MTA-100 @MTA-120 @MTA-221
  @usah
  Scenario: Callout Contractor for Triage job - job deferrals disabled
    Given a "Helpdesk Operator" has logged in
      And a job in Triage awaiting callout resource assignment
      And the Manage Resources panel is viewed
     When a Contractor is selected and saved
     Then the job resource status is now "New Job Notification Sent"
      And the timeline displays a "Resource Added" event with note "Awaiting Funding Authorisation"
      And the timeline displays an "Awaiting Funding Authorisation" event
      And the timeline displays a "Funding Approved" event with note "Amount authorized automatically"
      And the timeline displays an "Email notification sent" event
      
  #@MTA-100 @MTA-120 
  @usah
  Scenario: Callout Contractor for Triage job - job deferrals enabled - within deferral hours
    Given the job deferral time is in hours
      And a "Helpdesk Operator" has logged in
      And a job in Triage awaiting callout resource assignment
      And the Manage Resources panel is viewed
     When a Contractor is selected and saved
     Then the job resource status is now "Job Advise Deferred"
      And the timeline displays a "Resource Added" event with note "Awaiting Funding Authorisation"
      And the timeline displays an "Awaiting Funding Authorisation" event
    
  #@MTA-100 @MTA-120 @MTA-221 
  @usah
  Scenario: Callout Contractor for Triage job - job deferrals enabled - outwith deferral hours
    Given the job deferral time is out of hours
      And a "Helpdesk Operator" has logged in
      And a job in Triage awaiting callout resource assignment
      And the Manage Resources panel is viewed
     When a Contractor is selected and saved
     Then the job resource status is now "New Job Notification Sent"
      And the timeline displays a "Resource Added" event with note "Awaiting Funding Authorisation"
      And the timeline displays an "Awaiting Funding Authorisation" event
      And the timeline displays a "Funding Approved" event with note "Amount authorized automatically"
      And the timeline displays an "Email notification sent" event
      
  #@MTA-100 @MCP-1490 
  @usah
  Scenario: Callout City Tech resource for Triage job - job deferrals enabled - within deferral hours
    Given the job deferral time is in hours
      And a "Helpdesk Operator" has logged in
      And a job in Triage awaiting callout resource assignment
      And the Manage Resources panel is viewed
     When an available City Tech resource is selected
      And Override Recommended Resource details are entered
      And the manage resource details are saved
     Then the job resource status is now "Job Advise Deferred"
      And the timeline displays a "Resource Added" event with note "Job Advise Deferred"
      And the timeline displays a "Resource Added Deferred" event with "Defer Until"

  #@MTA-100 @MCP-1490 
  @usah
  Scenario: Callout City Tech resource for Triage job - job deferrals enabled - outwith deferral hours
    Given the job deferral time is out of hours
      And a "Helpdesk Operator" has logged in
      And a job in Triage awaiting callout resource assignment
      And the Manage Resources panel is viewed
     When an available City Tech resource is selected
      And Override Recommended Resource details are entered
      And the manage resource details are saved
     Then the job resource status is now "New Job Notification Sent"
      And the timeline displays a "Resource Added" event with note "New Job Notification Sent"
      And the timeline displays an "Notification and text message sent" event

  #@MTA-220
  @mcp 
  Scenario: Remove Suggested Resource - System displays warning 'This will mark the resource as no longer required' 
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a resource
     When user declines job on behalf of resource
      And removes suggested resource field
     Then a warning "This will mark the resource as no longer required" is displayed
      And the resource status is now "Declined"
      And the timeline displays a "Declined Job" event with "Reason, Notes"
      
  #@MTA-218
  @mcp @bugAdvocate @bugWalmart
  Scenario: Remove Suggested Resource - Park [bug: MCP-20552, MCP-20605]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a job that is not of type "Landlord" with "P1" fault priority and is assigned to a resource
     When user declines job on behalf of resource
      And removes suggested resource field
      And the details for why no additional resource is required is entered
      And user selects park job action with reason and date to unpark
     Then the client status is now "Parked"
      And the timeline displays a "Job Parked" event with "Parked until, Reason"
      And the timeline displays a "Additonal Resource Not Required" event
      
  #@MTA-219
  @mcp @bugAdvocate
  Scenario: Remove Suggested Resource - Cancel [bug: MCP-21353]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a resource
     When user declines job on behalf of resource
      And removes suggested resource field
      And the details for why no additional resource is required is entered
      And user selects cancel job action
     Then the client status is now "Cancelled"
      And the timeline displays a "Additonal Resource Not Required" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"
      And the timeline displays a "Job cancelled" event
      
  #@MTA-230
  @mcp
  Scenario: Schedule Call back  
    Given a "Helpdesk Operator" has logged in
      And a search is run for a job assigned to a resource with phone number
     When user selects Schedule Callback action for the resource
      And selects call back time, enters notes and saves it 
     Then telephone icon along with date and time of call back is displayed against the resource who requires a call back
      And the timeline displays a "Callback scheduled for Resource" event with "Due at, Notes"
  
  #@MTA-231
  @mcp
  Scenario: Call resource - Call Answered
    Given a "Helpdesk Operator" has logged in
      And a search is run for a job assigned to a resource with phone number
     When the "Call" button is clicked
      And the phone number to call is selected
      And the call is answered
     Then the timeline displays a "Outbound call successful" event with "Speaking With, Phone No"
  
  #@MTA-231
  @mcp
  Scenario: Call resource - Call Not Answered
    Given a "Helpdesk Operator" has logged in
      And a search is run for a job assigned to a resource with phone number
     When the "Call" button is clicked
      And the phone number to call is selected
      And the call is not answered
     Then the timeline displays a "Outbound call unsuccessful" event with "Phone No, Notes"
     
     
  #@MCF @MCP-7234 @MCP-8248 @MTA-280
  @mcp
  Scenario: Additional Resource Required - Individual Resource
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a resource
      And the "Manage Resources" action is selected
      And an additional "individual" resource is added
      And the manage resource details are saved
     Then the additional resource panel section displays the details of added resource
      And the timeline displays a "Resource Added" event
      
  @mcp @bugAdvocate @bugWalmart
  Scenario: Removing a Resource - Resource is caller [MCP-20669, MCP-20605]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a City Tech resource same as caller with phone number
      And the job is accepted for the resource and an ETA is not advised to site
      And the assigned resource is removed from manage resources section
     When the "Call" button is clicked
      And the phone number to call is selected
      And the call is answered
      And the "Advise Removal" action is selected
      And Advise removal is confirmed
      And the Advise removal form is saved
     Then the job resource status is now "Removed"
      And the client status is now "Logged"
      And the timeline displays a "Outbound call successful" event with "Speaking With, Phone No"
      And the timeline displays a "Resource Removed" event
      
  @mcp @bugAdvocate @bugWalmart
  Scenario: Removing a Resource - Resource is not caller and removal is agreed to [MCP-20669, MCP-20605]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a City Tech resource with phone number
      And the job is accepted for the resource and an ETA is not advised to site
      And the assigned resource is removed from manage resources section
     When the "Call" button is clicked
      And the phone number to call is selected
      And the call is answered
      And the "Advise Removal" action is selected
      And Advise removal is confirmed
      And the Advise removal form is saved
     Then the job resource status is now "Removed"
      And the client status is now "Logged"
      And the timeline displays a "Outbound call successful" event with "Speaking With, Phone No"
      And the timeline displays a "Resource Removed" event
      
  #@intermittentBug @MCP-3974
  @mcp
  Scenario: Removing a Resource - Resource is not caller and removal is declined
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a City Tech resource with phone number
      And the job is accepted for the resource and an ETA is not advised to site
      And the assigned resource is removed from manage resources section
     When the "Call" button is clicked
      And the phone number to call is selected
      And the call is answered
      And the "Advise Removal" action is selected
      And Advise removal is not confirmed
      And the Advise removal form is saved
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Outbound call successful" event with "Speaking With, Phone No"
      And the timeline displays a "Resource Removal Rejected" event
      
  #@MTA-313
  @mcp @smoke
  Scenario: Assigning a single City tech resource with no iPad - Job Accepted (ETA not advised to site)
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
     When the job is accepted for the resource and an ETA is not advised to site 
      And the page is refreshed
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Job Provided With ETA" event with "ETA"
      
  #@MTA-313
  @mcp
  Scenario: Assigning a single City tech resource with no iPad - Job Accepted (ETA advised to site) 
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
     When the job is accepted for the resource and an ETA is advised to site
     Then the job resource status is now "ETA Advised To Site"
      And the client status is now "Allocated"
      And the timeline displays a "ETA advised to site" event with "Advised To, ETA"
      And the timeline displays a "Job Provided With ETA" event with "ETA"
  
  #@MTA-314
  @mcp @bugAdvocate @bugWalmart
  Scenario: Assigning a single City tech resource with no iPad - Job Declined [MCP-20669, MCP-20605, MCP-21353]
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
     When user declines job on behalf of resource
     Then the job resource status is now "Declined"
      And the client status is now "Logged"
      And the timeline displays a "Declined Job" event with "Reason, Notes"
      
  #@MTA-321
  @mcp
  Scenario: Assigning a single City tech resource with no iPad - Call Answered - Job Accepted (ETA not advised to site)  
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
      And the "Call" button is clicked
      And the phone number to call is selected
     When the call is answered
      And the job is accepted for the resource and an ETA is not advised to site
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays a "Outbound call successful" event with "Speaking With, Phone No"
      And the timeline displays a "Job Provided With ETA" event with "ETA"
  
  #@MTA-321
  @mcp
  Scenario: Assigning a single City tech resource with no iPad - Call Answered - Job Accepted (ETA advised to site)  
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
      And the "Call" button is clicked
      And the phone number to call is selected
     When the call is answered
      And the job is accepted for the resource and an ETA is advised to site
     Then the job resource status is now "ETA Advised To Site"
      And the client status is now "Allocated"
      And the timeline displays a "Outbound call successful" event with "Speaking With, Phone No"
      And the timeline displays a "ETA advised to site" event with "Advised To, ETA"
      And the timeline displays a "Job Provided With ETA" event with "ETA"
  
  #@MTA-322
  @mcp @bugAdvocate @bugWalmart
  Scenario: Assigning a single City tech resource with no iPad - Call Answered - Job Declined [MCP-20669, MCP-20605, MCP-21353]
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
      And the "Call" button is clicked
      And the phone number to call is selected
     When the call is answered
      And user declines job on behalf of resource
     Then the job resource status is now "Declined"
      And the client status is now "Logged"
      And the timeline displays a "Outbound call successful" event with "Speaking With, Phone No"
      And the timeline displays a "Declined Job" event with "Reason, Notes"
  
  #@MTA-315
  @mcp
  Scenario: Assigning a single City tech resource with no iPad - Call Not Answered
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
      And the "Call" button is clicked
      And the phone number to call is selected
     When the call is not answered
     Then the job resource status is now "Call Required"
      And the client status is now "Logged"
      And the timeline displays a "Outbound call unsuccessful" event with "Phone No, Notes"

  #@MTA-468
  @mcp
  Scenario: Removing a contractor with authorised funding request - Onsite event
    Given a "Helpdesk Operator" has logged in 
      And they are accepting a job for a resource with no iPad
      And an additional "Contractor" resource accepts the job
     When the contractor resource status for the job is updated to "on site"
     Then the "Remove Resource" action should not be available
      And the client status is now "In Progress"
  
  @smoke
  @mcp
  Scenario: Verify that the PO document is attached to the job when ETA has been advised to the site
    Given a "Helpdesk Operator" has logged in 
      And they are accepting a job for a resource with no iPad
     When an additional contractor resource accepts the job
     Then the PO document is attached to the job 
      And the purchase order details are shown in Documents Tab
   
  #Commented out the 'And' step due to bug MCP-11778 as discussed with BT until bug is fixed  
  @mcp
  Scenario: Removing a contractor with authorised funding request - No Onsite event
    Given a "Helpdesk Operator" has logged in 
      And they are accepting a job for a resource with no iPad
      And an additional "Contractor" resource accepts the job
     When the additional resource is removed
     Then the job resource status is now "Removed"
      And the timeline displays a "notified by email of removal from job" event
   #   And the timeline displays a "Confirmed Funding Request Cancelled" event
      And the timeline displays a "Resource Removed" event

  @mcp
  Scenario: Assigning multiple City Tech resources with no iPad - Call not answered
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
      And an additional "City Tech having phone number without ipad" resource is added
      And the manage resource details are saved
     When the call is not answered by initial resource
      And the call is not answered by additional resource
     Then the job resource status is now "Call Required"
      And the client status is now "Logged"
      And the timeline displays "2" "Outbound call unsuccessful" events with "phone" icon and with "Phone No, Notes" details 
      
  @mcp @bugAdvocate 
  Scenario: Additional Resource Required - Resource Profile [bug: MCP-21790]
    Given a "Helpdesk Operator" has logged in
      And I have "Resource" profile
      And a search is run for a "Reactive" job assigned to a resource
      And the "Manage Resources" action is selected
     When an additional "Resource Profile" resource is added 
      And the manage resource details are saved
     Then the additional resource panel section displays the details of added resource
      And the timeline displays a "Resource Added" event    
      
  @mcp
  Scenario: Removing a Resource - Cancel
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a City Tech resource with phone number
      And the job is accepted for the resource and an ETA is not advised to site
     When the removal form is filled and the cancel button is clicked
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline does not display a "Resource Removal Request" event
      
  @mcp
  Scenario: Removing a Resource - Confirm 
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a City Tech resource with phone number
      And the job is accepted for the resource and an ETA is not advised to site
     When the assigned resource is removed from manage resources section
     Then the job resource status is now "Removal Requested"
      And the client status is now "Logged"
      And the timeline displays a "Resource Removal Request" event
      
  @mcp
  @bugRainbow @bugWalmart
  Scenario: Remove Resource - Status of Awaiting Parts or Returning - Transfer Work [bug: MCP-19816]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to single city resource with resource assignment status "Awaiting Parts" or "Returning"
      And the "Manage Resources" action is selected
     When the "Transfer Work" action is selected
      And the work is transferred to another "City tech" resource
     Then the job resource status is now "Work Transferred"
      And the job resource status is one of "New Job Notification Sent, Call Required" 
      And the client status is still "In Progress"
      And the timeline displays a "Work transferred" event with "Requested By, Old resource, New resource, Reason, Notes"
   
  @mcp @bugAdvocate @bugWalmart
  Scenario: Assigning multiple resources with no iPad - call answered - job accepted [MCP-20669, MCP-20605]
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
      And an additional "City Tech having phone number without ipad" resource is added
      And the manage resource details are saved
     When the call is answered by initial resource
      And the call is answered by additional resource
      And the job is accepted for the initial resource and an ETA is not advised to site
      And the job is accepted for the additional resource and an ETA is not advised to site
     Then the job resource status is now "ETA Provided"
      And the client status is now "Allocated"
      And the timeline displays "2" "Outbound call successful" events with "phone" icon and with "Speaking With, Phone No" details
      And the timeline displays "2" "Job Provided With ETA" events with "wrench" icon and with "ETA" details
      
  @bugRainbow @bugWalmart
  @mcp @bugAdvocate
  Scenario: Assigning multiple resources with no iPad - call answered - job declined [bug: MCP-7288, MCP-20669, MCP-20605, MCP-21353]
    Given a "Helpdesk Operator" has logged in
      And an "occupied" City Tech store with an existing caller
      And a new job is logged and assigned to a City resource with "mobile" phone, "with" email and "no" ipad
      And the job is viewed
      And an additional "City Tech having phone number without ipad" resource is added
      And the manage resource details are saved
     When the call is answered by initial resource
      And the call is answered by additional resource
      And user declines job on behalf of initial resource
      And user declines job on behalf of additional resource
     Then the job resource status is now "Declined"
      And the client status is now "Logged"
      And the timeline displays "2" "Outbound call successful" events with "phone" icon and with "Speaking With, Phone No" details
      And the timeline displays "2" "Declined Job" events with "wrench" icon and with "Reason, notes" details
