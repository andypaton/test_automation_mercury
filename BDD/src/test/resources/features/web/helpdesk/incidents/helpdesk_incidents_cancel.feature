@helpdesk @helpdesk_incidents @helpdesk_incidents_cancel
@ukrb @uswm
Feature: Helpdesk - Incidents - Cancel

  #@MTA-194 @MTA-268
  Scenario: Cancel an Incident
    Given a "Helpdesk Operator" has logged in
      And a search is run for an Incident reference number
     When "Cancel Incident" is clicked from the Actions dropdown
      And the 'Confirm' button is clicked on Cancel Incident pop up
      And the Timeline tab is refreshed
     Then the incident status is now "Cancelled"
      And the Incident timeline displays "Description" as "Incident was cancelled"
      And the Incident timeline displays "Type" as "Incident Cancelled"