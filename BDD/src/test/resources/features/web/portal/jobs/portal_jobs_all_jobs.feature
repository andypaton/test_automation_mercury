@portal @portal_jobs @portal_jobs_all_jobs
Feature: Portal - Jobs - All Jobs

  @mcp
  Scenario: Contractor Admin selects All Jobs from the Jobs menu
    Given a portal user with profile "Contractor Admin" 
      And the user logs in
     When the "All Jobs" sub menu is selected from the "Jobs" top menu
     Then the "All Jobs" form displays correctly
      And the Find button will be disabled if there are no dates in fields
      | From        |
      | To          |
      | From and To |
      
  @uswm @bugWalmart
  Scenario: Operations Director selects All Jobs from the Jobs menu [bug: MCP-18048]
     Given a "Operations Director" with access to the "Jobs > All Jobs" menu
      And the user logs in
      When the "All Jobs" sub menu is selected from the "Jobs" top menu
      Then the Find button will be disabled if there are no dates in fields
      | From        |
      | To          |
      | From and To |
            
  @mcp @grid 
  @bugAdvocate @bugWalmart @bugRainbow
  Scenario: Find All Jobs for date range - Contractor Admin [bug: MCP-15441]
    Given a portal user with a "Contractor Admin" profile and with "Open" Jobs 
      And the user logs in
      And the "All Jobs" sub menu is selected from the "Jobs" top menu
     When dates are entered
      And the "Find" button is clicked
     Then the "All Jobs" table is displayed
      And a search can be run on the "All Jobs" table 
      And the "All Jobs" table can be sorted on all columns
                     
  @uswm      
  @bugWalmart @bugRainbow @notsignedoff
  @grid
  Scenario: Find All Jobs for date range - Operations Director [bug: MCP-15441]
     Given a "Operations Director" with access to the "Jobs > All Jobs" menu
      And the user logs in
      And the "All Jobs" sub menu is selected from the "Jobs" top menu
     When a "3" day date window is entered
      And the "Find" button is clicked
     Then "All Jobs" table displays headers "Job reference, Assignment status, Site, Asset subtype / classification, Serial No, Days outstanding, Reference, ETA"
      And a search can be run on the "All Jobs" table 
      And the "All Jobs" table can be sorted on all columns   
      
  @bugWalmart @uswm @notsignedoff
  @grid
  Scenario: Split RFMs - Operations Director [bug: MCP-15441]
     Given a "Operations Director" with access to the "Jobs > All Jobs" menu
      And the user logs in
      And the "All Jobs" sub menu is selected from the "Jobs" top menu
     When a "3" day date window is entered
      And the "Split RFMs" button is clicked
      And the "Find" button is clicked
     Then a table is displayed for each Split RFM
            
  @mcp
  @grid @bugWalmart @bugAdvocate 
  Scenario: Job selected from All Jobs grid - Contractor Admin [bug: MCP-15441]
    Given a portal user with a "Contractor Admin" profile and with "Open" Jobs 
      And the user logs in
      And the "All Jobs" sub menu is selected from the "Jobs" top menu
      And dates are entered
     When the "Find" button is clicked
      And a job is selected from the grid
     Then the job details page is displayed
     And selecting the browsers Back button returns to the "All Jobs" page   
   
  @bugWalmart @uswm
  Scenario: Job selected from All Jobs grid - Operations Director [bug: MCP-15441]
     Given a "Operations Director" with access to the "Jobs > All Jobs" menu
      And the user logs in
      And the "All Jobs" sub menu is selected from the "Jobs" top menu
      And a "3" day date window is entered
     When the "Find" button is clicked
      And a job is selected from the grid
     Then the job history page is displayed
     And selecting the browsers Back button returns to the "All Jobs" page
    