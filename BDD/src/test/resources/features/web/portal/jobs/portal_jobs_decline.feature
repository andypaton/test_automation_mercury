@portal @portal_jobs @portal_jobs_decline
@mcp
Feature: Portal - Jobs - Decline
  
  @bugAdvocate
  Scenario: Decline a job with "Contractor Admin" profile and verify the job count has been decremented [MCP-21357]    
    Given a portal user with profile "Contractor Admin"
      And has "reactive" "Logged" jobs only assigned to "Single" resource with status "Call Required" or "New Job Notification Sent" 
     When the user logs in    
      And the user is viewing a job awaiting acceptance
      And the user declines the job
      And the user selects a random reason
      And the user enters decline notes
      And the job is assigned to a "" resource
      And the user saves the decline form
     Then the job is removed from the jobs awaiting acceptance table
#     And the awaiting job count will have decremented on the landing page      ### this cant be guaranteed when jobs are running in parallel

  @bugAdvocate @bugWalmart
  Scenario: Decline a job with "Contractor Admin" profile [bug: MCP-20552, MCP-20605, MCP-21353, MCP-21357]
    Given a portal user with profile "Contractor Admin"
      And has "reactive" "Logged" jobs only assigned to "Single" resource with status "Call Required" or "New Job Notification Sent" 
     When the user logs in
      And the user is viewing a job awaiting acceptance
      And the user declines the job
      And the user selects a random reason
      And the user enters decline notes
      And the user saves the decline form
     Then the Resource Assignment table has been updated with the status "Declined"
      And the Timeline Event Summary has been updated with "Declined"
      And the Job is sitting in "Logged" status