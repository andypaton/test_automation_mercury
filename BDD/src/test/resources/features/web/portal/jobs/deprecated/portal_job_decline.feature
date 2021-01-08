@portal @portal_jobs
@wip @deprecated
@mcp
Feature: Portal - Jobs - Decline
  
  Scenario Outline: Decline a job with "<profile>" profile and verify the job count has been decremented    
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Logged" jobs only assigned to "Single" resource with status "Call Required" or "New Job Notification Sent" 
     When the user logs in    
      And the user is viewing a job awaiting acceptance
      And the user declines the job
      And the user selects a random reason
      And the user enters decline notes
      And the job is assigned to a "<resource>" resource
      And the user saves the decline form
     Then the awaiting job count will have decremented on the landing page
    Examples:
      | profile          | jobtype  | resource          | status |
      | RHVAC Technician | reactive | RHVAC Technician  | Logged |
  
  #@MCP-12120
  @bug
  Scenario Outline: Decline a job with "<profile>" profile and assign to "<resource>"
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Logged" jobs only assigned to "Single" resource with status "Call Required" or "New Job Notification Sent" 
     When the user logs in
      And the user is viewing a job awaiting acceptance
      And the user declines the job
      And the user selects a random reason
      And the user enters decline notes
      And the job is assigned to a "<resource>" resource
      And the user saves the decline form
     Then the Resource Assignment table has been updated with the status "Declined"
      And the Timeline Event Summary has been updated with "Declined"
      And the Job is sitting in "<status>" status
    Examples:
      | profile       | jobtype  | resource       | status |
      | City Resource | reactive | City Resource  | Logged |
      | City Resource | reactive | Contractor     | Logged |
      
  #@2nddeclineajob 
  @notsignedoff @wip
  Scenario Outline: 2nd decline a job with "<profile>" profile
    Given a portal user with profile "<profile>" 
      And has "Awaiting Acceptance" jobs previously declined
     When the user logs in
      And the user is viewing a job awaiting acceptance previously declined
      And the user declines the job with reason "<reason>"
      And the job is assigned to a "<resource>" resource
      And the user saves the decline form
     Then the Resource Assignment table has been updated with the status "Declined"
      And the Timeline Event Summary has been updated with "Declined"
#    And the Job is sitting in "<status>" status 
#    And the awaiting job count will have decremented on the landing page
    Examples:
      | profile          | jobtype  | reason                     | resource       | status              |
      | RHVAC Technician | reactive | Incorrect resource         | City Resource  | Awaiting Assignment |
      | RHVAC Supervisor | reactive | Incorrect resource         | Contractor     | Awaiting Assignment |       