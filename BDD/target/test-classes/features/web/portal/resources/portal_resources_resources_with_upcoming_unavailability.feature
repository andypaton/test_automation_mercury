@portal @portal @portal_resources @portal_resources_upcoming_unavailability
Feature: Portal - Resources - Resources with Upcoming Unavailability

  # In these test scenarios a Supervisor can be any of: RFM / HVAC Supervisor / Refrigeration Supervisor
  
  ########################################################################################
  # Business Team talk of 'District Directors' - but there is no such thing in the DB. 
  # They call Phil Shiver a 'District Director' and for him:
  #                 User Profile = Divisional Manager
  #                 Resource Profile = Divisional Manager
  #                 Resource Type = Operational Manager
  ########################################################################################
  
  # note : current framework expects all UKRB tests in UAT and TEST environments to be simelar - however for this scenario UAT and TEST are different
  # updating scenarios for TEST env!!! 
#  @ukrb
#  Scenario: Outstanding activities displayed after login
#    Given a "RFM" with jobs "Awaiting Reallocation"
#     When the user logs in
#     Then "Resources with Upcoming Unavailability" is displayed on the table of outstanding activities "without" counts
     
  @mcp
  Scenario: Outstanding activities displayed after login
    Given a "RFM" with jobs "Awaiting Reallocation"
     When the user logs in
     Then "Resources with Upcoming Unavailability" is displayed on the table of outstanding activities "with" counts
     
     
#########################################
  @mcp
  Scenario Outline: Resources with upcoming unavailability page - "<manager>"
    Given an "<manager>" with access to the "Resources > Resources with upcoming unavailability" menu
      And the user logs in
     When the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     Then the "<table>" table on the "Resources with upcoming unavailability" page displays correctly
    Examples: 
      | manager             | table                 |
      | RFM                 | My Resources          |
      | Divisional Manager  | Resource Availability |
  
  @uswm
  Scenario Outline: Resources with upcoming unavailability page - "<manager>"
    Given an "<manager>" with access to the "Resources > Resources with upcoming unavailability" menu
      And the user logs in
     When the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     Then the "<table>" table on the "Resources with upcoming unavailability" page displays correctly
    Examples: 
      | manager             | table                 |
      | Operations Director | Resource Availability |
      
      
        
#########################################
  @bugWalmart @mcp
  Scenario Outline: "<manager>" with upcoming absence for "<minion>" without a covering resource [bug: MCP-21949]
    Given a "<manager>" with a "<minion>" with upcoming unavailability without a covering resource assigned
      And the user logs in
     When the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     Then the resource unavailability will appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion    |
      | RFM                 | City Tech | 
      | Divisional Manager  | RFM       |

  @uswm
  Scenario Outline: "<manager>" with upcoming absence for "<minion>" without a covering resource
    Given a "<manager>" with a "<minion>" with upcoming unavailability without a covering resource assigned
      And the user logs in
     When the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     Then the resource unavailability will appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion    |
      | Operations Director | RFM       |
      
  # merge with above scenario after bug fixed! 
  @uswm
  @bugWalmart
  Scenario Outline: "<manager>" with upcoming absence for "<minion>" without a covering resource [bug: MCP-11259]
    Given a "<manager>" with a "<minion>" with upcoming unavailability without a covering resource assigned
      And the user logs in
     When the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     Then the resource unavailability will appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion             |
      | Operations Director | Divisional Manager |
      
      
      
#########################################
   @mcp
   Scenario Outline: "<manager>" with upcoming absence for "<minion>" with a covering resource
    Given a "<manager>" with a "<minion>" with upcoming unavailability with a covering resource assigned
      And the user logs in
     When the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     Then the resource unavailability will not appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion             |
      | RFM                 | City Tech          | 
      | Divisional Manager  | RFM                |
      
   @uswm
   Scenario Outline: "<manager>" with upcoming absence for "<minion>" with a covering resource
    Given a "<manager>" with a "<minion>" with upcoming unavailability with a covering resource assigned
      And the user logs in
     When the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     Then the resource unavailability will not appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion             |
      | Operations Director | RFM                |
      | Operations Director | Divisional Manager |


#########################################
  @bugWalmart @mcp
  Scenario Outline: "<manager>" with upcoming absence for "<minion>" without a covering resource assigns a backup resource [bug: MCP-21949]
    Given a "<manager>" with a "<minion>" with upcoming unavailability without a covering resource assigned
      And the user logs in
      And the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     When a backup resource is assigned
     Then the resource unavailability will not appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion             |
      | RFM                 | City Tech          | 
      | Divisional Manager  | RFM                |
  
  @uswm
  Scenario Outline: "<manager>" with upcoming absence for "<minion>" without a covering resource assigns a backup resource
    Given a "<manager>" with a "<minion>" with upcoming unavailability without a covering resource assigned
      And the user logs in
      And the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     When a backup resource is assigned
     Then the resource unavailability will not appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion             |
      | Operations Director | RFM                |

  # merge with above scenario after bug fixed! 
  @uswm
  @bugWalmart
  Scenario Outline: "<manager>" with upcoming absence for "<minion>" without a covering resource assigns a backup resource [bug: MCP-11259]
    Given a "<manager>" with a "<minion>" with upcoming unavailability without a covering resource assigned
      And the user logs in
      And the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     When a backup resource is assigned
     Then the resource unavailability will not appear on the Line Managers Resource Unavailability screen
    Examples: 
      | manager             | minion             |
      | Operations Director | Divisional Manager |
      
      
      
#########################################
  @uswm @ukrb @usah
  Scenario: "RFM" views open jobs for unavailable resource
    Given a "RFM" with jobs "Awaiting Reallocation" and no covering resource
      And the user logs in
      And the "Resources with upcoming unavailability" sub menu is selected from the "Resources" top menu
     When the resource name is selected from the Resource Availability grid
     Then the "Jobs Awaiting Reallocation" table for that resource is displayed 
      And the 'Reallocate' button in the 'Jobs Awaiting Reallocation for Resource' page is disabled until a covering resource is selected

      