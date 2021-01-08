@helpdesk @helpdesk_search
@mcp
Feature: Helpdesk - Search
  
  Scenario Outline: search for a "<siteType>" site
    Given a "Helpdesk Operator" has logged in
     When a search is run for an "<siteType>" site
     Then a new tab is opened with the site details
      And the search bar contains a lozenge with site name and code
      And open jobs for the site are displayed
      And "Log an incident" and "Log a job" buttons are enabled
      And a site card is displayed
      And a job card is not displayed
      And the icon tool tips are displayed
    Examples: 
      | siteType   | 
      | Occupied   | 
      | Unoccupied | 
  
  Scenario Outline: search for a "<siteType>" site
    Given a "Helpdesk Operator" has logged in
     When a search is run for a "<siteType>" site
     Then a new tab is opened with the site details
      And "Log an incident" and "Log a job" buttons are disabled
    Examples: 
      | siteType   | 
      | Sold       | 
      | Demolished | 
  
  Scenario: search for an active resource
    Given a "Helpdesk Operator" has logged in
     When a search is run for an "active" resource
     Then a new tab is opened with the resource details
     And a resource card is displayed
  
  Scenario: search for an inactive resource
    Given a "Helpdesk Operator" has logged in
     When a search is run for a "inactive" resource
     Then no matching "user" is found
  
  Scenario: search for a asset subtype
    Given a "Helpdesk Operator" has logged in
     When a search is run for an asset subtype
     Then a new tab is opened with the asset details

  Scenario: search for a job
    Given a "Helpdesk Operator" has logged in
     When a search is run for a random job
     Then a new tab is opened with the job details
      And the client status is correctly displayed
      And a job card is displayed
  
  Scenario Outline: search for a cancelled "<type>" job
        * using dataset "helpdesk_search_001"
    Given an "IT" user has logged in
     When a search is run for a cancelled "<type>" job
     Then a new tab is opened with the job details
      And the client status is "Cancelled"
      And a job card is displayed
      And the available job actions includes "<actions>"
    Examples:
      | type     | actions |
      | Reactive | Manage Incidents, Quotes, Reopen Job, Chase, Confirm Warranty |
      | Quote    | Manage Incidents, Quotes, Reopen Job, Chase |
  
  Scenario: search for a client caller
    Given a "Helpdesk Operator" has logged in
     When a search is run for a client caller
     Then the search bar contains lozenges for site and caller
      And caller details are displayed
      And a caller card is displayed
      And a site card is displayed
  
  Scenario: search for a sites existing caller
    Given a "Helpdesk Operator" has logged in
     When a search is run for an occupied site and an existing caller
     Then caller details are displayed
  
