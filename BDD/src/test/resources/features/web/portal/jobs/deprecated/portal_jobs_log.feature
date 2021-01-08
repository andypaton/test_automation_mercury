@portal @portal_jobs
@mcp
@deprecated @wip
Feature: Portal - Jobs - Log

####### NOTE: only RFM/AMM should be able to log job via Portal


  #@MCF @MCP-7234 @MCP-8274
  #@MTA-150 @MTA-214 @MTA-404
  Scenario Outline: "<profile>" logs "<priority>" job for "<asset>" asset without a serial number and with "<contact>" contact and assigns to "<resource>"
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "<priority>" priority "<asset>" asset without serial number 
      And a "<contact>" contact is added for the site
      And the "<resource>" resource is assigned to the job
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
    Examples: 
      | profile     | priority | asset      | resource      | contact  |
      | City Tech   | P3       | non tagged | City Resource | existing |
      | City Tech   | P1       | non tagged | Contractor    | new      |
      | City Tech   | P2       | non tagged | Contractor    | existing |
      | Supervisor  | P2       | tagged     | me            | existing |
      | City Tech   | P3       | non tagged | City Resource | new      |
      | City Tech   | P3       | non tagged | Contractor    | new      |
      | Supervisor  | P3       | tagged     | me            | new      |
  
  Scenario: Supervisor logs P1 job for tagged asset without a serial number and with existing contact and assigns to self
     Given a "Supervisor" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority "tagged" asset without serial number 
      And an "existing" contact is added for the site
      And the "me" resource is assigned to the job
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
  
  #@MTA-150 @MTA-214
  Scenario Outline: "<profile>" logs "<priority>" job for "<asset>" asset with a serial number and with "<contact>" contact and assigns to "<resource>"
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "<priority>" priority "<asset>" asset with serial number
      And an "<contact>" contact is added for the site
      And the "<resource>" resource is assigned to the job
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
    Examples: 
      | profile     | priority | asset      | resource      | contact  |
      | City Tech   | P1       | non tagged | Contractor    | new      |
      | City Tech   | P3       | non tagged | City Resource | existing |
      | City Tech   | P2       | non tagged | Contractor    | existing |
      | Supervisor  | P1       | tagged     | me            | existing |
      | Supervisor  | P2       | tagged     | me            | existing |
      | City Tech   | P3       | non tagged | City Resource | new      |
      | City Tech   | P3       | non tagged | Contractor    | new      |
      | Supervisor  | P3       | tagged     | me            | new      |
  
  
  #@MTA-150 @MTA-777
  Scenario Outline: "<profile>" logs "<priority>" job without asset and with "<contact>" contact and assigns to "<resource>"
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "<priority>" priority and no asset
      And an "<contact>" contact is added for the site
      And the "<resource>" resource is assigned to the job
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
    Examples: 
      | profile     | priority | resource      | contact  |
      | Supervisor  | P2       | me            | new      |     
      | Supervisor  | P2       | me            | existing |
      | City Tech   | P1       | Contractor    | existing |
      | City Tech   | P1       | Contractor    | new      |
      | City Tech   | P3       | City Resource | new      |
  
  
  #@MCP-1859
  Scenario Outline: "<profile>" logs "<priority>" job without asset and quote requested
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "<priority>" priority and no asset    
      And a "<quote>" quote is requested
      And a random Quote Priority is selected
      And a "new" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status
    Examples: 
      | profile     | priority | quote |
      | City Tech   | P2       | CAPEX |
      | Supervisor  | P3       | OPEX  |
      
  Scenario: City Tech logs P1 job without asset and quote requested
     Given a "City Tech" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "P1" priority and no asset    
      And an "OPEX" quote is requested
      And a random Quote Priority is selected
      And a "new" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status  
  
  #@MCP-1859 @MTA-189 @MTA-923
  Scenario Outline: "<profile>" logs "<priority>" job without asset and with "<contact>" contact and non urgent quote requested for "<fundingRoute>" funding route
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "<priority>" priority and no asset    
      And a non-urgent quote with a "<fundingRoute>" funding route is requested
      And a "<contact>" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status
    Examples: 
      | profile     | priority | fundingRoute | contact  |
      | City Tech   | P2       | OPEX         | existing |
      | City Tech   | P2       | CAPEX        | new      |
      | City Tech   | P2       | CAPEX        | new      |
      | City Tech   | P1       | OPEX         | new      |
      | City Tech   | P3       | OPEX         | new      |
  
  
  #@MCP-1859 @MTA-189
  Scenario Outline: "<profile>" logs "<priority>" job without asset and urgent quote requested for "<fundingRoute>" funding route
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "<priority>" priority and no asset    
      And a urgent quote with a "<fundingRoute>" funding route is requested
      And a "<contact>" contact is added for the site    
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Awaiting Quote Request Review" status or "ITQ Awaiting Acceptance" status
    Examples: 
      | profile     | priority | fundingRoute | contact  |
      | RFM         | P2       | OPEX         | existing |
      | RFM         | P2       | CAPEX        | existing |
      | City Tech   | P2       | OPEX         | existing |
      | City Tech   | P2       | CAPEX        | new      |
   
   
  Scenario Outline: "<profile>" cancels logging a new job assigned to "<resource>"
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a tagged asset 
      And an "existing" contact is added for the site
      And the "<resource>" resource is assigned to the job
      And the Cancel button is clicked on the Log Job form
     Then a popup is displayed requesting Cancel confirmation
    Examples: 
      | profile     | resource      | 
      | City Tech   | me            | 
      | City Tech   | Contractor    | 
      | City Tech   | City Resource | 
  
  
  #@duplicateJobCheck
  Scenario: "City Tech" logs a duplicate job
     Given a "City Tech" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is complete with duplicate information       
     Then the potential duplicate Jobs grid will be displayed   
  
  Scenario Outline: "<profile>" logs "<priority>" job with "<contact>" contact and assign to "<resource>"
     Given a "<profile>" with access to the "Jobs > Log Job" menu
     When the user logs in
      And the "Log Job" sub menu is selected from the "Jobs" top menu
      And the form is completed with a "<priority>" priority and no asset to be assigned to technician
      And a "<contact>" contact is added for the site
      And the "<resource>" resource is assigned to the job
      And the log job form is saved
     Then a confirmation that the job has been logged with job number is displayed
      And the Job will exist in the database
      And the Job is sitting in "Allocated" status or "Logged" status
    Examples: 
      | profile     | priority | resource      | contact  |
      | Supervisor  | P2       | me            | new      |
      | Supervisor  | P2       | me            | existing |
      | City Tech   | P1       | City Resource | new      |
      | City Tech   | P3       | City Resource | new      |  
 