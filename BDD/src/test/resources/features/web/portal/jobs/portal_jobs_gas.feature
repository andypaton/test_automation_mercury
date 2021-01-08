@portal @portal_jobs @portal_jobs_fgas 
@geolocation
Feature: Portal - Jobs - Update FGAS

##################################################################################################################
# Business Team test: Contractor updating a job that forces a return

  # Note: any of the following question - answer combinations will force a return:
  #         leakCheckStatus :     Partial leak check performed / Leak check not performed / Preliminary leak check performed
  #         leakCheckResultType : Leak site(s) located - One or more not accessible / Inconclusive - Follow-up required
  #         leakSiteStatus :      Leak site isolated / Not repaired - Active leak
  #         gasLeakFollowUpTest : Failed Test
  
  @uswm @ukrb @usah
  Scenario: Contractor Admin updates FGAS job that forces a return
        #* using dataset "portal_jobs_fgas_001"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas "Refrigerant Source" sub section questions are answered
      And the Gas "Maximum Charge" sub section questions are answered 
      And the Leak Check questions are answered forcing the resource to return
      And the update Job form is completed with "Returning" status on departure
     Then the Job is updated with a "In Progress" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Timeline Event Summary has been updated with "Resource returning"

      
##################################################################################################################
# Business Team test: Gas Used - Yes 

     
##################################################################################################################
# Business Team test: Leak Check Status Complete = Yes AND Leak Check Result = Leak Site(s) Located - All Accessible OR No Leak Found
  @uswm @ukrb @usah
  Scenario: Leak Check Status Complete & Leak Site Located - All Accessible. Add Leak Site Details
        #* using dataset "portal_jobs_fgas_002"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                | 
      | Leak Check Status      | Complete                              | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
     When Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |     
     Then the Status on Departure can be set to "Awaiting Parts, Complete, Returning"
     
  @uswm @ukrb @usah
  Scenario: Leak Check Status Complete & Leak Site Located - All Accessible. Additional Leak Site Checks
        #* using dataset "portal_jobs_fgas_003"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                | 
      | Leak Check Status      | Complete                              | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
      And Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |
     When an additional Leak Site Check is added
     Then the following Leak Site Checks are listed "Leak Site Check #1, Additional Leak Site Check #2"
     
  @uswm @ukrb @usah
  Scenario: Leak Check Status Complete & Leak Site Located - All Accessible. Add Leak Site Details - Cancel
        #* using dataset "portal_jobs_fgas_004"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                | 
      | Leak Check Status      | Complete                              | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
     When Leak Site Details are entered then cancelled   
     Then no Leak Site Checks are listed

  @uswm @ukrb @usah
  @bugRainbow
  Scenario: Leak Check Status Complete & Leak Site Located - All Accessible. Job Completed [bug: MCP-19406]
        #* using dataset "portal_jobs_fgas_005"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas "Refrigerant Source" sub section questions are answered
      And the Gas "Maximum Charge" sub section questions are answered 
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                | 
      | Leak Check Status      | Complete                              | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
     When Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |     
      And the on Status on Departure section is completed with status "Complete"
      And the user updates the gas job
     Then the Job is updated with a "Fixed" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
      
    
##################################################################################################################
# Business Team test: Leak Check Status Complete = Yes AND Leak Check Result = Leak Site Located - One or more inaccessible OR Inconclusive - Follow Up Required (All leak site info = repaired/passed)
  
  @uswm @ukrb @usah
  Scenario: Leak Check Status Complete & Leak site(s) located - One or more not accessible. Add Leak Site Details
        #* using dataset "portal_jobs_fgas_006"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | Complete                                          | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
     When Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |     
     Then the Status on Departure has been set to "Returning"
      And FGAS question "Reason for Returning" is mandatory
      And FGAS question "ETA Date" is requested
      And FGAS question "ETA Window" is requested

  @uswm @ukrb @usah 
  Scenario: Leak Check Status Complete & Leak site(s) located - One or more not accessible. Add Leak Site Details - cancel
        #* using dataset "portal_jobs_fgas_007"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | Complete                                          | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible |
     When Leak Site Details are entered then cancelled   
     Then no Leak Site Checks are listed
     
  @uswm @ukrb @usah
  Scenario: Leak Check Status Complete & Leak site(s) located - One or more not accessible. Additional Leak Site Checks
        #* using dataset "portal_jobs_fgas_008"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | Complete                                          | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
      And Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |
     When an additional Leak Site Check is added
     Then the following Leak Site Checks are listed "Leak Site Check #1, Additional Leak Site Check #2"
     
##################################################################################################################
# Business Team test: Leak Check Status Complete = No AND Leak Check Result = Leak Site Located - All Accessible OR No Leak Found
  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario: Leak Check Status NOT Complete & Confirm repair has been leak checked
        #* using dataset "portal_jobs_fgas_009"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
     When the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer        | 
      | Leak Check Status      | Not Complete  | 
     Then FGAS question "Leak Check Method" is mandatory
      And FGAS question "Leak Check Result Type" is mandatory
     
  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario: Leak Check Status NOT Complete & Leak site(s) located - All accessible. Add Leak Site Details - Returning
        #* using dataset "portal_jobs_fgas_010"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                | 
      | Leak Check Status      | Not Complete                          | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
     When Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |     
      And the Status on Departure is set to "Returning"
     Then FGAS question "Reason for Returning" is mandatory
      And FGAS question "ETA Date" is requested
      And FGAS question "ETA Window" is requested   
       
  @uswm @ukrb @usah
  Scenario: Leak Check Status Complete & Leak site(s) located - All accessible. Add Leak Site Details - Awaiting Parts
        #* using dataset "portal_jobs_fgas_011"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer   | 
      | Leak Check Status      | Complete | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
     When Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |     
      And the Status on Departure is set to "Awaiting Parts"
     Then FGAS question "ETA Date" is requested
      And FGAS question "ETA Window" is requested         
     
  @uswm @ukrb @usah
  Scenario: Leak Check Status Complete & Leak site(s) located - All accessible. Add Leak Site Details - Complete
        #* using dataset "portal_jobs_fgas_012"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer   | 
      | Leak Check Status      | Complete | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
     When Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |     
      And the Status on Departure is set to "Complete"
     Then FGAS question "Asset Condition" is mandatory
      And FGAS question "Root cause category" is mandatory
      And FGAS question "Root cause" is mandatory
      And FGAS question "Please describe works carried out and any parts fitted" is mandatory    
      
  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario: Leak Check Status NOT Complete & Leak site(s) located - All accessible. Add Leak Site Details - cancel
        #* using dataset "portal_jobs_fgas_013"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                | 
      | Leak Check Status      | Not Complete                          | 
      | Leak Check Result Type | Leak site(s) located - All accessible |
     When Leak Site Details are entered then cancelled   
     Then no Leak Site Checks are listed
     
  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario: Leak Check Status NOT Complete & Leak site(s) located - All accessible. Additional Leak Site Checks
        #* using dataset "portal_jobs_fgas_014"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                | 
      | Leak Check Status      | Not Complete                          | 
      | Leak Check Result Type | Leak site(s) located - All accessible | 
      And Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |
     When an additional Leak Site Check is added
     Then the following Leak Site Checks are listed "Leak Site Check #1, Additional Leak Site Check #2"
     
##################################################################################################################
# Business Team test: Leak Check Status Complete = No AND Leak Check Result = Leak Site Located - One or more inaccessible OR Inconclusive - Follow Up Required
  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario: Leak Check Status NOT Complete & Leak site(s) located - One or more not accessible. Add Leak Site Details
        #* using dataset "portal_jobs_fgas_015"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | Not Complete                                      | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
     When Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |     
     Then the Status on Departure has been set to "Returning"
      And FGAS question "Reason for Returning" is mandatory
      And FGAS question "ETA Date" is requested
      And FGAS question "ETA Window" is requested   
      
  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario: Leak Check Status NOT Complete & Leak site(s) located - One or more not accessible. Add Leak Site Details - cancel
        #* using dataset "portal_jobs_fgas_016"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | Not Complete                                      | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible |
     When Leak Site Details are entered then cancelled   
     Then no Leak Site Checks are listed
     
  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario: Leak Check Status NOT Complete & Leak site(s) located - One or more not accessible. Additional Leak Site Checks
        #* using dataset "portal_jobs_fgas_017"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
      And the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | Not Complete                                      | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
      And Leak Site Details are added with
      | Question                    | Answer   |
      | Leak Site Status            | Repaired |
      | Initial Verification Test   | Passed   |
      | Follow Up Verification Test | Passed   |
     When an additional Leak Site Check is added
     Then the following Leak Site Checks are listed "Leak Site Check #1, Additional Leak Site Check #2"
           
##################################################################################################################
# Business Team test: Leak Site Check Modal = System allows user to select Awaiting Parts/Complete/Returning
 
##################################################################################################################
# Business Team test: Leak Site Check Modal = System forces job to status of Returning
      
##################################################################################################################
  @uswm @ukrb @usah
  Scenario Outline: Leak Check Status Complete & "<LEAK CHECK RESULT TYPE>"
        #* using dataset "portal_jobs_fgas_018"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
     When the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                    | 
      | Leak Check Status      | Complete                  | 
      | Leak Check Result Type | <LEAK CHECK RESULT TYPE>  | 
     Then Add Leak Site Details is "<ADD LEAK SITE DETAILS>"
    Examples:
     | LEAK CHECK RESULT TYPE                            | ADD LEAK SITE DETAILS |
     | Leak site(s) located - All accessible             | mandatory             |
     | Leak site(s) located - One or more not accessible | mandatory             |
     | Inconclusive - Follow-up required                 | mandatory             |
     | No leak found                                     | optional              |
     

  # note : for UKRB Leak Check Status can only be complete!
  @uswm
  Scenario Outline: Leak Check Status NOT Complete & "<LEAK CHECK RESULT TYPE>"
        #* using dataset "portal_jobs_fgas_019"
    Given a "Contractor" with a job for an asset that uses gas
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered
     When the Gas Leak Check Questions sub section questions are answered with
      | Question               | Answer                               | 
      | Leak Check Status      | Not Complete                         | 
      | Leak Check Result Type | <LEAK CHECK RESULT TYPE>             | 
     Then Add Leak Site Details is "<ADD LEAK SITE DETAILS>"
    Examples:
     | LEAK CHECK RESULT TYPE                            | ADD LEAK SITE DETAILS |
     | No leak found                                     | optional              |
     | Leak site(s) located - All accessible             | mandatory             |
     | Leak site(s) located - One or more not accessible | mandatory             |
     | Inconclusive - Follow-up required                 | mandatory             |

  @uswm @ukrb @usah
  Scenario: Follow Up Visit - Gas not used
        #* using dataset "portal_jobs_fgas_020"
    Given a "Contractor" with a job for an asset that uses gas and a forced returning status
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
     Then the following information message is displayed: "A leak test is required for this job because previously a leak test was carried out and failed, or refrigerant gas was previously added and no leak test was performed. Please indicate if a leak test was carried out during the current visit" 

  @uswm @ukrb @usah
  Scenario: Follow Up Visit - Gas used
        #* using dataset "portal_jobs_fgas_021"
    Given a "Contractor" with a job for an asset that uses gas and a forced returning status
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
     Then Appliance Details are read only
      And Leak Inspection and Repair questions previously entered are displayed      
      And a Follow Up button is displayed for the previous Leak Site Check
      And FGAS "Refrigerant Details" are mandatory
      And FGAS "Reason for Returning" are mandatory
      And FGAS "ETA Date" are mandatory
     
  @uswm @ukrb @usah
  Scenario: Follow Up Visit - previously entered Leak Site Information
        #* using dataset "portal_jobs_fgas_022"
    Given a "Contractor" with a job for an asset that uses gas and a forced returning status
      And is on site and has started the job
      And the user logs in    
      And the user views the "reactive" "Open" job     
      And the Update Job form is complete with basic information
      And Refrigerant gas is used during this visit
     When the Leak Site Check 'Follow Up' button is clicked
     Then Leak Inspection and Repair questions previously entered are displayed
      And Leak Site Check questions "Primary Component, Primary Component Information, Sub-Component" can not be updated
      And Leak Site Check questions "Leak Site Status, Initial Verification Test, Follow Up Verification Test" can be updated
      