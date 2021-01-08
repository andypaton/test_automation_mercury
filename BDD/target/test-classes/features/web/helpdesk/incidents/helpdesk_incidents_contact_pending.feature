@helpdesk @helpdesk_incidents @helpdesk_incidents_contact_pending
@ukrb @uswm
Feature: Helpdesk - Incidents - Contact Pending
 
  Scenario: Send Escalation for an Incident with status Incident Initial Review
    Given a "Helpdesk Operator" has logged in
      And the incident reference number with status "Incident Initial Review" is searched via a search bar
      And the incident is reviewed
      And the "Save" button is clicked
      And "Escalate" is clicked from the Actions dropdown
      And the message is entered on incident escalation page
      And user can add additional user on Escalation page
     When the "Next" button is clicked
      And the "Send Escalations" button is clicked
     Then the incident status is now "Incident Telephone Escalation Callback"
    
  #@MTA-338
  Scenario: Send Escalation for an Incident with status Incident Telephone Escalation Callback
    Given a "Helpdesk Operator" has logged in
      And a search is run for an Incident reference number with status Incident Telephone Escalation Callback 
     When "Escalation Calls" is clicked from the Actions dropdown
      And user clicks the called checkbox on incident send escalation page
      And the "Send Escalations" button is clicked
     Then the incident status is now "Logged"