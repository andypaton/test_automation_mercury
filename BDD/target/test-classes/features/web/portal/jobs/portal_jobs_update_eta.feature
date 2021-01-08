@portal @portal_jobs @portal_jobs_eta
@mcp
Feature: Portal - Jobs - Update ETA
    
  Scenario: Update ETA for a job with a "Contractor Admin" profile and ETA not advised to site
   Given a portal user with profile "Contractor Admin"
     And has "reactive" "Allocated" jobs only assigned to "Single" resource with status "Awaiting ETA" or "Call Required"
    When the user logs in    
     And the user views an "reactive" "Allocated" job     
     And the ETA is updated
     And the ETA is saved
    Then the Job is sitting in "Allocated" status
     And the job is now sitting in the "ETA not advised to site" monitor
     And the Resource Assignment table has been updated with the status "ETA Provided"
     And the Timeline Event Summary has been updated with "ETA Updated"
     