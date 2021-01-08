@helpdesk @helpdesk_incidents @helpdesk_incidents_permissions
@ukrb @uswm
Feature: Helpdesk - Incidents - Security Permission

  @bugRainbow
  Scenario: A user with incident permissions - log an incident and view an incident [bug: MCP-13556]
    Given a "Helpdesk Operator" has logged in
     When a search is run for an "occupied" City Tech store with an existing caller
     Then user can view incident tab on site view
      And user can log an incident
      And user can view an incident details
                
  #@MTA-287
  Scenario: A user with incident permissions - linked assets panel
    Given a "Helpdesk Operator" has logged in
     When a search is run for an Incident reference number with a linked job 
     Then user can access linked assets panel on job view
    
  #@MTA-287
  Scenario: A user with incident permissions - incident reference number search via search bar
    Given a "Helpdesk Operator" has logged in
     When user search for an incident reference number via search bar 
     Then user can view an incident details
     
  #@MTA-288
  Scenario: A user with no incident permissions - can not see incident monitor
    Given a "OSS Operator" has logged in
     When user is on helpdesk home page 
     Then user can not see incident monitor on helpdesk search page
     
  #@MTA-288
  Scenario: A user with no incident permissions - can not see incident tab on site view
    Given a "OSS Operator" has logged in
     When a search is run for an "occupied" City Tech store with an existing caller
     Then user can not see incident tab on site view 
    
  #@MTA-288 
  Scenario: A user with no incident permissions - can not access linked assets panel
    Given a "OSS Operator" has logged in
     When a search is run for a Job reference number with a linked incident
      And the "Linked Incidents" button is clicked 
     Then user can not see linked assets panel on job view 
 
  #@MTA-288
  Scenario: A user with no incident permissions - can not view incident when search via search bar
    Given a "OSS Operator" has logged in
     When user search for an incident reference number via search bar
     Then user can not view incident details and an error message is displayed 