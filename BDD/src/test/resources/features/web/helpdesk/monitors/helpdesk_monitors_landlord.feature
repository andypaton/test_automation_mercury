@helpdesk @helpdesk_monitors @helpdesk_monitors_landlord
@mcp
Feature: Helpdesk - Monitors - Landlord Jobs
  
   #@MTA-903
   Scenario: Helpdesk Landlord monitor - For Info
    Given a "IT" has logged in
     When the "Landlord Jobs" tile is selected
     Then the "To Do" section displays all configured active monitors for "Landlord Jobs" 
      And the "For Info" section displays all configured active monitors for "Landlord Jobs" 