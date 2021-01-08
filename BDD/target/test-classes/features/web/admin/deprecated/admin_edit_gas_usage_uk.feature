#@MCP-5479
@admin
@deprecated @wip
Feature: Admin - Refrigerant Gas Usage - Edit UK Regulations
     
  # set feature toggles until MCP-8464 fixed
  # @bugWalmart @MCP-8464
  @notsignedoff @MCP-5905 @MCP-6064
  Scenario: Refrigerant Gas Usage - View - UK regulations
      Given the system feature toggle "RefrigerantGas" is "enabled"
      And the system sub feature toggle "LeakLocationQuestion" is "enabled"
      And the system sub feature toggle "LeakageCodeQuestion" is "enabled"
      And a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a "UK" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
     Then the job details are displayed
      And UK Regulation questions completed by the engineer are displayed

  # set feature toggles until MCP-8464 fixed
  # @bugWalmart @MCP-8464
  # @MCP-5542 @MCP-5972
  @notsignedoff
  @intermittentBug @MCP-8819
  Scenario: Refrigerant Gas Usage - Edit - UK regulations
      Given the system feature toggle "RefrigerantGas" is "enabled"
      And the system sub feature toggle "LeakLocationQuestion" is "enabled"
      And the system sub feature toggle "LeakageCodeQuestion" is "enabled"
      And a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a "UK" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
      And the following gas usage answers are updated:
      | Gas Type             | 
      | Leak Location        | 
      | Leak check method    | 
      | Action               | 
      | Fault Code           | 
      | Bottle/Serial Number | 
      And the "Save" button is clicked
     Then the "Site visit details have been updated" popup alert is confirmed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Refrigerant Gas Audit History has been updated
      
      
  # merge with above scenario when bug fixed
  # @MCP-5542
  @notsignedoff
  @bugWalmart @MCP-5666
  Scenario: Refrigerant Gas Usage - Edit - UK regulations
      Given the system feature toggle "RefrigerantGas" is "enabled"
      And the system sub feature toggle "LeakLocationQuestion" is "enabled"
      And the system sub feature toggle "LeakageCodeQuestion" is "enabled"
      And a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a "UK" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
      And the following gas usage answers are updated:
      | Leakage Code |
      And the "Save" button is clicked
     Then the "Site visit details have been updated" popup alert is confirmed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Refrigerant Gas Audit History has been updated
  
  
  # @MCP-5542 @MCP-6974
  Scenario: Refrigerant Gas Usage - Edit - UK regulations - amount used below maximum charge
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a "UK" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
      And the Amount Used is updated to "below" the maximum charge allowed
      And the "Save" button is clicked
      And the "Site visit details have been updated" popup alert is confirmed
     Then the Site Visit Gas Usage and Leak Checks have been recorded
      And the Refrigerant Gas Audit History has been updated
  
  
  # @MCP-5542 @MCP-6397 @MCP-7052
  Scenario: Refrigerant Gas Usage - Edit - UK regulations - amount used exceeds maximum charge
    Given a user with "Mercury_Admin_Refrigeration" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Refrigerant Gas Usage" tile is selected
     When a search is run for a job with a "UK" FGAS regulated site visit
      And the Refrigerant Gas Usage visit is viewed
      And the Amount Used is updated to "exceed" the maximum charge allowed
      And the "Save" button is clicked
     Then the "Total gas quantity exceeds maximum refrigerant charge allowed, please amend" popup alert is displayed
      
