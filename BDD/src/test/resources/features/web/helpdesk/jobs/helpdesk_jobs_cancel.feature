@helpdesk @helpdesk_jobs @helpdesk_jobs_cancel
Feature: Helpdesk - Jobs - Cancel

  #Removed env tag as this test is no longer valid - resource must be removed first
  @deprecated
  Scenario: don't cancel job
    Given a "Helpdesk Operator" has logged in
      And a "Logged" job is created and searched for
     When the "Cancel Job" action is selected
      And all cancelation details are entered
      And the "Don't Cancel" button is clicked
     Then the job status is still "Logged"
      And the Resource Status and Client Status are not updated
      And the timeline does not display a "Job cancellation requested" event

  # Removing mcp tag as BT confirmed this is not valid when there is no resource assigned and AutoAssign toggle is ON for all environments
  @toggles @AutoAssign
  Scenario: Cancel job - no resource allocated, Work No Longer Required
        * using dataset "helpdesk_jobs_cancel_001"
    Given an "IT" user has logged in
      And a "reactive" job with "no" resource and status "Logged"
      And a search is run for the job reference
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Work No Longer Required"
      And Notes are entered
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the Resource Status is not updated
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason, Notes"
      And the available job actions includes "Manage Incidents, Unlock Job, Quotes, Reopen Job, Chase, Confirm Warranty"
  
  # Removing mcp tag as BT confirmed this is not valid when there is no resource assigned and AutoAssign toggle is ON for all environments
  @toggles @AutoAssign
  Scenario: Cancel job - no resource allocated, Duplicate Job
        * using dataset "helpdesk_jobs_cancel_001"
    Given a "Helpdesk Operator" has logged in
      And a duplicate "reactive" job with "no" resource and status "Logged"
      And a search is run for the job reference
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Duplicate Job"
      And the original job number is entered
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the Resource Status is not updated
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"
      And a original job number is saved to the database
      
  # Removing mcp tag as BT confirmed this is not valid when there is no resource assigned and AutoAssign toggle is ON for all environments
  @toggles @AutoAssign
  Scenario: Cancel job - no resource allocated, Other reason
        * using dataset "helpdesk_jobs_cancel_001"
    Given a "Helpdesk Operator" has logged in
      And a "reactive" job with "no" resource and status "Logged"
      And a search is run for the job reference
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Other"
      And Other Reason is entered
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the Resource Status is not updated
      And the Client status is now "Cancelled"
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"
  
  @uswm @ukrb @usah
  Scenario: Park Job then Cancel - Contractor resource allocated, Work No Longer Required
    Given a "Helpdesk Operator" has logged in
      And a "reactive" job with status "Logged"
      And the Resource Assignment Status is "New Job Notification Sent"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And any additional resources are removed
      And the additional resource required section is closed and "Parked"
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Work No Longer Required"
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the job displays Resource status "Removed" and Client status "Cancelled"
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"
      
  @usad
  Scenario: Park Job then Cancel - Contractor resource allocated, Work No Longer Required
    Given a "Helpdesk Manager" has logged in
      And a "reactive" job with status "Logged"
      And the Resource Assignment Status is "New Job Notification Sent"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And any additional resources are removed
      And the additional resource required section is closed and "Parked"
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Work No Longer Required"
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the job displays Resource status "Removed" and Client status "Cancelled"
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"
  
  @uswm @ukrb @usah
  Scenario: Park Job then Cancel - Contractor resource allocated, Duplicate Job
    Given a "Helpdesk Operator" has logged in
      And a "reactive" job with status "Logged"
      And the Resource Assignment Status is "New Job Notification Sent"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And any additional resources are removed
      And the additional resource required section is closed and "Parked"
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Duplicate Job"
      And the original job number is entered
      And Notes are entered
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the job displays Resource status "Removed" and Client status "Cancelled"
      And the timeline displays a "Job canceled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason, Notes"
      
  @usad
  Scenario: Park Job then Cancel - Contractor resource allocated, Duplicate Job
    Given a "Helpdesk Manager" has logged in
      And a "reactive" job with status "Logged"
      And the Resource Assignment Status is "New Job Notification Sent"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And any additional resources are removed
      And the additional resource required section is closed and "Parked"
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Duplicate Job"
      And the original job number is entered
      And Notes are entered
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the job displays Resource status "Removed" and Client status "Cancelled"
      And the timeline displays a "Job canceled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason, Notes"
  
  @uswm @ukrb @usah
  Scenario: Park Job then Cancel - Contractor resource allocated, Other reason
    Given a "Helpdesk Operator" has logged in
      And a "reactive" job with status "Logged"
      And the Resource Assignment Status is "New Job Notification Sent"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And any additional resources are removed
      And the additional resource required section is closed and "Parked"
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Other"
      And Other Reason is entered
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the job displays Resource status "Removed" and Client status "Cancelled"
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"  
      
  @usad
  Scenario: Park Job then Cancel - Contractor resource allocated, Other reason
    Given a "Helpdesk Manager" has logged in
      And a "reactive" job with status "Logged"
      And the Resource Assignment Status is "New Job Notification Sent"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
      And the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And any additional resources are removed
      And the additional resource required section is closed and "Parked"
     When the "Cancel Job" action is selected
      And the name of person canceling the job is entered
      And Reason is set to "Other"
      And Other Reason is entered
      And the "Cancel Job" button is clicked
     Then the job status is now "Cancelled"
      And the job displays Resource status "Removed" and Client status "Cancelled"
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason" 
      
  @mcp @bugWalmart @smoke
  Scenario: Cancel job from Resource Section - Contractor resource allocated [bug : MCP-20598, MCP-20836]
    Given a "Helpdesk Operator" has logged in
      And a "reactive" job with status "Logged"
      And the Resource Assignment Status is "New Job Notification Sent"
      And a "Contractor" resource is assigned
      And a search is run for the job reference
     When the assigned resource is removed from manage resources section
      And the advise removal is confirmed
      And any additional resources are removed
      And the additional resource required section is closed and "Cancelled"
     Then the job status is now "Cancelled"
      And the job displays Resource status "Removed" and Client status "Cancelled"
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"
      
  @mcp
  Scenario: Cancel Job - Advise Removal not confirmed
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a City Tech resource
      And the job is accepted for the resource and an ETA is not advised to site
      And the "Remove Resource" action is selected
      And the resource is removed
     When the "Advise Removal" action is selected
      And Advise removal is not confirmed
      And the Advise removal form is saved
     Then the job resource status is now "ETA Provided"
      And the timeline displays a "Resource Removal Request" event
      And the client status is now "Allocated"
      And the timeline displays a "Resource Removal Rejected" event 

  @usah @uswm @usad @bugAdvocate
  Scenario: Cancel Job - Advise Removal - confirmed [bug: MCP-21175]
    Given a "Helpdesk Operator" has logged in
      And a search is run for a "reactive" job assigned to a City Tech resource
      And the job is accepted for the resource and an ETA is not advised to site
      And the "Remove Resource" action is selected
      And the resource is removed
     When the "Advise Removal" action is selected
      And Advise removal is confirmed
      And the Advise removal form is saved
     Then the job resource status is now "Removed"
      And the client status is now "Logged"
      And the timeline displays a "Resource Removal Request" event
      And the timeline displays a "Resource Removed" event
      And the Additional Resource Required section is shown where user will be able to select an Alternative Resource to pass the job to
    
   #Step commented out until bug MCP-19481 is fixed - agreed with the Business Team
   @mcp @bugWalmart
   Scenario: Viewing a cancelled job [bug: MCP-19481]
    Given an "IT" user has logged in
      And a "Logged" job is created and searched for
     When job is cancelled
#     Then a job card is displayed
     Then the available job actions includes "Manage Incidents, Quotes, Reopen Job, Chase, Confirm Warranty"
      And the timeline displays a "Job cancelled" event
      And the timeline displays a "Job cancellation requested" event with "Requested By, Reason"
