#@MTA-140 
@portal @portal_jobs
@wip @deprecated
@mcp 
Feature: Portal - Jobs - Accept
 
  # Note: City Techs only use MyJobs and hence bug MCP-12149 is set to LOW priority and probably will never get picked up!
  #@MCF @MCP-7234
  @bug
  @sanity
  Scenario Outline: accept a job with a City Resource - with transfer [bug: MCP-12149]
   Given a portal user with profile "<profile>"
     And has "<jobtype>" "Logged" jobs only assigned to "Single" resource with status "New Job Notification Sent" with transfer
    When the user logs in    
     And the user is viewing a job awaiting acceptance
     And the user completes the accept form
     And the site eta advised status is "<adivsed>"
     And the user accepts the job
    Then the Job is sitting in "<status>" status
     And the Timeline Event Summary has been updated with "Job Provided With ETA For"
     And the Timeline Event Summary has been updated with "ETA advised to site"
     And the open job count will have incremented
     And the awaiting job count will have decremented on the landing page
    Examples:
      | profile        | jobtype  | adivsed | status      |     
      | City Resource  | reactive | advised | In Progress |
      
  #@MCF @MCP-7234
  @bug
  Scenario Outline: accept a job with a City Resource - without transfer [bug:  MCP-12149]
   Given a portal user with profile "<profile>"
     And has "<jobtype>" "Logged" jobs only assigned to "Single" resource with status "New Job Notification Sent" without transfer
    When the user logs in    
     And the user is viewing a job awaiting acceptance
     And the user completes the accept form
     And the site eta advised status is "<adivsed>"
     And the user accepts the job
    Then the Job is sitting in "<status>" status
     And the Timeline Event Summary has been updated with "ETA advised to site"
     And the open job count will have incremented
     And the awaiting job count will have decremented on the landing page
    Examples:
      | profile        | jobtype  | adivsed | status    |     
      | City Resource  | reactive | advised | Allocated |      