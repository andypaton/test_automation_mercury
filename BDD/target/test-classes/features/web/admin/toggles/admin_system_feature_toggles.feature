#Removed environment tags as we don't change the toggles anymore
@deprecated
@toggles @admin @admin_toggles
Feature: Admin - System Feature Toggles

  #@MCP-5541
  Scenario: System Feature Toggle - ON
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the following System Feature Toggles are set to "ON":
      | Resource Quote Questions                    | 
      | Additional Close Down Questions             | 
      | Auto Assign                                 | 
      | Quote Job                                   | 
      | Refrigerant Gas                             | 
      | Snow Management                             | 
      | Fault Priority Deferral                     | 
      | SICJobs                                     | 
      | OBWTCP                                      | 
      | Recall Resources                            | 
      | Portal Daily Actions Emails                 | 
      | Asset Not Selected Warning                  | 
      | Funding Request Available For City Resource | 
      | Landlord                                    | 
      | Job Deferrals                               | 
      | Auto Approve Contractor Funding Requests    | 
      | Store Portal                                |
      | Invoicing                                   | 
      And the "Save" button is clicked
     Then the toggled features are stored as "active" in the database
     
  Scenario: System Feature Toggle - ON
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the following System Feature Toggles are set to "ON":
      | New Routing Rules                           | 
      And the "Save" button is clicked
     Then the toggled features are stored as "active" in the database
         
         
  #@MCP-5541
  Scenario: System Feature Toggle - OFF
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the following System Feature Toggles are set to "OFF":
      | Resource Quote Questions                    | 
      | Additional Close Down Questions             | 
      | Auto Assign                                 | 
      | Quote Job                                   | 
      | Refrigerant Gas                             | 
      | Snow Management                             | 
      | Fault Priority Deferral                     | 
      | SICJobs                                     | 
      | OBWTCP                                      | 
      | Recall Resources                            | 
      | Portal Daily Actions Emails                 | 
      | Asset Not Selected Warning                  | 
      | Funding Request Available For City Resource | 
      | Job Deferrals                               | 
      | Auto Approve Contractor Funding Requests    |
      | Store Portal                                | 
      | Invoicing                                   |
      And the toggled features have their Sub Features automatically set to OFF
      And the "Save" button is clicked
     Then the toggled features are stored as "not active" in the database
      And the toggled sub features are stored as "not active" in the database 
               
  Scenario: System Feature Toggle - OFF
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the following System Feature Toggles are set to "OFF":
      | New Routing Rules                           | 
      And the toggled features have their Sub Features automatically set to OFF
      And the "Save" button is clicked
     Then the toggled features are stored as "not active" in the database
      And the toggled sub features are stored as "not active" in the database 
      
  #@MCP-5541
  Scenario: System Feature Toggle - OFF - Landlord
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the following System Feature Toggles are set to "OFF":
      | Landlord                                    |
      And the toggled features have their Sub Features automatically set to OFF
      And the "Save" button is clicked
     Then the "Please deactivate the following resources first:" popup alert is displayed      
      
  #@MCP-5541
  Scenario: Sub System Feature Toggle - ON
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the following System Sub Feature Toggles are set to "ON":
      | High Risk Works |
      | Travel Time |
      | Overtime |
      | Leak Tested |
      | Auto Assign Contractors |
      | Asset Driven |
      | Budget Driven |
      | Quote Doc Optional For Technicians |
      | Quote Doc Optional For Contractors |
      | Leak Location Question |
      | Leakage Code Question |
      | Fault Priority Deferrals Include Contractors |
      | Potential Landlord - First Look process |
      | Auto-convert Immediate Callout OOH |
      | Landlord Email Updates |
      | Show Job Deferral Questions   | 
      | Awaiting Feedback Section |
      | Dashboard Section |
      | On Site Section |
      | Ppm Jobs |
      | Show Job Priorities |
      | Show Job Statuses |
      | Show Non Priority Jobs |
      And the "Save" button is clicked
     Then the toggled sub features are stored as "active" in the database
      
  #@MCP-5541
  Scenario: Sub System Feature Toggle - OFF
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
     When the following System Sub Feature Toggles are set to "OFF":
      | High Risk Works |
      | Travel Time |
      | Overtime |
      | Leak Tested |
      | Auto Assign Contractors |
      | Asset Driven |
      | Budget Driven |
      | Quote Doc Optional For Technicians |
      | Quote Doc Optional For Contractors |
      | Leak Location Question |
      | Leakage Code Question |
      | Fault Priority Deferrals Include Contractors |
      | Potential Landlord - First Look process |
      | Auto-convert Immediate Callout OOH |
      | Landlord Email Updates |
      | Show Job Deferral Questions   |
      | Awaiting Feedback Section |
      | Dashboard Section |
      | On Site Section |
      | Ppm Jobs |
      | Show Job Priorities |
      | Show Job Statuses |
      | Show Non Priority Jobs | 
      And the "Save" button is clicked
     Then the toggled sub features are stored as "not active" in the database

  #@consolidatedInvoicing @MP7-305 
  Scenario: System Sub Feature Toggle - ON - Invoicing
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
      And the following System Feature Toggles are set to "ON":
      | Invoicing                              |
     When the following System Sub Feature Toggles are set to "ON":
      | Consolidated Invoicing                 |
      | Invoicing Line Fulfilled               |
      | Legal Entity Admin                     |
      | Legal Entity Assignment and Validation |
      | Mandatory Invoice General              |
      And the "Save" button is clicked
     Then the toggled sub features are stored as "active" in the database
     
  #@consolidatedInvoicing @MP7-305 
  Scenario: System Sub Feature Toggle - OFF - Invoicing
    Given a user with "Mercury_Admin_System_Feature_Toggle" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "System Feature Toggle" tile is selected
      And the following System Feature Toggles are set to "ON":
      | Invoicing                              |
     When the following System Sub Feature Toggles are set to "OFF":
      | Consolidated Invoicing                 |
      | Invoicing Line Fulfilled               |
      | Legal Entity Admin                     |
      | Legal Entity Assignment and Validation |
      | Mandatory Invoice General              |
      And the "Save" button is clicked
     Then the toggled sub features are stored as "inactive" in the database
