@portal @portal_jobs @portal_jobs_log
Feature: Portal - Jobs - Log
  
  @uswm @ukrb 
  Scenario: RFM logs job for tagged asset without a serial number
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority "tagged" asset without serial number 
      And an "existing" contact is added for the site
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
      
  @uswm @ukrb @smoke1
  Scenario: RFM logs job for tagged asset with random data
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with tagged asset
      And an "new" contact is added for the site
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job is sitting in "Allocated" status or "Logged" status
     
  @uswm @ukrb
  Scenario: RFM logs job without asset and OPEX quote requested with random data
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed without tagged asset  
      And an "OPEX" quote is requested
      And a random Quote Priority is selected
      And a "new" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job is sitting in "Awaiting Quote Request Review" status or "ITQ Awaiting Acceptance" status

  @uswm @ukrb
  Scenario: RFM logs job for non tagged asset without a serial number
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority "<asset>" asset without serial number 
      And a "existing" contact is added for the site
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
  
  @uswm @ukrb
  Scenario Outline: RFM logs job for "<asset>" asset with a serial number
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority "<asset>" asset with serial number
      And an "existing" contact is added for the site
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
    Examples: 
      | asset      |
      | tagged     |
      | non tagged |
    
  @uswm @ukrb
  Scenario: RFM logs job without asset
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority and no asset
      And an "existing" contact is added for the site
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
   
  @uswm @ukrb
  Scenario Outline: RFM logs job without asset and "<quote>" quote requested
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority and no asset    
      And a "P2" quote is requested
      And a random Quote Priority is selected
      And a "new" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status or "ITQ Awaiting Acceptance" status
    Examples: 
      | quote |
      | CAPEX |
      | OPEX  |
          
  @uswm @ukrb 
  Scenario: RFM logs job without asset and OPEX quote requested
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority and no asset    
      And an "OPEX" quote is requested
      And a random Quote Priority is selected
      And a "new" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status or "ITQ Awaiting Acceptance" status
  

  # If 'Invitation to Quote' is selected by the RFM when setting funding route then status will be 'ITQ Awaiting Acceptance', otherwise 'Awaiting Quote Request Review'
  # The RFM would need to log a quote job for a site they look after to have the ability to select resources 
  #  - they have the ability to raise quote jobs against any site but then can't assign resources for all 
  @uswm @ukrb
  Scenario Outline: RFM logs job without asset and non urgent quote requested for "<fundingRoute>" funding route
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority and no asset    
      And a non-urgent quote with a "<fundingRoute>" funding route is requested
      And a "existing" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status or "ITQ Awaiting Acceptance" status
    Examples: 
      | fundingRoute | 
      | OPEX         | 
      | CAPEX        | 
  
  @uswm @ukrb
  Scenario Outline: RFM logs job without asset and urgent quote requested for "<fundingRoute>" funding route
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P2" priority and no asset    
      And a urgent quote with a "<fundingRoute>" funding route is requested
      And a "existing" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status or "ITQ Awaiting Acceptance" status
    Examples: 
      | fundingRoute |
      | OPEX         |
      | CAPEX        |
   
  @uswm @ukrb
  Scenario: RFM cancels logging a new job
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority and no asset    
      And an "existing" contact is added for the site
      And the Cancel button is clicked on the Log Job form
     Then a popup is displayed requesting Cancel confirmation
  
  @uswm @ukrb
  Scenario: RFM logs a duplicate job
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is complete with duplicate information       
     Then the potential duplicate Jobs grid will be displayed   
  
  @uswm @ukrb
  Scenario Outline: RFM logs job with "<contact>" contact
    Given a "RFM" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority and no asset to be assigned to technician
      And a "<contact>" contact is added for the site
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
    Examples: 
      | contact  |
      | existing |
      | new      |  
 
   @uswm @ukrb
   Scenario Outline: City Tech logs P1 job and assigns to "<resource>"
    Given a "City Tech" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority "non tagged" asset without serial number 
      And a "existing" contact is added for the site
      And the "<resource>" resource is assigned to the job
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
    Examples: 
      | resource      | 
      | me            | 
      | City Resource |      
      | Contractor    |
      
  # only valid on USWM. Divisional Manager CAN log a job on UKRB
  @uswm @notsignedoff
  Scenario: Divisional Manager cannot log job
    Given a user with profile "Divisional Manager" 
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
     Then the user cannot log a job