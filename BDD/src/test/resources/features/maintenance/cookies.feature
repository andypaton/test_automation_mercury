@maintenance @envchecks 
Feature: test MERCURY cookies for ApiHelper

  given setup steps have run
  
  
  @MTA-154
  Scenario: successful call to API Helper
    When resource details are requested via the API
    Then resource details are returned