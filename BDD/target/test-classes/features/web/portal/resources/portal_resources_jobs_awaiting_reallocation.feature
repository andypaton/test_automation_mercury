@mcp @portal @portal_resources @portal_resources_jobs_awaiting_reallocation
Feature: Portal - Resources - Jobs Awaiting Reallocation

##########################################################################################################################################
### Notes:
### The 'Jobs Awaiting Reallocation' table will not show jobs which are currently at a status of 'OnSite' ...
###                                        and will only display jobs that have been accepted by the tech
###
### Jobs with resource status as "Awaiting Parts Review" need to be excluded from our search results. 
### Even though they sit in the jobs awaiting reallocation screen, they can't be reallocated until RFM approves or rejects Parts order.
##########################################################################################################################################

  # note : current framework expects all UKRB tests in UAT and TEST environments to be simelar - however for this scenario UAT and TEST are different
  # updating scenarios for TEST env!!! 
#  @ukrb
#  Scenario: Outstanding activities displayed after login
#    Given a "RFM" with jobs "Awaiting Reallocation"
#     When the user logs in
#     Then "Jobs Awaiting Reallocation" is displayed on the table of outstanding activities "without" counts
     
  Scenario: Outstanding activities displayed after login
    Given a "RFM" with jobs "Awaiting Reallocation"
     When the user logs in
     Then "Jobs Awaiting Reallocation" is displayed on the table of outstanding activities "with" counts
     
  Scenario: RFM views Jobs Awaiting Reallocation
    Given a "RFM" with jobs "Awaiting Reallocation"
      And the user logs in
     When the "Jobs Awaiting Reallocation" sub menu is selected from the "Resources" top menu
     Then the "Jobs Awaiting Reallocation" form displays correctly
      And the "Jobs Awaiting Reallocation" table on the "Jobs Awaiting Reallocation" page displays expected headers
      And a search can be run on the "Jobs Awaiting Reallocation" table
      And covering resources include "Resources in the area, Other Resources"
      And only available resources are included in covering resources list
          
  Scenario: Line Manager views Job Awaiting Reallocation with no covering resource
    Given a "RFM" with jobs "Awaiting Reallocation" and no covering resource
      And the user logs in
      And the "Jobs Awaiting Reallocation" sub menu is selected from the "Resources" top menu
     When the user "Searches" a "Job Awaiting Reallocation"
     Then the 'Reallocate' button in the 'Jobs Awaiting Reallocation' page is disabled until a covering resource is selected
     
  Scenario: Job Awaiting Reallocation - Show contractors
    Given a "RFM" with jobs "Awaiting Reallocation" and no covering resource
      And the user logs in
      And the "Jobs Awaiting Reallocation" sub menu is selected from the "Resources" top menu
      And the user "Searches" a "Job Awaiting Reallocation"
     When Show Contractors is selected
     Then covering resources include "Contractors in the area"
     
  # A line of code  in the last assertion step of this scenario ( And the Timeline Event Summary has been updated with "Resource Removed") should be uncommented once MCP - 7288 is resolved
  @bugRainbow
  Scenario: Line Manager reallocates job to a resource with ipad [bug: MCP-7288]
    Given a "RFM" with jobs "Awaiting Reallocation"
      And the user logs in
      And the "Jobs Awaiting Reallocation" sub menu is selected from the "Resources" top menu
      And the user "Searches" a "Job Awaiting Reallocation"
     When the job is reallocated to other city resource with ipad
     Then the job will not appear on the "Jobs Awaiting Reallocation In Your Region" screen
      And the Resource Assignment table has been updated with the status "New Job Notification Sent"
      And the Timeline Event Summary has been updated with "notification sent"
      And the Timeline Event Summary has been updated with "Resource Added - New Job Notification Sent"
      And the Timeline Event Summary has been updated with "Resource Removed"

  # A line of code  in the last assertion step of this scenario ( And the Timeline Event Summary has been updated with "Resource Removed") should be uncommented once MCP - 7288 is resolved
  Scenario: Line Manager reallocates job to a resource without ipad
    Given a "RFM" with jobs "Awaiting Reallocation"
      And the user logs in
      And the "Jobs Awaiting Reallocation" sub menu is selected from the "Resources" top menu
      And the user "Searches" a "Job Awaiting Reallocation"
     When the job is reallocated to other city resource without ipad
     Then the job will not appear on the "Jobs Awaiting Reallocation In Your Region" screen
      And the Resource Assignment table has been updated with the status "Call Required"
      And the Timeline Event Summary has been updated with "Resource Added - Call Required"
      And the Timeline Event Summary has been updated with "Resource Removed"

  Scenario: Completed jobs whose resource is absent
    Given a RFM with a completed job whose resource is absent
      And the user logs in
      When the "Jobs Awaiting Reallocation" sub menu is selected from the "Resources" top menu
     Then the job will not appear on the "Jobs Awaiting Reallocation In Your Region" screen
