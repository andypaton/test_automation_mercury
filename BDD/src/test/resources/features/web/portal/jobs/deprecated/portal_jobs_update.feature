@portal @portal_jobs
@wip @deprecated
@mcp
Feature: Portal - Jobs - Update

  Scenario Outline: Incomplete Update a Remote Job with Status on Departure Complete - Validate error messages
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the user updates the job
     Then the Travel time tooltip will be displayed
      And the Work Start tooltip will be displayed
      And the Time Spent tooltip will be displayed
      And the Status on Departure tooltip will be displayed
    Examples: 
      | profile       | jobtype  | 
      | City Resource | reactive | 
  
  Scenario Outline: Update a Remote Job with Status on Departure Complete and Verify Quote Form is visible
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Awaiting Parts" or "ETA Provided"
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the "Complete" Status on Departure is entered
      And a Quote is Requested for the Job    
     Then the Quote Request form is displayed
      And the Quote Request form is populated correctly
    Examples: 
      | profile       | jobtype  | 
      | City Resource | reactive | 
  
  Scenario Outline: Update a Remote Job with Status on Departure Complete
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the job is not the landlords responsibility
      And the job is a Remote Job
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And the "Complete" Status on Departure is entered
      And the Asset Condition is entered
      And the Root Cause Category is entered
      And the Root Cause is entered
      And the Root Cause Description is entered
      And the Notes are entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource has been marked as "Complete" for the job
      And the Message table has been updated with "Complete"
  #      And the Message table has been updated with the Job Status "<status>"
      And the Site Visit has been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | 
      | City Resource | reactive | No      | Yes       | Fixed  | 
  
  Scenario Outline: Update a Non Remote Job with Status on Departure Complete
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" 
     When the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the job is not the landlords responsibility
      And the Travel Time is entered
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And the "Complete" Status on Departure is entered
      And the Asset Condition is entered
      And the Root Cause Category is entered
      And the Root Cause is entered
      And the Root Cause Description is entered
      And the Notes are entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
  #      And the Message table has been updated with the Resource Status "Complete"
      And the Resource Assignment table has been updated with the status "Complete"
  #      And the Message table has been updated with the Job Status "<status>"
      And the Site Visit has been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | 
      | City Resource | reactive | No      | Yes       | Fixed  | 
  
  @bug
  Scenario Outline: Update a Remote Job with Status on Departure Complete and Additional Resource Required
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the job is not the landlords responsibility
      And the job is a Remote Job
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And the "Complete" Status on Departure is entered
      And the Asset Condition is entered
      And the Root Cause Category is entered
      And the Root Cause is entered
      And the Root Cause Description is entered
      And the Notes are entered
      And an Additional Resource is Required
      And a "<resource>" resource is selected
      And the Resources Notes are entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
  #    And the Message table has been updated with the Resource Status "Complete"
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | resource   | status      | 
      | City Resource | reactive | No      | Yes       | Contractor | In Progress | 
  
  @bug
  Scenario Outline: Update a Non Remote Job with Status on Departure Complete and Additional Resource Required - Operational On Departure is "<departure>"
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" 
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the job is not the landlords responsibility
      And the Travel Time is entered
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And the "Complete" Status on Departure is entered
      And the Asset Condition is entered
      And the Root Cause Category is entered
      And the Root Cause is entered
      And the Root Cause Description is entered
      And the Notes are entered
      And an Additional Resource is Required
      And a "<resource>" resource is selected
      And the Resources Notes are entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
  #      And the Message table has been updated with the Resource Status "Complete"
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | resource   | status      | 
      | City Resource | reactive | No      | Yes       | Contractor | In Progress | 
      | City Resource | reactive | No      | No        | Contractor | In Progress | 
  
  Scenario Outline: Update a Non Remote Job with Status on Departure Complete and Request a Quote
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the job is not the landlords responsibility
      And the Travel Time is entered
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And the "Complete" Status on Departure is entered
      And the Asset Condition is entered
      And the Root Cause Category is entered
      And the Root Cause is entered
      And the Notes are entered
      And the Root Cause Description is entered
      And a Quote is Requested for the Job
      And the Scope of Work is entered
      And a non-urgent quote with a "<fundingRoute>" funding route is requested
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
  #    And the Message table has been updated with the Resource Status "Complete"
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the new quote job is created
    Examples: 
      | profile       | jobtype  | arrival | departure | fundingRoute | resource   | status | 
      | City Resource | reactive | No      | Yes       | CAPEX        | Contractor | Fixed  | 
      | City Resource | reactive | No      | Yes       | OPEX         | Contractor | Fixed  | 
  
  Scenario Outline: Update a Non Remote Job with Status on Departure Complete
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the job is not the landlords responsibility
      And the Travel Time is entered
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And the "Complete" Status on Departure is entered
      And the Asset Condition is entered
      And the Root Cause Category is entered
      And the Root Cause is entered
      And the Notes are entered
      And the Root Cause Description is entered
      And a Quote is Requested for the Job
      And the Scope of Work is entered
      And a non-urgent quote with a "<fundingroute>" funding route is requested  
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed 
      And the View Job Details link be displayed
      And the View Open Jobs link will be displayed
      And the View All Jobs link will be displayed
    Examples: 
      | profile       | jobtype  | arrival | departure | fundingroute | 
      | City Resource | reactive | No      | Yes       | CAPEX        | 
  
  Scenario Outline: Update a Remote Job with Status on Departure Returning
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
     When the user logs in    
      And the user views an "<jobtype>" "Open" job
      And the job is not the landlords responsibility
      And the job is a Remote Job
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And the "Returning" Status on Departure is entered
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
  #    And the Message table has been updated with "ETACreated"
  #    And the Message table has been updated with "Returning"
      And the Site Visit has been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | 
      | City Resource | reactive | No      | Yes       | In Progress | 
  