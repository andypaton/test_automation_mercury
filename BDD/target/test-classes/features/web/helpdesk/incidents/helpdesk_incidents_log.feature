@helpdesk @helpdesk_incidents @helpdesk_incidents_log
@ukrb @uswm
Feature: Helpdesk - Incidents - Log an Incident

  @smoke
  Scenario: log an incident - Site closed
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log an incident" button is clicked
      And the Incident description is entered 
      And the Core details are entered
      And the site questions are answered when the site is closed 
      And the Incident Type "Refrigeration Outage" is selected 
      And all incident questions are answered
      And an incident is saved
     Then the Incident Summary page is displayed
      And the Incident Timeline contains a new row for Description "Incident was logged" and Type "Incident logged" 
      And the Answers tab displays the all incident questions asked with answers 
      
  @bugRainbow
  Scenario: log an incident - All Incident Types [bug: MCP-13556]
    Given a "Helpdesk Operator" has logged in
     When a search is run for an "occupied" City Tech store with an existing caller
     Then user can log all types of incidents
   
  Scenario: log an incident - Site closed but has re-opened
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log an incident" button is clicked
      And the Incident description is entered
      And the Core details are entered
      And the site questions are answered when the site is closed but has re-opened 
      And the Incident Type "Refrigeration Outage" is selected
      And all incident questions are answered
      And an incident is saved
     Then the Incident Summary page is displayed 
      And the Incident Timeline contains a new row for Description "Incident was logged" and Type "Incident logged"
      And the Answers tab displays the all incident questions asked with answers
   
  Scenario: log an incident - Department closed
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log an incident" button is clicked
      And the Incident description is entered
      And the Core details are entered
      And the site questions are answered when the department is closed 
      And the Incident Type "Refrigeration Outage" is selected
      And all incident questions are answered
      And an incident is saved
     Then the Incident Summary page is displayed 
      And the Incident Timeline contains a new row for Description "Incident was logged" and Type "Incident logged"
      And the Answers tab displays the all incident questions asked with answers

  Scenario: log an incident - Department closed but has reopened
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log an incident" button is clicked
      And the Incident description is entered 
      And the Core details are entered
      And the site questions are answered when the department is closed but has reopened
      And the Incident Type "Refrigeration Outage" is selected   
      And all incident questions are answered
      And an incident is saved
     Then the View Incident page is displayed correctly
      And the Incident Timeline contains a new row for Description "Incident was logged" and Type "Incident logged"
      And the Answers tab displays the all incident questions asked with answers
 
  Scenario: log an incident - Nothing closed
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log an incident" button is clicked
      And the Incident description is entered
      And the Core details are entered
      And the site questions are answered when nothing is closed
      And the Incident Type "Refrigeration Outage" is selected
      And all incident questions are answered
      And an incident is saved
     Then the Incident Summary page is displayed 
      And the Incident Timeline contains a new row for Description "Incident was logged" and Type "Incident logged"
      And the Answers tab displays the all incident questions asked with answers

  Scenario: log an incident - Site is open and department is closed
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log an incident" button is clicked
      And the Incident description is entered
      And the Core details are entered
      And the site questions are answered when the site is "open" and department is "closed"  
      And the Incident Type "Refrigeration Outage" is selected
      And all incident questions are answered
      And an incident is saved
     Then the Incident Summary page is displayed
      And the site questions are displayed correctly when the site is "open" and department is "closed" 
      And the Incident Timeline contains a new row for Description "Incident was logged" and Type "Incident logged"
      And the Answers tab displays the all incident questions asked with answers
      
  Scenario: log an incident - Site Open, Department closed but has reopened
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log an incident" button is clicked
      And the Incident description is entered 
      And the Core details are entered
      And the site questions are answered when the site is "open" and department is "open"
      And the Incident Type "Refrigeration Outage" is selected  
      And all incident questions are answered
      And an incident is saved
     Then the Incident Summary page is displayed
      And the site questions are displayed correctly when the site is "open" and department is "open"
      And the Incident Timeline contains a new row for Description "Incident was logged" and Type "Incident logged"
      And the Answers tab displays the all incident questions asked with answers

  Scenario: log an incident - caller details are auto completed
    Given a "Helpdesk Operator" has logged in
      And a search is run for an occupied site with an existing caller
      And the "Log an incident" button is clicked
     When an existing caller is entered 
     Then the phone number, extension and caller type are auto completed