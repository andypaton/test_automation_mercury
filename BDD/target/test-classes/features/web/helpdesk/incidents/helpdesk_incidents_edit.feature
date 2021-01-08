@helpdesk @helpdesk_incidents @helpdesk_incidents_edit
@ukrb @uswm
Feature: Helpdesk - Incidents - Edit

  #@MTA-225
  Scenario: Incident User adding a Note
    Given a "Helpdesk Operator" has logged in
      And a search is run for an Incident reference number
     When the "Add note" button is clicked
      And the notes are entered
      And the "Save" button is clicked
     Then the Incident timeline displays "Type" as "Incident update - Note"
       
  #@MTA-225 
  Scenario: Incident User updating a Note
    Given a "Helpdesk Operator" has logged in
      And a search is run for an Incident reference number with a note
     When the "View" button is clicked
      And the incident update page is displayed
      And the "Save" button is clicked
     Then the Incident timeline displays "Type" as "Update note updated"
           
  #@MTA-244 @MTA-549
  Scenario: Review an incident
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
      And the "Log an incident" button is clicked
      And the Incident description is entered
      And the Core details are entered
      And the site questions are answered when the site is closed
      And the Incident Type "Refrigeration Outage" is selected
      And all incident questions are answered
      And the "Save" button is clicked
      And the Incident Summary page is displayed
     When the incident is reviewed
      And the "Save" button is clicked
     Then the Incident timeline displays "Description" as "Incident was reviewed"
         
  Scenario: Editing an Incident after review
    Given a "Helpdesk Operator" has logged in
      And a search is run for an Incident reference number with site closed
     When "Edit" is clicked from the Actions dropdown
      And the Incident description is updated as "description updated"
      And the core details and site questions are updated
      And all incident questions are answered
      And the "Save only" button is clicked
     Then the description is displayed correctly
      And the core details and site questions are displayed correctly
      And the Incident timeline displays "Description" as "re-assessed for H and S referral"
      And the Incident timeline displays the updated core details
      And the Incident timeline displays "Type" as "Incident edited"
      And the Answers tab displays the all incident questions asked with answers 