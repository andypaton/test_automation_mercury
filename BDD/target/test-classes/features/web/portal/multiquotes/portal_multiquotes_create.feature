@portal @portal_multiquotes @portal_multiquotes_create
@mcp
Feature: Portal - Multi Quotes - Create a quote from the portal
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and verify the line value is correct
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
      And the line item quantity <12> is entered
      And the line item unit price <9.99> is entered
     Then the line value is calculated correctly
  
  @smoke
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and submit the quote
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
      And the line item unit price <9.99> is entered
      And the line is added to the quote
      And uploads the quote document
      And the user submits the quote
     Then the "Quote Submitted Notification" notification has been updated
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and cancel add a quote line item of type Part and verify that the Quote Line grid is not updated
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
      And the line item unit price <9.99> is entered
      And the line is not added to the quote
     Then the Quote line grid is empty
  
  #@MTA-229
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and add a quote line item of type Part and verify that the Quote Line grid is updated correctly
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
      And the line item unit price <9.99> is entered
      And the line is added to the quote
     Then the Quote line grid is updated
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and add a quote line item of type Labour then submit and verify
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process
     When a new line is added to the quote breakdown
      And the line item type "Labour" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <9.99> is entered
      And the line is added to the quote
      And uploads the quote document
      And the user submits the quote
     Then the "Quote Submitted Notification" notification has been updated
  
  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and add a quote line item of type Labour then delete line and verify
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process
     When a new line is added to the quote breakdown
      And the line item type "Labour" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <9.99> is entered
      And the line is added to the quote
      And the line with description "some value" is selected for delete
      And the line is deleted
     Then the Quote line grid is updated with the delete

  Scenario: Create a Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and add a quote line item of type Labour then cancel a delete line and verify
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process
     When a new line is added to the quote breakdown
      And the line item type "Labour" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <9.99> is entered
      And the line is added to the quote
      And the line with description "some value" is selected for delete
      And the line is not deleted
     Then the Quote line grid is updated
  
  Scenario: Create Multi Quote for a Job ITQ Awaiting Acceptance for a Contractor resource and submit a quote line item of type Hire/Access Equipment
    Given a portal user with a "Contractor Admin" profile and with "Jobs Awaiting Quote" Jobs in "ITQ Awaiting Acceptance" for a "Multi" quote
      And the user logs in
      And the "Jobs Awaiting Quote" sub menu is selected from the "Quotes" top menu
      And the user "Accepts" a "Jobs Awaiting Quote"
      And the user "Creates" a "Jobs Awaiting Quote"
      And the "Create Quote" form displays correctly
      And the "Quote" is created as a "Contractor Admin" profile
      And and the user starts the quote process
     When a new line is added to the quote breakdown
      And the line item type "Hire/Access Equipment" is entered
      And the line item description "some value" is entered
      And the line item quantity <1> is entered
      And the line item unit price <9.99> is entered
      And the line is added to the quote
      And uploads the quote document
      And the user submits the quote
     Then the "Quote Submitted Notification" notification has been updated