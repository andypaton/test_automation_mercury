@admin @admin_jobs
@mcp
Feature: Admin - Jobs

  Scenario: Admin Jobs - Home Screen
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "Jobs" tile is selected
     Then the "Jobs" table on the "Jobs" page displays correctly
      And a search box is present on the "Jobs" page
      And the note "Enter the first few characters of job reference, then click Search" is displayed above the search box 
      And the mercury navigation menu in the admin "Jobs" page displays options correctly
      And the "Job Status Admin" menu option in the admin "Jobs" page is highlighted
     
  Scenario: Job Status Admin - User is presented with a table grid showing a list of job numbers with the specified characters
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Jobs" tile is selected
     When the user enters first few numberical characters of job number and selects search
     Then the "Jobs" table on the "Job Status Admin" page displays each row correctly
     
  Scenario: Job Status Admin - User searches and selects a job
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Jobs" tile is selected
     When the admin user searches and selects a job
     Then the Admin Job Status Edit page will be displayed with warning "Warning! This will override the standard system behavior. Use with care.", job details and save button
     
  Scenario: On Call Scheduler Admin
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Jobs" tile is selected
     When the user selects "On Call Scheduler" from the sub menu
     Then the "Weekly Schedule Start Day, Out Of Hours Times" sections are dipslayed on the On Call Scheduler Admin page
      And the Start of the Week drop-down is displayed with each day of the week available for selection
      And the "Out Of Hours Times" table on the "On Call Scheduler Admin" page displays correctly
      And the "Out Of Hours Times" table on the "On Call Scheduler Admin" page displays each row correctly
      And the "Save Changes" button is disabled
      
  Scenario: On Call Scheduler - Editing Start of Week Day
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Jobs" tile is selected
      And the user selects "On Call Scheduler" from the sub menu
     When the user edits Start Of The Week Day
      And the "Save Changes" button is clicked
     Then "Success" popup alert is displayed with text "Changes have been saved."
      And the new Start Of The Week Day is saved to the database
      And the "Save Changes" button is disabled
      
  Scenario: On Call Scheduler - User clicks Edit button for a day in Out of Hours Times
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Jobs" tile is selected
      And the user selects "On Call Scheduler" from the sub menu
     When the user clicks Edit button for the day they wish to amend
     Then the Start Time and End Time Fields for user selected day are available for editing
      And the Start Time and End Time Fields for other days are not available for editing
      
  Scenario: On Call Scheduler - Editing Out of Hours Times
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Jobs" tile is selected
      And the user selects "On Call Scheduler" from the sub menu
     When the user clicks Edit button for the day they wish to amend
      And selects new Start Time and End Time
      And the "Update" button is clicked
     Then the new Start Time and End Time is updated in the database