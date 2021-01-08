#@fgasold @usv1
@portal @portal_fgas
@wip @deprecated
@mcp
Feature: Portal - Jobs - Update with Gas usage 

  Background: System Feature Toggles are set for UK Gas Regulations
    Given the system feature toggle "RefrigerantGas" is "enabled"
      And the system sub feature toggle "US Regulations" is "disabled"
      And the system sub feature toggle "UK Regulations" is "enabled"
 
  #@MTA-632
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Returning with gas usage and added to the rack
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
     When the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the job is not the landlords responsibility
      And the Travel Time is entered
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And Refrigerant is selected
      And Refrigerant was used and was added to the rack
      And the leak test repair has been checked
      And the "Returning" Status on Departure is entered
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | 
      | City Resource | reactive | No      | Yes       | In Progress | 
  
  #@MTA-632
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Returning with gas usage and not added to the rack
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a HVAC asset and is not required to return
     When the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the job is not the landlords responsibility
      And the Travel Time is entered
      And the Operational On Arrival is "<arrival>"
      And the Operational On Departure is "<departure>"
      And a Work Start date and time is entered
      And the Time Spent is entered
      And Refrigerant is selected
      And Refrigerant was used and was not added to the rack
      And the leak test repair has been checked
      And the "Returning" Status on Departure is entered
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | 
      | City Resource | reactive | No      | Yes       | In Progress | 
  
  #@MTA-651
  @notsignedoff
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with gas usage and not added to the rack
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant is selected
      And Refrigerant was used and was added to the rack
      And the leak test repair has been checked
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | 
      | City Resource | reactive | No      | Yes       | Fixed  | 
  
  #@MTA-651 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with gas usage and added to the rack and omit all answers
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a HVAC asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant is selected
      And Gas was added to a rack
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the following error messages are displayed:    
      | errorMessage                                           | question                 | 
      | Please select a rack from the list                     | Select a rack            | 
      | Please enter a reason for selecting this rack          | Notes                    | 
      | Please select a gas type from the list                 | Gas Type                 | 
      | Please select a gas leakage code from the list         | Gas Leakage Code         | 
      | Please select a gas leakage check method from the list | Gas Leakage Check Method | 
      | Please select a gas Primary Component from the list    | Gas Primary Component    | 
      | Please select an action from the list                  | Action                   | 
      | Please select a fault code from the list               | Fault Code               | 
      | Please enter a bottle number                           | Bottle Number            | 
      | Please enter a quantity                                | Quantity (Lbs)           | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | 
      | City Resource | reactive | No      | Yes       | Fixed  | 
  
  #@MTA-651 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with gas usage and not added to the rack and omit all answers
    Given a portal user with profile "<profile>" 
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a HVAC asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant is selected
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the following error messages are displayed:    
      | errorMessage                                           | question                 | 
      | Please select an asset from the list                   | Select an Asset          | 
      | Please select a gas type from the list                 | Gas Type                 | 
      | Please select a gas leakage code from the list         | Gas Leakage Code         | 
      | Please select a gas leakage check method from the list | Gas Leakage Check Method | 
      | Please select a gas Primary Component from the list    | Gas Primary Component    | 
      | Please select an action from the list                  | Action                   | 
      | Please select a fault code from the list               | Fault Code               | 
      | Please enter a bottle number                           | Bottle Number            | 
      | Please enter a quantity                                | Quantity (Lbs)           | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | 
      | City Resource | reactive | No      | Yes       | Fixed  | 
  
  #@MTA-651 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with gas usage and not added to the rack and omit all answers after selecting asset not in rack
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a HVAC asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant is selected
      And a "Asset not in this list" asset is selected
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the following error messages are displayed:    
      | errorMessage                                           | question                                           | 
      | Please enter details related to the asset              | Please enter Asset details (minimum 20 characters) | 
      | Please select a gas type from the list                 | Gas Type                                           | 
      | Please select a gas leakage code from the list         | Gas Leakage Code                                   | 
      | Please select a gas leakage check method from the list | Gas Leakage Check Method                           | 
      | Please select a gas Primary Component from the list    | Gas Primary Component                              | 
      | Please select an action from the list                  | Action                                             | 
      | Please select a fault code from the list               | Fault Code                                         | 
      | Please enter a bottle number                           | Bottle Number                                      | 
      | Please enter a quantity                                | Quantity (Lbs)                                     | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | 
      | City Resource | reactive | No      | Yes       | Fixed  | 
  
  #@MTA-651
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with gas usage and not added to the rack and omit all answers
    Given a portal user with profile "<profile>"
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant is selected
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the following error messages are displayed:    
      | errorMessage                                           | question                 | 
      | Please select a gas type from the list                 | Gas Type                 | 
      | Please select a gas leakage code from the list         | Gas Leakage Code         | 
      | Please select a gas leakage check method from the list | Gas Leakage Check Method | 
      | Please select a gas Primary Component from the list    | Gas Primary Component    | 
      | Please select an action from the list                  | Action                   | 
      | Please select a fault code from the list               | Fault Code               | 
      | Please enter a bottle number                           | Bottle Number            | 
      | Please enter a quantity                                | Quantity (Lbs)           | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | 
      | City Resource | reactive | No      | Yes       | Fixed  | 
  
  # save typing this again      
  #      | Please select a rack from the list                     | Select a rack                 |
  #      | Please enter a reason for selecting this rack          | Notes                         |
  
