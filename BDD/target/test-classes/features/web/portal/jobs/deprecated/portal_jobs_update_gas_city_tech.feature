#@bug @MCP-9941 found on uat_ukrb
@portal @notsignedoff @portal_fgas
@toggles @RefrigerantGas
@mcp
@wip
Feature: Portal - Jobs - Update a gas job as a City tech

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"
  
  #@MCP-5610 
  @notsignedoff
  Scenario Outline: Refrigerant Gas Used during this visit - Yes
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit      
     Then the "FGAS Appliance" form is populated with the following answers
      | Appliance Type                            | Appliance Identification | Please provide appliance details | Has receiver level been recorded? | Quantity of Balls Floating | Provide Level Indicator % | 
      | Commercial Refrigeration - Remote System  | Visible                  | Visible                          | Yes                               | Visible                    | Visible                   | 
      | Commercial Refrigeration - Self Contained | Invisible                | Visible                          | No                                | Invisible                  | Invisible                 | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @grid @rework
  Scenario Outline: FGAS Appliance and Refrigerant Source sections completed
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a HVAC asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | 
      | Recovered Gas      | Full                     | 
      | Recovered Gas      | Partial                  | 
      | On Site Inventory  | Full                     | 
      | On Site Inventory  | Partial                  | 
      | Off Site Inventory | Full                     | 
      | Off Site Inventory | Partial                  | 
      | Truck Stock        | Full                     | 
      | Truck Stock        | Partial                  | 
      | New Purchase Order | invisible                | 
     Then the "Refrigerant Source" answers are displayed on update job page    
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6633 
  @notsignedoff
  Scenario Outline: Update a Non Remote Job to have used gas and with returning status on departure
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) |
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question                                                    | Answer                                            | 
      | Leak Check Result Type                                      | Leak site(s) located - One or more not accessible | 
      | Have you completed any other leak checks during this visit? | Yes                                               | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question          | Answer                             | 
      | Primary Component | Condensing Unit (factory assembly) | 
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the Timeline Event Summary has been updated with "Resource returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6633 
  # deprecated - looks to be same test as above?!
  @wip @deprecated
  @notsignedoff
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Resource returning with Gas not in Plant with out Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) |
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question          | Answer                   | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question         | Answer                     | 
      | Leak Site Status | Not repaired - Active leak |       
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the Timeline Event Summary has been updated with "Resource returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
      
      
  
