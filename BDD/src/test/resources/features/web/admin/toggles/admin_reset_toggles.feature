#Removed environment tags as we don't change the toggles anymore
@deprecated
@toggles @reset_toggles
Feature: Admin - Reset System Feature and Sub-Feature toggles
     
  Scenario: Reset toggles to default values
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the toggles are set to the environment defaults
      And the "Save" button is clicked
     Then the default toggle values are stored in the database
       