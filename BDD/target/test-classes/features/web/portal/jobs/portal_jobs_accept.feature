@portal @portal_jobs @portal_jobs_accept
@mcp
Feature: Portal - Jobs - Accept
 
  @bugRainbow @bugWalmart @bugAdvocate
  Scenario: Contractor Admin views Jobs Awaiting Acceptance [bug: MCP-13767]
    Given a portal user with profile "Contractor Admin"
      And has "reactive" "Logged" jobs only assigned to "Single" resource with status "Call Required" or "New Job Notification Sent" 
      And the user logs in
     When the "Jobs Awaiting Acceptance" sub menu is selected from the "Jobs" top menu
     Then the "Jobs Awaiting Acceptance" table is displayed
      And the "Jobs Awaiting Acceptance" table can be sorted on all columns
  
  Scenario: verify a job with "Contractor Admin" profile
    Given a portal user with profile "Contractor Admin"
      And has "reactive" "Logged" jobs only assigned to "Single" resource with status "Call Required" or "New Job Notification Sent"
     When the user logs in    
      And the user is viewing a job awaiting acceptance
     Then the job info is displayed correctly
      And the site info is displayed correctly
      And the contact info is displayed correctly 
     
  Scenario: Contractor Admin accepts job
    Given a Contractor with a job awaiting acceptance
     When the user logs in 
      And the user is viewing the job awaiting acceptance
      And the user completes the accept form
      And a Contractor Reference Number "new" value is entered    
      And the user accepts the job
     Then the Job is sitting in "Allocated" status
      And the resource status is "ETA Provided"
      And the job is removed from the jobs awaiting acceptance table
#      And the open job count will have incremented                          ### this cant be guaranteed when jobs are running in parallel
#      And the awaiting job count will have decremented on the landing page  ### this cant be guaranteed when jobs are running in parallel
      And the job event summary has been updated with "Job Provided With ETA"

     