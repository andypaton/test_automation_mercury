@helpdesk @helpdesk_incidents @helpdesk_incidents_last_viewed
@ukrb @uswm
Feature: Helpdesk - Incidents - Last Viewed

  Scenario: Last viewed incident list - content
    Given a "Helpdesk Operator" has logged in
     When a search is run for "11" incidents
     Then the Last Viewed list displays upto the last "10" incident numbers with store names
      And the Last Viewed incident list is updated immediately
  
  #@MTA-679
  Scenario: Last viewed incident list - incident selection
    Given a "Helpdesk Operator" has logged in
     When an incident is selected from the Last Viewed list
     Then a new tab is opened with the incident details

  Scenario: Last viewed incident list - new session
    Given a "Helpdesk Operator" has logged in
      And a search is run for "2" incidents
     When the user logs out and then back in
     Then the Last Viewed list displays the last "2" incident numbers with store names
     