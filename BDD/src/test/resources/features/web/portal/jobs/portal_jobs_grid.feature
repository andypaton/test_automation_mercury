@portal @portal_jobs @portal_jobs_grid
@mcp
Feature: Portal - Jobs - Grid
  
  Scenario: Verify awaiting jobs grid with "Contractor Admin" profile
    Given a "Contractor Admin" with "Awaiting Acceptance" jobs
     When the user logs in
      And the user is viewing the Jobs Awaiting Acceptance grid
     Then "Jobs Awaiting Acceptance" table displays headers "Job Reference, Status, Asset subtype/classification, Asset No, Priority, Priority, Days outstanding"
      And "Jobs Awaiting Acceptance" table displays sub-headers "Job Type, Fault Type, Serial No, Response, Repair, Logged Date"
      And the awaiting jobs grid displays data correctly

  @bugWalmart @bugAdvocate
  Scenario Outline: Contractor views open "<JOB TYPE>" jobs [bug: MCP-21559]
    Given a Contractor with an "<JOB TYPE>" job
     When the user logs in
      And the user is viewing the open jobs grid
     Then the job is displayed in the Open Jobs grid
    Examples:
      | JOB TYPE |
      | Reactive |
#      | PPM      | - need to update scenario to create data
#      | Quote    | - need to update scenario to create data
 
  @bugRainbow @bugWalmart
  Scenario: Verify open jobs grid with "Contractor Admin" profile [bug: MCP-13767, MCP-21709]
    Given a "Contractor Admin" with an "Open" job
     When the user logs in
      And the user is viewing the open jobs grid
     Then the "Open" jobs grid displays data correctly
      And the job is displayed in the Open Jobs grid
      And a search can be run on the "Open Jobs" table 
      And the "Open Jobs" table can be sorted on all columns
      And the "Open Jobs" table excludes jobs that are 'Deferred', 'Declined', 'Removed' 'Cancelled' and 'Fixed'
      And clicking on a job on the "Open Jobs" table will take the user to the 'Job Details' screen
      
      
  #@MTA-661
  @grid
  Scenario: Verify open jobs by site grid with "Contractor Admin" profile
    Given a "Contractor Admin" with "Open" jobs
      And the user logs in
     When the "Open Jobs By Site" sub menu is selected from the "Jobs" top menu
     Then the open jobs by site grid displays data correctly
      And the number of "Awaiting Acceptance" jobs displayed on the table is correct
      And the number of "Allocated" jobs displayed on the table is correct
        