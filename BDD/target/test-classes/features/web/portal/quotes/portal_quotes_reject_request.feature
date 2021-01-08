@portal @portal_quotes @portal_quotes_reject
@mcp
Feature: Portal - Quotes - Reject an Awaiting Quote Request Job
  
  @wip 
  Scenario: Verify the Reject form displays the correct job information
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the reject form for a job "Awaiting Quote Request Approval"
     Then the Job Details on the Jobs Awaiting Quote Request Approval form displays correctly
      And the Approve Quote Request Form displays correctly

  #@MTA-754
  Scenario: Verify all resources are shown when Use alternative contractor is selected
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the reject form for a job "Awaiting Quote Request Approval"
      And the Rejection Reason "Fund as reactive" is selected
      And "Contractor" is selected from the resource picker
      And Use alternative contractor is selected 
     Then all "Contractor" will be available
       
  #@MTA-269 @MTA-423
  Scenario: Verify all resources are shown
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the reject form for a job "Awaiting Quote Request Approval"
      And the Rejection Reason "Fund as reactive" is selected
      And "Technician" is selected from the resource picker
     Then all "Technician" will be available     
       
  #@MCF @MCP-7234 @MCP-8199
  Scenario: Reject Awaiting Quote Request and assign to a RFM Technician
     Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the reject form for a job "Awaiting Quote Request Approval"
      And the Rejection Reason "Fund as reactive" is selected
      And a resource is selected
      And the additional comments are entered
      And the Quote Request is rejected
     Then the Job is updated with a "Logged" status
     And the JobTimelineEvent table has been updated with "Job Type changed"
  
  Scenario: Reject Awaiting Quote Request and assign to a Contractor
     Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the reject form for a job "Awaiting Quote Request Approval"
      And the Rejection Reason "Fund as reactive" is selected
      And "Contractor" is selected from the resource picker
      And Use alternative contractor is selected
      And a resource is selected
      And the additional comments are entered
      And the Quote Request is rejected
     Then the Job is updated with a "Logged" status
      And the JobTimelineEvent table has been updated with "Job Type changed"
      And the JobTimelineEvent table has been updated with "Quote Approval Declined Notification"
  
  Scenario: Reject Awaiting Quote Request and Cancel
     Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
     When the user logs in
      And the "Open Quote Requests" sub menu is selected from the "Quotes" top menu
      And the user is viewing the reject form for a job "Awaiting Quote Request Approval"
      And the Rejection Reason "Work not needed" is selected     
      And the additional comments are entered
      And the Quote Request is rejected
     Then the Job is updated with a "Canceled" status
      And the JobTimelineEvent table has been updated with "Job cancellation requested"
      And the JobTimelineEvent table has been updated with "Job canceled"
      And the JobTimelineEvent table has been updated with "Quote Approval Declined Notification"
