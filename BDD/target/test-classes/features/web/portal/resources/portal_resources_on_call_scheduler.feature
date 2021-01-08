@portal @portal_resources @portal_resources_on_call_scheduler
Feature: Portal - Resources - On Call Scheduler
     
  @mcp
  Scenario: On Call Scheduler - Table View
    Given a portal user with profile "RFM"
      And the user logs in
     When the "On Call Scheduler" sub menu is selected from the "Resources" top menu
     Then the "On Call Scheduler" form is displayed
      And the current week view is displayed
      And resource profiles are available from the dropdown
      And the region which is prefilled
      And time and dates from oncall starting to finishing are displayed
      And the current week displayed can be updated
      And Tech Positions are in a collapsed state
      And the status for Tech Supervisors is verified for the displayed week
        
  # counts not displayed of UKRB   
  @notsignedoff @bugWalmart
  @uswm
  Scenario: On Call Scheduler - Update Rota Entries [bug: MCP-17776]
    Given a portal user with profile "RFM"
      And the user logs in
      And "On Call Schedule RHVAC Technician" is selected from outstanding activities
     When Rota Entries are updated for a site
     Then the outstanding activities count on the Home page is updated
