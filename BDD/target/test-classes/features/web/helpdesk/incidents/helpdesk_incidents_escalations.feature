@helpdesk @helpdesk_incidents @helpdesk_incidents_esculations
Feature: Helpdesk - Incidents - Escalations

  @uswm
  Scenario: Escalating an Incident with Follow up
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Pending To Do" link on Incidents monitor page
      And the user has "Refrigeration Outage" Incident type 
      And the incident reference number with escalation criteria is clicked
      And the incident is reviewed
      And the "Save" button is clicked
      And the user has updated incident for further follow up criteria
     When "Escalate" is clicked from the Actions dropdown
      And the message is entered on incident escalation page 
      And the "Next" button is clicked
      And the "Send Escalations" button is clicked
     Then the incident status is changed to "Incident Followup"
       
  @ukrb @uswm
  Scenario: Escalating an Incident with no Follow up
    Given a "Helpdesk Operator" has logged in
      And the user has "Death caused by FM Issue" Incident type
      And the user clicks "Pending To Do" link on Incidents monitor page 
      And the incident reference number with escalation criteria is clicked
      And the incident is reviewed
      And the "Save" button is clicked 
     When "Escalate" is clicked from the Actions dropdown
      And the message is entered on incident escalation page 
      And the "Next" button is clicked
     Then the text message is displayed correctly on send escalation page 
      And the "Send Escalations" button is clicked
      And the incident status is changed to "Logged"
      And the Incident Escalations tab contains a row for "Initial Escalation"
           
  @ukrb @uswm
  Scenario: Reviewing an Incident - no escalation criteria met
    Given a "Helpdesk Operator" has logged in 
      And the incident reference number with status "Incident Initial Review" is searched via a search bar
      And "Review" is clicked from the Actions dropdown
      And user can edit description, site questions and answer fields
      And user clicks the complete review checkbox
     When the "Save" button is clicked
     Then the incident status is changed to "Logged"
         
  @ukrb @uswm
  Scenario: Reviewing an Incident - escalation criteria met
    Given a "Helpdesk Operator" has logged in 
      And the user clicks "Reviews" link on Incidents monitor page
      And the incident reference number with escalation criteria is clicked
      And "Review" is clicked from the Actions dropdown
     When user clicks the complete review checkbox
      And the "Save" button is clicked 
      And the message is entered on incident escalation page
      And the "Next" button is clicked
      And the text message is displayed correctly on send escalation page 
      And the "Send Escalations" button is clicked
     Then the incident status is changed to one of "Incident Followup/Logged"
      And the Incident Escalations tab contains a row for "Initial Escalation"
      
  @ukrb @uswm
  Scenario: Escalating an Incident - change escalation contact preference and no follow up
    Given a "Helpdesk Operator" has logged in
      And the user has "Death caused by FM Issue" Incident type
      And a search is run for an Incident reference number with an escalation contacts 
      And "Escalate" is clicked from the Actions dropdown
      And the message is entered on incident escalation page
     When the communication method for an escalation contact is changed to "Skip"
      And the "Next" button is clicked
      And the "Send Escalations" button is clicked
     Then the incident status is changed to "Logged"
      And the communication method for an escalation contact is updated
      
  @uswm
  Scenario: Escalating an Incident - change escalation contact preference and follow up
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Pending To Do" link on Incidents monitor page
      And the user has "Refrigeration Outage" Incident type 
      And the incident reference number with escalation criteria is clicked
      And the incident is reviewed
      And the "Save" button is clicked
      And the user has updated incident for further follow up criteria
      And "Escalate" is clicked from the Actions dropdown
      And the message is entered on incident escalation page
     When the communication method for an escalation contact is changed to "Skip"
      And the "Next" button is clicked
      And the "Send Escalations" button is clicked
     Then the incident status is changed to "Incident Followup"
      And the communication method for an escalation contact is updated
      
  @ukrb @uswm
  Scenario: Escalating an Incident - adding an escalation contact and preference
    Given a "Helpdesk Operator" has logged in
      And a search is run for an Incident reference number with an escalation contacts
      And "Escalate" is clicked from the Actions dropdown
      And the message is entered on incident escalation page
      And system displays the escalation contacts
     When user adds an additonal contact with contact preference
     Then the added contact is displayed in additional tab