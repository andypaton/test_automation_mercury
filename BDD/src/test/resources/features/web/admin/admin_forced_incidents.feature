@admin @admin_incidents
Feature: Admin - Incident Configuration

  @uswm @ukrb
  Scenario: Forced Incidents Home page displayed as expected
    Given a user with "Mercury_Admin_Incidents" role has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Incident Configuration" tile is selected
     Then the forced incidents home page is displayed as expected
     
  @uswm @ukrb
  Scenario: New incident criteria - mandatory field error messages are displayed
    Given a user with "Mercury_Admin_Incidents" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Incident Configuration" tile is selected
     When a new criteria is added without completing mandatory fields
     Then the following error is displayed: "Please select whether this criteria is to be potential or forced."
      And the following error is displayed: "Please select an asset sub type."
      And the following error is displayed: "Please select an incident type."
      And the following error is displayed: "Please select a fault type."
      
  @uswm @ukrb
  Scenario: Edit incident criteria - mandatory field error messages are displayed
    Given a user with "Mercury_Admin_Incidents" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Incident Configuration" tile is selected
     When a criteria is edited to have empty mandatory fields
     Then the following error is displayed: "Please select whether this criteria is to be potential or forced."
      And the following error is displayed: "Please select an asset sub type."
      And the following error is displayed: "Please select an incident type."
      And the following error is displayed: "Please select a fault type."
     
  @uswm @ukrb
  Scenario: Add new incident criteria
    Given a user with "Mercury_Admin_Incidents" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Incident Configuration" tile is selected
     When a new criteria is added
     Then the new criteria is added to the relevant table
      And an audit history is now present
   
  @uswm @ukrb
  Scenario: Edit incident criteria
    Given a user with "Mercury_Admin_Incidents" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Incident Configuration" tile is selected
     When a criteria is edited
     Then the new criteria is added to the relevant table
      And an audit history is now present