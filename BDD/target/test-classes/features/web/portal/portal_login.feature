@portal @portal_login
Feature: Portal - Login as a portal user

  # Business Team have confirmed that this test is not valid as MST Techs/RHVAC Techs use iPads to close down jobs
  # and develeopment was not continued in Portal for them
  @deprecated @wip @mcp
  Scenario: Home Page for "City Resource"
    Given a "City Resource" portal user
     When the user logs in
     Then all their configured menus items are displayed
      And a user menu displays their username and a logout option
      
  @mcp
  Scenario Outline: Home Page for "<RESOURCE TYPE>"
    Given a "<RESOURCE TYPE>" portal user
     When the user logs in
     Then all their configured menus items are displayed
      And a user menu displays their username and a logout option
      And the portal "Home" page displays correctly
    Examples:
      | RESOURCE TYPE       |
      | Contractor Admin    |
      | Directors           |
      | Head Office         |
      | Operational Manager |
                 
  @uswm
  Scenario: Home Page for RFM
    Given a "RFM" portal user
     When the user logs in
     Then a user menu displays their username and a logout option
      And the "Resource" top menu contains following sub menu options
       | Resources with upcoming unavailability, Jobs Awaiting Reallocation, On Call Summary, On Call Scheduler |
      And the "Jobs" top menu contains following sub menu options
       | Log Job, All Jobs, Feedback |
      And the "Quotes" top menu contains following sub menu options
       |  Open Quote Requests, Jobs Awaiting Quote, Jobs Awaiting Resource Selection, Quotes Awaiting Review, Quotes in Query, Quotes with Query Response |
      And the "Multi-Quotes" top menu contains following sub menu options
       | Awaiting Resource Selection, Awaiting Quote, Awaiting Review, Awaiting Bypass Review |
      And the "Invoices" top menu contains following sub menu options
       | Invoices Awaiting Approval |
      And the "Parts" top menu contains following sub menu options
       | Parts Awaiting Approval |
      And the "Funding Requests" top menu contains following sub menu options
       | Funding Request Rejected, Funding Request Query, Initial Funding Request |
      And the portal "Home" page displays correctly
      And selecting "Home" displays the "Home" page
           
       
  @ukrb
  Scenario: Home Page for AMM
    Given a "AMM" portal user
     When the user logs in
     Then a user menu displays their username and a logout option
      And the "Resource" top menu contains following sub menu options
       | Resources with upcoming unavailability, Jobs Awaiting Reallocation, On Call Summary, On Call Scheduler |
      And the "Jobs" top menu contains following sub menu options
       | Log Job, All Jobs, Feedback |
      And the "Quotes" top menu contains following sub menu options
       |  Open Quote Requests, Jobs Awaiting Quote, Jobs Awaiting Resource Selection, Quotes Awaiting Review, Quotes in Query, Quotes with Query Response |
      And the "Multi-Quotes" top menu contains following sub menu options
       | Awaiting Resource Selection, Awaiting Quote, Awaiting Review, Awaiting Bypass Review |
      And the "Parts" top menu contains following sub menu options
       | Parts Awaiting Approval |
      And the "Funding Requests" top menu contains following sub menu options
       | Funding Request Rejected, Funding Request Query, Initial Funding Request |
      And the portal "Home" page displays correctly
      And selecting "Home" displays the "Home" page


  # Business Teams UAT spreadsheet calls this role a District Director!
  @uswm
  Scenario: Home Page for Divisional Manager
    Given a "Divisional Manager" portal user
     When the user logs in
     Then a user menu displays their username and a logout option
      And the "Resource" top menu contains following sub menu options
       | Resources with upcoming unavailability |
      And the "Jobs" top menu contains following sub menu options
       | Log Job, All Jobs, Feedback |
      And the "Funding Requests" top menu contains following sub menu options
       | Awaiting Review, Pending Query Response, Awaiting Response Review, Awaiting Bypass Review |
      And the portal "Home" page displays correctly
      And selecting "Home" displays the "Home" page
      
  @ukrb
  Scenario: Home Page for Divisional Manager
    Given a "Divisional Manager" portal user
     When the user logs in
     Then a user menu displays their username and a logout option
      And the "Resource" top menu contains following sub menu options
       | Resources with upcoming unavailability |
      And the "Jobs" top menu contains following sub menu options
       | Log Job, All Jobs, Feedback |
      And the "Quotes" top menu contains following sub menu options
       | All Quotes, Approved Quotes |
      And the "Funding Requests" top menu contains following sub menu options
       | Awaiting Review, Pending Query Response, Awaiting Response Review, Awaiting Bypass Review |
      And the portal "Home" page displays correctly
      And selecting "Home" displays the "Home" page
       
  @uswm @ukrb @usah
  Scenario: Home Page for Operations Director
    Given a "Operations Director" portal user
     When the user logs in
     Then a user menu displays their username and a logout option
      And the "Resource" top menu contains following sub menu options
       | Resources with upcoming unavailability |
      And the "Jobs" top menu contains following sub menu options
       | Log Job, All Jobs, Feedback |
      And the "Funding Requests" top menu contains following sub menu options
       | Awaiting Review, Pending Query Response, Awaiting Response Review, Awaiting Bypass Review |
      And the portal "Home" page displays correctly
      And selecting "Home" displays the "Home" page
      
      
  @bugWalmart @uswm
  Scenario Outline: Outstanding activities for "<RESOURCE TYPE>" [bug: MCP-18823]
    Given a "<RESOURCE TYPE>" portal user
     When the user logs in
     Then outstanding activities are displayed "<COUNTS>" counts
    Examples:
      | RESOURCE TYPE       | COUNTS  |
      | RFM                 | with    | 
      | Operations Director | with    | 
      | Divisional Manager  | without | 

    
  @ukrb
  Scenario Outline: Outstanding activities for "<RESOURCE TYPE>"
    Given a "<RESOURCE TYPE>" portal user
     When the user logs in
     Then outstanding activities are displayed "<COUNTS>" counts
    Examples:
      | RESOURCE TYPE       | COUNTS  |
      | AMM                 | with    | 
      | Divisional Manager  | without |   
  
  
  # only valid on USWM because password authentication is not enabled for UKRB. 
  # This is enabled/disabled in HelpdeskUI/web.config on the web server and requires a re-deployment to change
  @notsignedoff @uswm
  Scenario: Unsuccessful login with incorrect password for AD user
    Given an "Active Directory" portal user
     When the user attempts to login with an incorrect password
     Then a "incorrect username or password" alert is displayed
      And users failed login count "does not increase"
      And the user can re-enter details
      
  @mcp
  Scenario: Unsuccessful login with incorrect password for Non-AD user with "Contractor Admin" profile
    Given an "Non Active Directory" portal user with profile "Contractor Admin"
     When the user attempts to login with an incorrect password
     Then a "incorrect username or password" alert is displayed
      And users failed login count "increases"      
 
  @mcp
  Scenario: Unsuccessful login with incorrect username
    Given the user is on the mercury homepage
     When the user attempts to login with an incorrect username
     Then a "incorrect username or password" alert is displayed
  
  @bugWalmart @bugRainbow @bugAdvocate @mcp
  Scenario: Successful login after 2 failed attempts [bug: MCP-19468]
    Given a user with 2 failed login attempt and profile "Contractor Admin" 
     When the user logs in
     Then users failed login count resets to 0 
   
  @mcp
  Scenario: Supplier Only - Home Overview
    Given a portal user with profile "Supply Only" 
     When the user logs in
     Then the portal "Home" page displays correctly
      And the count on the Summary page for "Invoices and Credits" matches with the count for the following sections
      | All Orders                     | 
      | Orders Awaiting Invoice        | 
      | Submitted Invoices and Credits | 
      
  #Add a step to check the counts for each section on the Summary page once the counts functionality are back!    
  @mcp
  Scenario: Accounts Payable User - Home Overview
    Given a portal user with profile "Accounts Payable" 
     When the user logs in
     Then the portal "Home" page displays correctly
      And the "Invoices and Credits" top menu contains following sub menu options
       | Scanned Documents to be Processed, Orders Awaiting Invoice, Orders Not Invoiced, Orders with Invoices In Progress, Submitted Invoices and Credits, All Invoices Awaiting Approval, Create Standalone Credit Note, Invoices & Credits Awaiting Review |
      And a user menu displays their username and a logout option
 
  @mcp @geolocation
  Scenario: Login as Contractor Tech - Off Site
    Given a portal user with profile "Contractor Technician"
      And has "Open" jobs
      And is not within GEO radius
     When the user logs in
     Then a "We could not find a site at your location" warning displayed
     
  @mcp @geolocation
  Scenario: Login as Contractor Tech - On Site
    Given a portal user with profile "Contractor Technician"
      And has "Open" jobs
      And is within GEO radius
      And the user logs in
     Then a "We could not find a site at your location" warning is NOT displayed
      And the Jobs for Site page is displayed
  
  @mcp
  Scenario: Contractor Log in - Forgotten password - Password Reset Request
    Given a portal user with profile "Contractor Technician"
     When the user selects 'Forgotten Password?'
      And they request an Email Link on the Password Reset Request page
     Then a Password Reset Request Confirmation is displayed
      And an email is sent containing password reset link

  @mcp
  Scenario: Contractor Log in - Forgotten password - Password Reset
    Given a portal user with profile "Contractor Technician"
      And the user selects 'Forgotten Password?'
      And they request an Email Link on the Password Reset Request page
      And the Reset Password link is selected on the email 
     When the password is reset on the Reset Password page
     Then the user can log in with the new password
     