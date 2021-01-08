@portal @notsignedoff @portal_fgas
@toggles @RefrigerantGas
@mcp
@wip
Feature: Portal - Jobs - Update with Gas usage - Negative

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"
  
  
  #@MCP-5610 
  Scenario Outline: Partially complete the update job form with lbs in cylinder greater than capacity and verify the error messages
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      | Gas in Cylinder (lbs)    | Greater       | 
      | Fully Used               | UnAnswered    | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question              | errorMessage                                                    | 
      | Gas in Cylinder (lbs) | The gas in cylinder cannot be greater than cylinder capacity of | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610
  Scenario Outline: Partially complete the update job form with Fully Used omitted and verify the error messages
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      | Fully Used               | UnAnswered    | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question   | errorMessage                                              | 
      | Fully Used | Please specify if the full amount of refrigerant was used | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 

  
  #@MCP-5610 
  Scenario Outline: Partially complete the update job form with Gas Installed omitted and verify the error messages
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      | Gas in Cylinder (lbs)    | 1             | 
      | Fully Used               | No            | 
      | Gas Installed (lbs)      | UnAnswered    | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question            | errorMessage                  | 
      | Gas Installed (lbs) | The gas installed is required | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Partially complete the update job form and verify the error messages for cylinder serial number and lbs in cylinder
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      | Cylinder Serial No       | UnAnswered    | 
      | Gas in Cylinder (lbs)    | UnAnswered    | 
      | Fully Used               | UnAnswered    | 
      | Gas Installed (lbs)      | UnAnswered    | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question              | errorMessage                       | 
      | Cylinder Serial No    | Cylinder serial number is required | 
      | Gas in Cylinder (lbs) | Gas in Cylinder is required        | 
  #      | Fully Used            | Please specify if the full amount of refrigerant was used | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Partially complete the update job form and verify the error messages for Refrigerant Source Location, Cylinder Serial No and Gas in Cylinder
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                    | Answer        | 
      | Refrigerant Source          | Recovered Gas | 
      | Refrigerant Source Location | UnAnswered    | 
      | Full or Partial Cylinder    | Partial       | 
      | Cylinder Serial No          | UnAnswered    | 
      | Gas in Cylinder (lbs)       | UnAnswered    | 
      | Fully Used                  | UnAnswered    | 
      | Gas Installed (lbs)         | UnAnswered    | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question                    | errorMessage                              | 
      | Refrigerant Source Location | A refrigerant source location is required | 
      | Cylinder Serial No          | Cylinder serial number is required        | 
      | Gas in Cylinder (lbs)       | Gas in Cylinder is required               | 
  #      | Fully Used                  | Please specify if the full amount of refrigerant was used | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 @MCP-6848
  Scenario Outline: Partially complete the update job form and verify the error messages for Off Site Location, Cylinder Serial No and Gas in Cylinder
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                    | Answer             | 
      | Refrigerant Source          | Off Site Inventory | 
      | Refrigerant Source Location | UnAnswered         | 
      | Full or Partial Cylinder    | Partial            | 
      | Cylinder Serial No          | UnAnswered         | 
      | Gas in Cylinder (lbs)       | UnAnswered         | 
      | Fully Used                  | UnAnswered         | 
      | Gas Installed (lbs)         | UnAnswered         | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question                    | errorMessage                              | 
      | Refrigerant Source Location | A refrigerant source location is required | 
      | Cylinder Serial No          | Cylinder serial number is required        | 
      | Gas in Cylinder (lbs)       | Gas in Cylinder is required               | 
  #      | Fully Used                  | Please specify if the full amount of refrigerant was used | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Partially complete the update job form and verify the error messages lbs in Cylinder
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                    | Answer             | 
      | Refrigerant Source          | Off Site Inventory | 
      | Refrigerant Source Location | UnAnswered         | 
      | Full or Partial Cylinder    | Partial            | 
      | Cylinder Serial No          | UnAnswered         | 
      | Gas in Cylinder (lbs)       | UnAnswered         | 
      | Fully Used                  | UnAnswered         | 
      | Gas Installed (lbs)         | UnAnswered         | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question                    | errorMessage                              | 
      | Refrigerant Source Location | A refrigerant source location is required | 
      | Cylinder Serial No          | Cylinder serial number is required        | 
      | Gas in Cylinder (lbs)       | Gas in Cylinder is required               | 
  #      | Fully Used                  | Please specify if the full amount of refrigerant was used | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 @MCP-5971
  Scenario Outline: Partially complete the update job form with a negative lbs in cylinder greater and verify the error messages
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      | Gas in Cylinder (lbs)    | -100          | 
      | Fully Used               | UnAnswered    | 
      | Gas Installed (lbs)      | UnAnswered    | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question              | errorMessage                               | 
      | Gas in Cylinder (lbs) | The gas in cylinder must be greater than 0 | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 @MCP-5971
  Scenario Outline: Partially complete the update job form with refrigerant source questions unanswered
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      | Gas in Cylinder (lbs)    | 0             | 
      | Fully Used               | UnAnswered    | 
      | Gas Installed (lbs)      | UnAnswered    | 
      And the "Add" button is clicked
     Then the following error messages are displayed:    
      | question              | errorMessage                               | 
      | Gas in Cylinder (lbs) | The gas in cylinder must be greater than 0 | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  Scenario Outline: Partially complete the update non gas job form with unanswered leak check questions
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the user updates the job
     Then the following error messages are displayed:  
      | question            | errorMessage                         | 
      | Appliance Type      | Please select an appliance type      | 
      | Leak Check Status   | Please select the Leak Check Status  | 
      | Status on Departure | Please select a status from the list | 
      And the following alert is displayed: "Please specify if receiver level has been recorded"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  Scenario Outline: Partially complete the update job form with appliance questions unanswered
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer     | 
      | Appliance Type                    | UnAnswered | 
      | Appliance Identification          | UnAnswered | 
      | Please provide appliance details  | UnAnswered | 
      | Has receiver level been recorded? | Yes        | 
      | Quantity of Balls Floating        | UnAnswered | 
      | Provide Level Indicator %         | UnAnswered | 
      | Refrigerant Type Used             | UnAnswered | 
      And the user updates the job
     Then the following alert is displayed: "Please select an appliance type"
      And the following alert is displayed: "Please enter quantity of floating balls OR level indicator %"
      And the following alert is displayed: "Please select a gas type"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  
