@maintenance
@cleanupJobs
Feature: Terminate jobs   
    
  Scenario Outline: Cancel jobs "<MONITOR>" via the API
    Given a "Helpdesk Operator" has logged in
      And all except the latest "100" jobs on the "<MONITOR>" monitor are selected for cancelation
     When the Service Channel throttle is set to a "30" second wait after every "250" jobs 
      And the resources are removed from the selected jobs via the api
      And the selected jobs are cancelled via the api
     Then the selected jobs will have been cancelled
    Examples:
    | MONITOR             |
    | Awaiting Assignment |
    | Awaiting Acceptance |
    
        
  Scenario: Remove resources from jobs requested for cancelation via the API
    Given a "Helpdesk Operator" has logged in
      And historic cancellation requested jobs older than "2" days
     When the resources are removed via the api
     Then the selected jobs will have been cancelled    
    
    
  @completejobs
  Scenario Outline: Complete "<monitor>" jobs via the API
    Given all except the latest "100" jobs on the "<monitor>" monitor are selected for completion
     When the selected jobs are completed via the api
     Then the selected jobs will have been fixed
    Examples:
      | monitor             |
      | ETA greater         |
      | ETA Expired         |
      | SLA Near/Missed     |


  @removeresources
  Scenario: Confirm resource removal requests via the API
    Given all except the latest "10" jobs on the "Removal Requests" monitor are selected for resource removal
     When the resources are removed from the jobs via the api
     Then the selected jobs have had resources removed  


  @fundingrequestreject
  Scenario: Reject Funding Requests on via the API 
    Given all except the latest "50" jobs on the "Funding Requests" monitor have the funding request rejected
     When the funding requests are rejected for the jobs via the api
     Then the selected jobs will have been rejected
     
  @cancelJob
  Scenario: Cancel job with given jobStatus <> resourceAssignmentStatus mapping
 #   Given system property "cancelJob" is "Allocated > ETA Provided"
 #     And system property "maxNumberOfOpenJobs" is "9550"
 #     And system property "keepResourceIds" is "9550"
    Given a system property for "cancelJob"
      And a system property for "maxNumberOfOpenJobs"
      And a system property for "keepResourceIds"
      And the jobs to be cancelled are identified
     When the resources are removed from the jobs via the api
      And the selected jobs are "cancelled" via the admin api
     Then the selected jobs will have been cancelled    
     

  @cancelJobs
  Scenario Outline: Cancel "<JOB STATUS>" jobs with "<RESOURCE STATUS>" resource
    Given all except the latest "49" "<JOB STATUS>" jobs with "<RESOURCE STATUS>" resource are selected
     When the resources are removed from the jobs via the api
      And the selected jobs are "cancelled" via the admin api
     Then the selected jobs will have been cancelled
    Examples: 
      | JOB STATUS                                   | RESOURCE STATUS                | 
      | Allocated                                    | Awaiting Funding Authorisation | 
      | Allocated                                    | Awaiting Parts                 | 
      | Allocated                                    | Declined                       | 
      | Allocated                                    | ETA Advised To Site            | 
      | Allocated                                    | ETA Provided                   | 
      | Allocated                                    | Funding Request Rejected       | 
      | Allocated                                    | New Job Notification Sent      | 
      | Allocated                                    | Removed                        | 
      | Awaiting Approval                            | Funding Request Rejected       | 
      | Awaiting Approval                            | Removed                        | 
      | Awaiting Approval                            |                                | 
      | Awaiting Approval - Funding Request Rejected |                                | 
      | Awaiting Final Approval                      | Declined                       | 
      | Awaiting Final Approval                      | Funding Request Rejected       | 
      | Awaiting Final Approval                      | Removed                        | 
      | Awaiting Final Approval                      |                                | 
      | Awaiting Quote Request Review                | Declined                       | 
      | Awaiting Quote Request Review                | Funding Request Rejected       | 
      | Awaiting Quote Request Review                | Removed                        | 
      | Awaiting Quote Request Review                |                                | 
      | Awaiting Resource Assignment                 | Declined                       | 
      | Awaiting Resource Assignment                 | Funding Request Rejected       | 
      | Awaiting Resource Assignment                 | New Job Notification Sent      | 
      | Awaiting Resource Assignment                 | Removed                        | 
      | Awaiting Resource Assignment                 |                                | 
      | Awaiting Resource Quote                      | Declined                       | 
      | Awaiting Resource Quote ETA                  | Advised To Site                | 
      | Awaiting Resource Quote ETA                  | Provided                       | 
      | Awaiting Resource Quote                      | Funding Request Rejected       | 
      | Awaiting Resource Quote                      | New Job Notification Sent      | 
      | Awaiting Resource Quote                      | Removed                        | 
      | Awaiting Resource Quote                      |                                | 
      | Cancellation Requested                       | Complete                       | 
      | Cancellation Requested                       | Declined                       | 
      | Cancellation Requested                       | Removed                        | 
      | In Progress                                  | Awaiting Funding Authorisation | 
      | In Progress                                  | Awaiting Parts                 | 
      | In Progress                                  | Awaiting Parts Review          | 
      | In Progress                                  | Call Required                  | 
      | In Progress                                  | Complete                       | 
      | In Progress                                  | Declined                       | 
      | In Progress                                  | ETA Provided                   | 
      | In Progress                                  | Funding Request Rejected       | 
      | In Progress                                  | New Job Notification Sent      | 
      | In Progress                                  | On Site                        | 
      | In Progress                                  | Removed                        | 
      | In Progress                                  | Returning                      | 
      | In Query                                     | Returning                      | 
      | In Query                                     |                                | 
      | ITQ Awaiting Acceptance                      | Awaiting Parts                 | 
      | ITQ Awaiting Acceptance                      | Declined                       | 
      | ITQ Awaiting Acceptance                      | Funding Request Rejected       | 
      | ITQ Awaiting Acceptance                      | New Job Notification Sent      | 
      | ITQ Awaiting Acceptance                      |                                | 
      | Logged                                       | Awaiting Funding Authorisation | 
      | Logged                                       | Call Required                  | 
      | Logged                                       | Declined                       | 
      | Logged                                       | ETA Provided                   | 
      | Logged                                       | Funding Request Rejected       | 
      | Logged                                       | New Job Notification Sent      | 
      | Logged                                       | Removal Requested              | 
      | Logged                                       | Removed                        | 
      | Logged                                       |                                | 
      | Parked                                       | Declined                       | 
      | Parked                                       | Removed                        | 
      | Query with Initial Approver                  |                                | 
      | Quote Approved                               | Awaiting Funding Authorisation | 
      | Quote Approved                               | Awaiting Parts                 | 
      | Quote Approved                               | Awaiting Parts Review          | 
      | Quote Approved                               | Call Required                  | 
      | Quote Approved                               | Complete                       | 
      | Quote Approved                               | Declined                       | 
      | Quote Approved                               | ETA Advised To Site            | 
      | Quote Approved                               | ETA Provided                   | 
      | Quote Approved                               | Funding Request Rejected       | 
      | Quote Approved                               | New Job Notification Sent      | 
      | Quote Approved                               | On Site                        | 
      | Quote Approved                               | Removal Requested              | 
      | Quote Approved                               | Removed                        | 
      | Quote Approved                               | Returning                      | 
      | Quote Approved                               |                                | 
      | Reopened                                     | Awaiting Parts                 | 
      | Reopened                                     | Complete                       | 
      | Reopened                                     | Declined                       | 
      | Reopened                                     | Funding Request Rejected       | 
      | Reopened                                     | Removed                        | 
      | Tech Bureau Triage                           |                                | 
      

  @cancelQuoteJobs
  Scenario Outline: Cancel quote jobs with "<APPROVAL STATUS>" approval status
    Given all except the latest "50" quote jobs with "<APPROVAL STATUS>" approval status are selected
     When the selected quote jobs are cancelled by DB update
     Then the selected quote jobs will have been cancelled
    Examples:
      | APPROVAL STATUS            |
      | NULL                       |
      | Approved                   |
      | AwaitingApproval           |
      | AwaitingResourceAssignment |
      | ItqAwaitingAcceptance      |
      | None                       |
      | QueryResourceAnswered      |
      | QueryResourcePending       |
      
   
  @cancelChaseJobs
  Scenario Outline: Cancel chase jobs with "<CHASE TYPE>" approval status
    Given all except the latest "10" chase jobs with "<CHASE TYPE>" chase type are selected
     When the selected chase jobs are cancelled via the API
     Then the selected chase jobs will have been cancelled
    Examples:
      | CHASE TYPE                   |
      | Complaint                    |
      | ETA Chase                    |
      | Manager Chase                |
      | Parts/Equipment left on Site |
      | Works Complete Query         |
      | Worksheet Query              |

