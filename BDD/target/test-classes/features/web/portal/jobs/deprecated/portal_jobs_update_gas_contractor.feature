#@MCP-5098 @MCP-5093 
@portal @portal_fgas
@toggles @RefrigerantGas
@mcp @geolocation
@wip
Feature: Portal - Jobs - Update a gas job as a contractor

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"

  
  #@MCP-5702 @MCP-6839
  @notsignedoff
  Scenario Outline: Status on Departure set to Awaiting Parts
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with out Asset
      And is within GEO radius
      And the user logs in    
      And the "Stop Work" button is clicked  
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Fully Used | Destination        | 
      | Recovered Gas      | Partial                  | No         | Returned to Source | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Awaiting Parts"
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Timeline Event Summary has been updated with "Resource Awaiting Parts"
    Examples: 
      | profile               | jobtype  | arrival | departure | status      | gasQuestionSet        | 
      | Contractor Technician | reactive | No      | Yes       | In Progress | Not in Plant, no Rack | 
  
  
  #@MCP-5702 @MCP-6839
  Scenario Outline: Status on Departure set to Complete
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with out Asset
      And is within GEO radius
      And the user logs in    
      And the "Stop Work" button is clicked  
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                           | 
      | Appliance Type                    | Comfort Cooling - Self Contained | 
      | Has receiver level been recorded? | Yes    | 
      | Refrigerant Type Used             | R-404A | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Fully Used | 
      | Vendor Supplied    | Partial                  | No         |
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Complete"
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Timeline Event Summary has been updated with "Complete"
    Examples: 
      | profile               | jobtype  | arrival | departure | status  | gasQuestionSet        | 
      | Contractor Technician | reactive | No      | Yes       | Fixed   | Not in Plant, no Rack | 
  
  
  #@MCP-5702 @MCP-6839
  Scenario Outline: Status on Departure set to Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with out Asset
      And is within GEO radius
      And the user logs in    
      And the "Stop Work" button is clicked  
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question       | Answer                                   | 
      | Appliance Type | Commercial Refrigeration - Remote System | 
      | Has receiver level been recorded? | Yes | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Fully Used | 
      | Recovered Gas      | Partial                  | No         |
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Returning"
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Timeline Event Summary has been updated with "Resource returning"
    Examples: 
      | profile               | jobtype  | arrival | departure | status      | gasQuestionSet        | 
      | Contractor Technician | reactive | No      | Yes       | In Progress | Not in Plant, no Rack | 
  