@portal @portal_quotes @portal_quotes_create
@mcp
Feature: Portal - Quotes - Create a quote from the portal
  
  Scenario Outline: Verify the Jobs Awaiting Quote page displays the correct information for a "<profile>"
    Given a portal user with a "<profile>" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
     When the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
     Then the "Jobs Awaiting Quote" form displays correctly 
    Examples:
      | profile          |
      | Contractor Admin |
   
   Scenario Outline: Verify that a "<profile>" profile can see a quote status "<quote_status>"
    Given a "<profile>" with a "single" "Quote" in state "<quote_status>"
      And the user logs in
     When the portal "Home" page displays correctly
     Then the "Summary" table contains the "<quote_status>" section   
    Examples: 
      | profile          | quote_status                 | 
      | Contractor Admin | Jobs Awaiting Quote          | 
      | Contractor Admin | Quotes Awaiting Review       | 
      | Contractor Admin | Quotes With Query Pending    | 
   
   Scenario Outline: Verify that a "<profile>" profile can see a quote status "<quote_status>"
    Given a "<profile>" with a "single" "Quote" in state "<quote_status>"
      And the user logs in
      And the portal "Home" page displays correctly
     When "<quote_status>" is selected from outstanding activities
     Then the "<quote_status>" form displays correctly 
    Examples: 
      | profile          | quote_status              | 
      | Contractor Admin | Jobs Awaiting Quote       | 
      | Contractor Admin | Quotes Awaiting Review    | 
      | Contractor Admin | Quotes With Query Pending | 
   
  Scenario Outline: Verify that a user can Accept a quote for a "<profile>"
    Given a portal user with a "<profile>" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
     When the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
     Then the "Jobs Awaiting Quote" form displays correctly 
    Examples: 
      | profile          |
      | Contractor Admin |
 
  Scenario Outline: Verify that a user can Decline a quote for a "<profile>"
    Given a portal user with a "<profile>" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
     When the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Declines" a "Jobs Awaiting Quote"
     Then the "Decline Invitation To Quote" form displays correctly 
    Examples: 
      | profile          |
      | Contractor Admin |

  # [bug: MCP-13490] removed steps that are not in UAT spreadsheet
  Scenario: Decline a Job ITQ Awaiting Acceptance and the database is updated as expected for a "Contractor Admin"
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Declines" a "Jobs Awaiting Quote"
     When the Reason to decline is selected
      And the notes for the decline are entered
      And the "Decline Invitation To Quote" is saved
     Then the JobTimelineEvent table has been updated with "Resource declined invitation to quote"
      And the JobTimelineEvent table has been updated with "Invitation To Quote Declined Notification"
      And the Job is updated with a "Awaiting Resource Assignment" status
      #And the Message table has been updated with "DeclinedInvitationToQuote"
      And the Resource status will be None Assigned
  
  Scenario: Verify if system notifies the original approver that the ITQ has been declined
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Declines" a "Jobs Awaiting Quote"
     When the Reason to decline is selected
      And the notes for the decline are entered
      And the "Decline Invitation To Quote" is saved
     Then an email is sent for "Invitation To Quote Declined Notification" 
   
  Scenario: Validate decline reason dropdown list
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
     When the user "Declines" a "Jobs Awaiting Quote" 
     Then the decline reason list is validated
    
  Scenario Outline: Accepts a Job ITQ Awaiting Acceptance and verify the Job is updated for a "<profile>"
    Given a portal user with a "<profile>" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
     When the user "Accepts" a "Jobs Awaiting Quote"
     Then the Job is updated with a "Awaiting Resource Quote" status
      And the JobTimelineEvent table has been updated with "Invitation to Quote Accepted"
    Examples: 
      | profile          |
      | Contractor Admin |

  Scenario Outline: Verify the Create Quote page displays for a Job ITQ Awaiting Acceptance for a "<profile>"
    Given a portal user with a "<profile>" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
     When the user "Creates" a "Jobs Awaiting Quote"
     Then the "Create Quote" form displays correctly 
    Examples: 
      | profile          |
      | Contractor Admin |
 
  Scenario: Create Quote for a Job ITQ Awaiting Acceptance as a Contractor Admin resource 
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Quote Reference is entered
      And the Description of works is entered
      And the Proposed Working times is entered
      And and the user starts the quote process
     Then the JobTimelineEvent table has been updated with "Invitation to Quote Accepted"
      And the Quote summary information has been updated
  
  #Adding deprecated tag as this is not a valid scenario for a City Resource to view a jobs awaiting quote in a Portal side 
  @deprecated @wip
  Scenario: Create a Quote Summary for a Job ITQ Awaiting Acceptance as a City resource
    Given a portal user with a "City Resource" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Description of works is entered
      And the Proposed Working times is entered
      And and the user starts the quote process
     Then the JobTimelineEvent table has been updated with "Invitation to Quote Accepted"
      And the Quote summary information has been updated

  Scenario: Create a Quote Summary for a Job ITQ Awaiting Acceptance that is High Risk Works as a Contractor resource
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Quote Reference is entered
      And the Description of works is entered
      And the work is High Risk with random risks
      And the Proposed Working times is entered
      And and the user starts the quote process
     Then the JobTimelineEvent table has been updated with "Invitation to Quote Accepted"
      And the Quote summary information has been updated 
      
  #Adding deprecated tag as this is not a valid scenario for a City Resource to view a jobs awaiting quote in a Portal side 
  @deprecated @wip
  Scenario: Create a Quote Summary for a Job ITQ Awaiting Acceptance that is High Risk Works as a City resource
    Given a portal user with a "City Resource" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Description of works is entered
      And the work is High Risk with random risks
      And the Proposed Working times is entered
      And and the user starts the quote process
     Then the JobTimelineEvent table has been updated with "Invitation to Quote Accepted"
      And the Quote summary information has been updated   

  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and verify the Quote line breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile    
      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added
	  | type    | description | quantity | unitPrice | minUnitPrice | maxUnitPrice |
      | Labour  | something   | 1        | 2499.99   | 0            | 100          |
	  | Parts   | widget      | 10       | 14.99     | 0            | 100          |
	  | Travel  | petrol      | 1        | 74.49     | 0            | 100          |
     Then the Quote Line information has been updated
  
  Scenario: Add quote line, cancel it and verify that the Quote Line grid is not updated
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile    
      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice> is entered
        | type    | description | quantity | unitPrice |
        | Labour  | something   | 1        | 2499.99   |
      And the line is not added to the quote
     Then the Quote line grid is empty

  @wip
  Scenario: Create a Quote for a Job Awaiting Quote for a Contractor resource and verify the Quote line breakdown has been updated
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "Awaiting Quote" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
   #   And the user "Creates" a "Jobs Awaiting Quote"
#      And the "Create Quote" form displays correctly
      And the user "Registers" a "Jobs Awaiting Quote"      
#      And the "Register Quote" is created
#      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added
	  | type    | description | quantity | unitPrice | minUnitPrice | maxUnitPrice |
      | Labour  | something   | 1        | 2499.99   | 0            | 100          |
	  | Parts   | widget      | 10       | 14.99     | 0            | 100          |
	  | Travel  | petrol      | 1        | 74.49     | 0            | 100          |
     Then the Quote Line information has been updated

  Scenario: Submit a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource - Labour, Parts and Travel
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added
	  | type    | description | quantity | unitPrice | minUnitPrice | maxUnitPrice |
      | Labour  | something   | 1        | 2499.99   | 0            | 100          |
	  | Parts   | widget      | 10       | 14.99     | 0            | 100          |
	  | Travel  | petrol      | 1        | 74.49     | 0            | 100          |
      And uploads the quote document
      And the user submits the quote 
     Then the Job is updated with one of "Awaiting Approval/Awaiting Resource Quote" status     
      And the JobTimelineEvent table has been updated with one of "Quote Awaiting Approval/Awaiting Resource Quote"
      And the JobTimelineEvent table has been updated with "Quote Submitted Notification" 
  
  Scenario: Verify that an email has been sent to the approver when Quote has been submitted
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added
      | type    | description | quantity | unitPrice | minUnitPrice | maxUnitPrice |
      | Labour  | something   | 1        | 2499.99   | 0            | 100          |
      And uploads the quote document
      And the user submits the quote 
     Then an email is sent for "Quote Submitted Notification" 
      
  @smoke
  Scenario: Submit a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource - Labour
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added
	  | type    | description | quantity | unitPrice | minUnitPrice | maxUnitPrice |
      | Labour  | something   | 1        | 2499.99   | 0            | 100          |
      And uploads the quote document
      And the user submits the quote
     Then the Job is updated with one of "Awaiting Approval/Awaiting Resource Quote" status     
      And the "Quote Submitted Notification" notification has been updated
        
  #Adding deprecated tag as this is not a valid scenario for a City Resource to view a jobs awaiting quote in a Portal side 
  @deprecated @wip
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a City resource and order a part in the list
    Given a portal user with a "City Resource" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "City Resource" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Parts" is entered
      And the line item part code is selected from the list
      And the line item quantity <1> is entered
      And the line is added to the quote
      And the user submits the quote
     Then the Job is updated with a "Awaiting Approval" status     
      And the JobTimelineEvent table has been updated with "Quote Awaiting Approval"
      And the JobTimelineEvent table has been updated with "Quote Submitted Notification"

  #Adding deprecated tag as this is not a valid scenario for a City Resource to view a jobs awaiting quote in a Portal side 
  @deprecated @wip
  Scenario: Create Single Quote for a Job ITQ Awaiting Acceptance for a City resource and submit a quote line item of type Hire/Access Equipment
    Given a portal user with a "City Resource" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Single" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "City Resource" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Hire/Access Equipment" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <5009.99> is entered
      And the line is added to the quote
      And the user submits the quote
     Then the JobTimelineEvent table has been updated with "Quote Submitted Notification"
      
  