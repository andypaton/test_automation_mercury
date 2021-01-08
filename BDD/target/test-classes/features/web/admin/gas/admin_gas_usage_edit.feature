@admin @admin_fgas
Feature: Admin - Refrigerant Gas Usage - Edit

  @mcp
  Scenario: View site visit details for FGAS job
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     Then the job details are displayed
      And 2019 FGas Refrigerant Gas questions completed by the engineer are displayed
      And Leak Inspection and Repair questions completed by the engineer are displayed
      
  @mcp
  Scenario: Edit Appliance Information - Read Only questions
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     Then Refrigerant Gas Usage question "Refrigerant Gas used during this visit" is read only
      And all answers except Appliance Type can be updated in the Appliance Information section
     
  @uswm @ukrb @usah
  Scenario: Appliance Identification - Update that will result in change to Refrigerant Type
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a Remote System Appliance Type
      And the Refrigerant Gas Usage visit is viewed
     When the gas usage Appliance Identification is updated so that a change in Refrigerant Type will be required
     Then "Appliance Identification Changed" popup alert is displayed with text "Changing the Appliance Identification will remove all refrigerant related details recorded against this visit, are you sure?"
     
  @uswm @ukrb @usah
  Scenario: Appliance Identification - Update to a valid alternative
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a Remote System Appliance Type
      And the Refrigerant Gas Usage visit is viewed
     When Appliance Identification is updated to a valid alternative
     Then "Please provide appliance details" is not requested
      And "Refrigerant Type Used" can be set to "Incorrect refrigerant type displayed"

  @uswm @ukrb @usah
  Scenario: Appliance Identification - Update to Appliance not on list
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a Remote System Appliance Type
      And the Refrigerant Gas Usage visit is viewed
     When Appliance Identification is updated to "Appliance not on list"
     Then "Please provide appliance details" is requested
      And Refrigerant Type Used can be set to any active refrigerant type
      And "Refrigerant Type Used" can not be set to "Incorrect refrigerant type displayed"
      
  @mcp
  Scenario: Receiver Level recorded - Yes
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the gas usage "Has receiver level been recorded?" is updated to "Yes"  
     Then either "Quantity of Floating Balls" or "Level Indicator %" is mandatory
      And Quantity of Balls Floating must be a whole number between "0" and "4"

  @mcp
  Scenario: Receiver Level recorded - No
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the gas usage "Has receiver level been recorded?" is updated to "No"  
     Then "Quantity of Balls Floating" is not requested
      And "Provide Level Indicator %" is not requested
      
  @uswm @ukrb @usah
  Scenario: Edit Refrigerant Type Used - Change refrigerant type
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a Remote System Appliance Type
      And the Refrigerant Gas Usage visit is viewed
      And Appliance Identification is not "Appliance not on list"
     When the gas usage "Refrigerant Type Used" is updated to "Incorrect refrigerant type displayed"
     Then "Refrigerant Type Changed" popup alert is displayed with text "Changing the Refrigerant Type will remove all refrigerant related details recorded against this visit, are you sure?"
 
  @uswm @ukrb @usah
  Scenario: Edit Refrigerant Type Used - Update to incorrect refrigerant type
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a Remote System Appliance Type
      And the Refrigerant Gas Usage visit is viewed
      And Appliance Identification is not "Appliance not on list"
     When Refrigerant Type Used is updated to "Incorrect refrigerant type displayed"
      And a New Refrigerant Type Used is selected
     Then the Refrigerant Source and Refrigerant Installed sections are cleared
      And the Total gas used is set to zero
     
  @mcp
  Scenario: Delete Cylinder - Confirm
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When a Cylinder is deleted
      And the "Delete Refrigerant Source" popup alert is confirmed
     Then the Cylinder details are deleted
      
  @mcp
  Scenario: Delete Cylinder - Cancel
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When a Cylinder is deleted
      And the "Delete Refrigerant Source" popup alert is cancelled
     Then the Cylinder details are not deleted
     
  @mcp
  Scenario: Edit Cylinder - Full
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the Edit button is clicked for the first Cylinder
      And it is a "Full" cylinder
     Then Refrigerant Source "Gas in Cylinder (lbs)" is not requested      
     
  # Type of Cylinder [Disposable] should NOT be included in drop down list for some Gas Types - hence Cylinder Capacity is not available
  @mcp
  Scenario: Edit Cylinder - Partial
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the Edit button is clicked for the first Cylinder
      And it is a "Partial" cylinder
     Then Refrigerant Source "Gas in Cylinder (lbs)" is requested      

  @mcp
  Scenario: Edit Cylinder - Refrigerant Source updated
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the Edit button is clicked for the first Cylinder
      And the Refrigerant Source is updated
     Then the Refrigerant Installed section is cleared

  @mcp
  Scenario: Edit Cylinder - Save when Refrigerant Source section has not been completed
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the Edit button is clicked for the first Cylinder
      And the Refrigerant Source is updated
      And the "Save" button is clicked
     Then alerts are displayed for unanswered Refrigerant Source questions
     
  @mcp
  Scenario: Edit Cylinder - Returned To minimum length
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the Edit button is clicked for the first Cylinder
     Then Returned To has a minimum field length of "20"
     
  @mcp
  Scenario: Edit Cylinder - Cancel
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When a Cylinder is edited and the updates are cancelled
     Then the Cylinder has not been updated
     
  @mcp
  Scenario: Add Cylinder
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When a new Cylinder is added
     Then the Refrigerant Details now display the new cylinder details
     
  @mcp
  Scenario: Add Cylinder - Cancel
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When a new Cylinder is added then cancelled
     Then the Cylinder has not been added
     
  @mcp
  Scenario: Edit Refrigerant Installed - Fully Used
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the Edit button is clicked for the first Cylinder
      And Fully Used is "checked"
     Then Gas Installed is updated to match Gas Available
     
  @mcp
  Scenario: Edit Refrigerant Installed - Not Fully Used
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When the Edit button is clicked for the first Cylinder
      And updates are made resulting in unused gas within the cylinder
     Then the Surplus is displayed
     
  @mcp
  Scenario: Edit Refrigerant Installed - cylinder capacity
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a "Fully Used" partial cylinder
      And the Refrigerant Gas Usage visit is viewed
      And the Edit button is clicked for the "Fully Used" partial Cylinder
     When Gas in Cylinder is updated to be greater than a cylinders capacity
     Then the following error is displayed: "The gas in cylinder cannot be greater than cylinder capacity"
     
  @mcp
  Scenario: Edit Refrigerant Installed - Reduce gas in cylinder when Fully Used
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a "Fully Used" partial cylinder
      And the Refrigerant Gas Usage visit is viewed
      And the Edit button is clicked for the "Fully Used" partial Cylinder
     When Gas in Cylinder is updated to be less than Gas Installed
     Then Gas Installed is updated to equal the Gas in Cylinder
     
  @mcp
  Scenario: Edit Refrigerant Installed - Reduce gas in cylinder when not Fully Used
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with a "not Fully Used" partial cylinder
      And the Refrigerant Gas Usage visit is viewed
      And the Edit button is clicked for the "not Fully Used" partial Cylinder
     When Gas in Cylinder is updated to be less than Gas Installed
      And the "Save" button is clicked
     Then the following error is displayed: "The gas installed cannot be greater than the gas available"
     
  @mcp
  Scenario: Edit Refrigerant Installed - gas installed exceeds available
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
      And the Edit button is clicked for the first Cylinder
      And Fully Used is "unchecked"
     When Gas Installed is entered to exceed Gas Available
      And the "Save" button is clicked
     Then the following error is displayed: "The gas installed cannot be greater than the gas available"
     
  @mcp
  Scenario: Edit Refrigerant Installed - gas installed set to zero
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
      And the Edit button is clicked for the first Cylinder
      And Fully Used is "unchecked"
     When Gas Installed is set to "0"
      And the "Save" button is clicked
     Then the following error is displayed: "The gas installed cannot be less than 1"
     
  @uswm @ukrb @usah
  Scenario: Edit Refrigerant Installed - Total refrigerant gas installed exceeds asset capacity
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with installed gas at max capacity
      And the Refrigerant Gas Usage visit is viewed
     When a new Fully Used Cylinder is added
     Then the following error is displayed: "The total refrigerant gas installed exceeds asset capacity"
      And the following error is displayed: "Please edit and reduce the amount used or confirm asset capacity is incorrect"
      And FGAS question "Please enter appliance maximum charge" is mandatory   
      And FGAS question "Please provide notes to explain reason for changing appliance maximum charge" is mandatory   
      
  @mcp
  Scenario: Edit Leak Check and Repair Questions - With no Leak Site Check failures
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with 2019 FGAS regulated site visit "without" Leak Site Check failures
      And the Refrigerant Gas Usage visit is viewed
     Then the Leak Site Check "Edit" button is displayed
      And the Leak Site Check "Delete" button is displayed
      And all Leak Site Check questions can be updated
      
  @mcp
  Scenario: Edit Leak Check and Repair Questions - With Leak Site Check failures
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with multiple 2019 FGAS regulated site visits with Leak Site Check failures
      And the Refrigerant Gas Usage visit is viewed
     Then the Leak Site Check "Delete" button is not displayed
      And Leak Site Check questions "Primary Component, Primary Component Information, Sub-Component" can not be updated
      And Leak Site Check questions "Leak Site Status, Initial Verification Test, Follow Up Verification Test" can be updated
      
  @mcp
  Scenario: Edit Leak Site Check - Cancel
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When "Edit" Leak Site Check is cancelled
     Then the Leak Site Checks are not updated
     
  @mcp
  Scenario: Add Additional Leak Site Check
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When an Additional Leak Site Check is added
     Then the new Leak Site Check is added to the Refrigerant Leak Inspection & Repair section
   
  @mcp
  Scenario: Delete Leak Site Check - Cancel
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with multiple leak site checks, but NOT with multiple site visits requiring a return visit
      And the Refrigerant Gas Usage visit is viewed
      And "Delete" Leak Site Check is cancelled
     Then the Leak Site Checks are not updated
      And the Leak Site Check "is not" soft deleted from the database

  @mcp
  Scenario: Delete Leak Site Check
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with multiple leak site checks, but NOT with multiple site visits requiring a return visit
      And the Refrigerant Gas Usage visit is viewed
     When the last Leak Site Check is deleted
      And the "Save" button is clicked
     Then the Leak Site Check "is" soft deleted from the database
     
  @mcp
  Scenario: Add Additional Leak Site Check - Mandatory fields
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When Add Additional Leak Site Check is selected
      And the Add button is selected on the Leak Site Information popup
     Then warnings are displayed for unanswered mandatory Leak Site Information questions
     
  @mcp
  Scenario: Add Additional Leak Site Check - Cancel
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When "Add Additional" Leak Site Check is cancelled
     Then the Leak Site Checks are not updated
     
  @mcp
  Scenario: Edit Leak Check and Repair Questions - Updated to returning
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a completed 2019 FGAS regulated site visit with a Leak Site Check
      And the Refrigerant Gas Usage visit is viewed
     When a change is made to the leak site information that would cause the job status to be updated to returning
     Then a warning is displayed stating the user shall need to contact the Helpdesk to create a new job
      
  @mcp
  Scenario: Leak Site Check - Primary Component Information minimum length
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a "2019" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     When Add Additional Leak Site Check is selected
     Then Primary Component Information is requested with a minimum field length "20"  

  @mcp
  Scenario: Edit Leak Site Check - No leak found
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a 2019 FGAS regulated site visit with no leak found
      And the Refrigerant Gas Usage visit is viewed
     Then Adding Leak Site Details is optional
     
  @mcp
  Scenario: Edit Leak Site Check - No leak found - Add Leak Details
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with no leak found
      And the Refrigerant Gas Usage visit is viewed
     When Add Leak Details is selected
      And leak details are entered
     Then the new Leak Site Check is added to the Refrigerant Leak Inspection & Repair section
      And gas usage button "Add Additional Leak Site Details" is displayed
     

  # MCP-5906 MCP-7344 MCP-11913
  @mcp
  Scenario: Edit Leak Site Check - not No leak found 
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit with no leak found
      And the Refrigerant Gas Usage visit is viewed
     When Leak Check Result Type is updated
     Then Adding Leak Site Details is mandatory

  @bugWalmart @mcp @bugAdvocate
  Scenario: Save updated gas details - Multiple questions updated [bug: MCP-13433, MCP-20909]
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a completed 2019 FGAS regulated site visit with a Leak Site Check
      And the Refrigerant Gas Usage visit is viewed
      And all gas answers are updated
     Then the Refrigerant Gas Audit History has been updated
      And a timeline "Gas details updated" event is created in the database with the updated FGAS questions and answers
      
      
  # leak check not performed : option is not available for UKRB
  @uswm
  Scenario: Save updated gas details - No Leak Site Check performed
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
      And a search is run for a job with a 2019 FGAS regulated site visit without a Leak Check performed
      And the Refrigerant Gas Usage visit is viewed
     When the "Save" button is clicked
     Then the following toast message is displayed: "Gas details updated successfully"
      And user is returned to initial job reference entry screen      
