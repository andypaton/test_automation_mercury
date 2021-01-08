@portal @portal_fgas
@toggles @RefrigerantGas
@mcp @notsignedoff
@wip
Feature: Portal - Jobs - Update with Gas usage changing Maximum Charge

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"
  
  #@MCP-6633 @MCP-7378
  Scenario Outline: Complete the update job form with using more than the permitted maximum charge and verify - On Site Inventory
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                    | 
      | Appliance Type                    | Commercial Refrigeration - Self Contained | 
      | Has receiver level been recorded? | No                                        | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs)       | Fully Used | Gas Installed (lbs)       | 
      | Off Site Inventory | Partial                  | less than cylinder capacity | No         | less than gas in cylinder | 
      | On Site Inventory  | Partial                  | less than cylinder capacity | No         | less than gas in cylinder | 
      And the Gas "Maximum Charge" sub section questions are answered
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question          | Answer                             | 
      | Primary Component | Condensing Unit (factory assembly) | 
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Timeline Event Summary has been updated with "Resource returning"
      And an email is sent for "Appliance Information Is Missing"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-6633  @MCP-7378
  Scenario Outline: Complete the update job form with using more than the permitted maximum charge and verify - Off Site Inventory
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                    | 
      | Appliance Type                    | Commercial Refrigeration - Self Contained | 
      | Has receiver level been recorded? | No                                        | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs)       | Fully Used | Gas Installed (lbs)       | 
      | On Site Inventory  | Partial                  | less than cylinder capacity | No         | less than gas in cylinder | 
      | Off Site Inventory | Partial                  | less than cylinder capacity | No         | less than gas in cylinder | 
      And the Gas "Maximum Charge" sub section questions are answered
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question          | Answer                             | 
      | Primary Component | Condensing Unit (factory assembly) | 
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Timeline Event Summary has been updated with "Resource returning"
      And an email is sent for "Appliance Information Is Missing"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-6633 @MCP-7378
  Scenario Outline: Complete the update job form with using more than the permitted maximum charge and verify - Appliance not on list
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                    | 
      | Appliance Type                    | Commercial Refrigeration - Self Contained | 
      | Appliance Identification          | Appliance not on list                     | 
      | Has receiver level been recorded? | No                                        | 
      | Refrigerant Type Used             | R-134A                                    | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Type of Cylinder | Fully Used | Gas Installed (lbs) | 
      | On Site Inventory  | Partial                  | 125                   | Deposit          | No         | 122                 |
      | Off Site Inventory | Partial                  | 125                   | Deposit          | No         | 122                 | 
      And the Gas "Maximum Charge" sub section questions are answered 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question          | Answer                             | 
      | Primary Component | Condensing Unit (factory assembly) | 
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Timeline Event Summary has been updated with "Resource returning"
      And an email is sent for "Appliance Information Is Missing"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-6633 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Resource returning with Gas not in Plant with out Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated / ETA Provided" job without Gas used and without a Leak Check and a Refrigeration asset and is not required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                    | 
      | Appliance Type                    | Commercial Refrigeration - Self Contained | 
      | Has receiver level been recorded? | Yes                                       | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Fully Used | Gas Installed (lbs) | 
      | On Site Inventory  | Partial                  | Yes        | max                 | 
      | Off Site Inventory | Partial                  | Yes        | max                 | 
      And the Gas "Maximum Charge" sub section questions are answered
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question          | Answer                   | 
      | Leak Check Status | Leak check not performed | 
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
     Then the Timeline Event Summary has been updated with "Resource returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  