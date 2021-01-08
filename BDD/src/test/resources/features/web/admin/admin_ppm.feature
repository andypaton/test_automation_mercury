#These tests are tagged only for advocate until other clients get the new code change
@admin @admin_ppm
@usad
Feature: Admin - PPM

  @notsignedoff
  Scenario: The PPM Homepage is displayed correctly
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "PPMs" tile is selected
     Then the PPM screen is displayed as expected
      And the PPM Types menu is displayed by default
      
  @notsignedoff
  Scenario: The PPM Types page is displayed correctly
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "PPMs" tile is selected
     Then the PPM Types page is displayed as expected
     
  @notsignedoff
  Scenario: Add a new PPM Type page is displayed correctly
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "PPMs" tile is selected
      And the Add a new PPM Type button is pressed
     Then the Add a new PPM Type page is displayed as expected
     
  @notsignedoff @wip
  Scenario: Add a new PPM Type at Asset level
    Given an IT user has logged in
      And "Admin" is selected from the Mercury navigation menu
     When the "PPMs" tile is selected
      And the Add a new PPM Type at Asset Level
      And an Asset is added to the PPM Type
     Then the Add a new PPM Type page is displayed as expected