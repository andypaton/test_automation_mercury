@portal @portal_multiquotes @portal_multiquotes_create
@mcp
Feature: Portal - Multi Quotes - Create a quote from the portal - Negative

  Scenario Outline: Create Quote for a Job ITQ Awaiting Acceptance as a Contractor resource and and less than the required characters for Description of Works
    Given a portal user with a "<profile>" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Quote Reference is entered
      And the Description of works is entered with less than "20" characters
      And the Proposed Working times is entered
      And and the user clicks Start Quote
     Then the following error is displayed: "Description of Works must be 20 characters or more"
    Examples: 
      | profile          |
      | Contractor Admin |
   
  Scenario: Create Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and does not upload a quote document
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added
      | type   | description | quantity | unitPrice | minUnitPrice | maxUnitPrice | 
      | Labour | something   | 1        | 2499.99   | 0            | 100          | 
      And the user submits the quote
     Then the following error is displayed: "Quote upload is missing, please add this document"
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance and attempt to upload a document which is too large
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When uploads the quote document which is too large
     Then the following error is displayed: "File exceeds maximum allowed file size."      
  
  Scenario: Create Quote for a Job ITQ Awaiting Acceptance as a Contractor resource omit the Descripton of works
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Quote Reference is entered
      And the Proposed Working times is entered
      And and the user clicks Start Quote
     Then the following error is displayed: "Description of Works is required"
  
  Scenario: Create Quote for a Job ITQ Awaiting Acceptance as a Contractor resource and omit the Proposed Working times
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Quote Reference is entered
      And the Description of works is entered
      And and the user clicks Start Quote
     Then the following error is displayed: "Proposed Working Times is required"
  
  Scenario: Create Quote for a Job ITQ Awaiting Acceptance as a Contractor resource and omit the Quote Reference
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
     When the Description of works is entered
      And the Proposed Working times is entered
      And and the user clicks Start Quote
     Then the following error is displayed: "Quote Reference is required"
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and omit adding quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When uploads the quote document
      And the user submits the quote
     Then the following error is displayed: "A quote must contain at least one quote line"
      And the following error is displayed: "The total value of the quote should be greater than zero"
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and omit line type in the quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <9.99> is entered 
      And the line is added to the quote
     Then the error message "Type is required" for "Line type" will be displayed
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and omit line description in the quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Travel" is entered
      And the line item quantity <1> is entered
      And the line item unit price <9.99> is entered 
      And the line is added to the quote
     Then the error message "Description is required" for "Line description" will be displayed
  
  Scenario: Create a Quote a Job ITQ Awaiting Acceptance for a Contractor resource and omit line quantity in the quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Travel" is entered
      And the line item description "some value" is entered
      And the line item unit price <9.99> is entered 
      And the line is added to the quote
     Then the error message "Quantity is required" for "Line quantity" will be displayed
  
  Scenario: Create a Quote a Job ITQ Awaiting Acceptance for a Contractor resource and omit line unit price in the quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Travel" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line is added to the quote
     Then the error message "Unit Price is required" for "Line unit price" will be displayed
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and enter an invalid quantity in the quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Travel" is entered
      And the line item description "some value" is entered
      And the line item quantity "invalid" is entered
      And the line item unit price <9.99> is entered 
      And the line is added to the quote
     Then the error message "The field Quantity must be a number." for "Line quantity" will be displayed
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and enter an invalid unit price in the quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Travel" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price "invalid" is entered 
      And the line is added to the quote
     Then the error message "Unit price must contain only numeric characters and cannot exceed 2 decimal places" for "Line unit price" will be displayed
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and omit Part Code the quote breakdown
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Parts" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <9.99> is entered 
      And the line is added to the quote
     Then the error message "Part Number is required" for "Part Number" will be displayed
  
  Scenario: Create a Quote a Job ITQ Awaiting Acceptance for a Contractor resource and add a quote line item of type Part and with a zero priced item and verify error message
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a new line is added to the quote breakdown
      And the line item type "Parts" is entered
      And the line item part number is entered 
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <0.00> is entered
      And the line is added to the quote
     Then the error message "Unit price must be a number greater than 0" for "Line unit price" will be displayed
  
  Scenario: Create a Quote a Job ITQ Awaiting Acceptance for a Contractor resource and and verify multiple Quote lines can be added
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly 
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process   
     When a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added
      | type   | description | quantity | unitPrice | minUnitPrice | maxUnitPrice |
      | Labour | something   | 1        | 2499.99   | 0            | 100          |
      | Labour | something   | 1        | 2499.99   | 0            | 100          |
      | Labour | something   | 1        | 2499.99   | 0            | 100          |
      | Labour | something   | 1        | 2499.99   | 0            | 100          |
      | Labour | something   | 1        | 2499.99   | 0            | 100          |
      And the user submits the quote
     Then the following error is displayed: "Quote upload is missing, please add this document"
