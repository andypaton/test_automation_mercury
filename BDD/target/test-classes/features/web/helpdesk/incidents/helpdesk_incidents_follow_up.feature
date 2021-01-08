@helpdesk @helpdesk_incidents @helpdesk_incidents_follow_up
Feature: Helpdesk - Incidents - Follow up

  # functionality not possible for UKRB : RDATA-2
  @uswm
  Scenario: Follow up - No further Follow up required - Additional contact
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Follow ups" link on Incidents monitor page
      And the incident reference number for incident type "Refrigeration outage" with status "Incident Followup" is clicked from "Follow ups" monitor
      And "Follow Up" is clicked from the Actions dropdown
     When all follow up questions are answered for "No further follow up"
      And the update notes are entered on follow up page
      And the "Update Follow Up" button is clicked
     Then the Emails text is displayed correctly on incident escalation page
      And the escalation email address is displayed
      And user can add additional user on Escalation page
           
  # functionality not possible for UKRB : RDATA-2
  @uswm
  Scenario: Follow up - No further Follow up required - check messages on Send Escalation page
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Follow ups" link on Incidents monitor page
      And the incident reference number for incident type "Refrigeration outage" with status "Incident Followup" is clicked from "Follow ups" monitor
      And "Follow Up" is clicked from the Actions dropdown
      And all follow up questions are answered for "No further follow up"
      And the update notes are entered on follow up page
      And the "Update Follow Up" button is clicked
      And the message is entered on incident escalation page
     When the "Next" button is clicked
     Then the Email text is displayed correctly on send escalation page 
      And the text message is displayed correctly on send escalation page
     
  @ukrb @uswm
  @bugRainbow
  Scenario: Follow up - No further Follow up required - check status [bug: MCP-13556]
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Pending To Do" link on Incidents monitor page
      And the user has "Death caused by FM Issue" Incident type 
      And the incident reference number with escalation criteria is clicked
      And the incident is reviewed
      And the "Save" button is clicked
      And "Follow Up" is clicked from the Actions dropdown
      And all follow up questions are answered for "No further follow up"
      And the update notes are entered on follow up page
      And the "Update Follow Up" button is clicked
      And the message is entered on incident escalation page
      And the "Next" button is clicked
     When the "Send Escalations" button is clicked 
     Then the incident status is now "Logged"
            
  # functionality not possible for UKRB : RDATA-2
  @uswm
  Scenario: Follow up - Further Follow up required - user can add additional contact
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Follow ups" link on Incidents monitor page
      And the incident reference number for incident type "Refrigeration outage" with status "Incident Followup" is clicked from "Follow ups" monitor
      And "Follow Up" is clicked from the Actions dropdown
     When all follow up questions are answered for "Further follow up"
      And the update notes are entered on follow up page
      And the "Update Follow Up" button is clicked
     Then the Emails text is displayed correctly on incident escalation page
      And the escalation email address is displayed
      And user can add additional user on Escalation page
               
  # functionality not possible for UKRB : RDATA-2
  @uswm
  Scenario: Follow up - Further Follow up required - follow up time is displayed
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Follow ups" link on Incidents monitor page
      And the incident reference number for incident type "Refrigeration outage" with status "Incident Followup" is clicked from "Follow ups" monitor
      And "Follow Up" is clicked from the Actions dropdown
      And all follow up questions are answered for "Further follow up"
      And the update notes are entered on follow up page
      And the "Update Follow Up" button is clicked
      And the message is entered on incident escalation page  
     When the "Next" button is clicked
     Then the Email text is displayed correctly on send escalation page 
      And the text message is displayed correctly on send escalation page 
      And the next follow up time is displayed 
      And user can change the next follow up time 
       
  # functionality not possible for UKRB : RDATA-2
  @uswm
  Scenario: Follow up - Further Follow up required - status changed to Incident followup
    Given a "Helpdesk Operator" has logged in
      And the user clicks "Follow ups" link on Incidents monitor page
      And the incident reference number for incident type "Refrigeration outage" with status "Incident Followup" is clicked from "Follow ups" monitor
      And "Follow Up" is clicked from the Actions dropdown
      And all follow up questions are answered for "Further follow up"
      And the update notes are entered on follow up page
      And the "Update Follow Up" button is clicked
      And the message is entered on incident escalation page  
      And the "Next" button is clicked
      And user can change the next follow up time
     When the "Send Escalations" button is clicked
     Then the incident status is now "Incident Followup"