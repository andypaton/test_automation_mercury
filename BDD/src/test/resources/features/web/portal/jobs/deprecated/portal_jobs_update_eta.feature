@portal @portal_jobs
@wip @deprecated
@mcp
Feature: Portal - Jobs - Update ETA
    
  #@MTA-242 @MTA-582 @MTA-198 MCP-12149
  @bug
  Scenario Outline: Update ETA for a job with a "<profile>" profile and ETA advised to site
   Given a portal user with profile "<profile>"
     And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
    When the user logs in    
     And the user views an "<jobtype>" "Allocated" job     
     And the ETA is updated
     And the ETA is advised to site
     And the ETA is saved
    Then the Job is sitting in "<status>" status
     And  the Job is removed from the "ETA not Advised to site" monitor
     And the Resource Assignment table has been updated with the status "ETA Advised To Site"
    Examples:
      | profile          | jobtype  | status    |     
      | City Resource    | reactive | Allocated |
      
  #@MTA-582 @MTA-198 MCP-12149
  @bug
  Scenario Outline: Update ETA for a job with a "<profile>" profile and ETA not advised to site
   Given a portal user with profile "<profile>"
     And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided"
    When the user logs in    
     And the user views an "<jobtype>" "Allocated" job     
     And the ETA is updated
     And the ETA is not advised to site
     And the ETA is saved
    Then the Job is sitting in "<status>" status
     And the job is now sitting in the "ETA not advised to site" monitor
     And the Resource Assignment table has been updated with the status "ETA Provided"
    Examples:
      | profile          | jobtype  | status    |     
      | City Resource    | reactive | Allocated |       