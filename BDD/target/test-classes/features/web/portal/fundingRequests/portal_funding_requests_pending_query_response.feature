@portal @portal_funding_requests  @portal_funding_requests_pending_query_response
Feature: Portal - Funding Requests - Pending Query Response

  @bugRainbow @uswm @ukrb @usah
  Scenario Outline: "<profile>" views Funding Requests Pending Query Response [bug: MCP-13654 on ukrb]
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Pending Query Response"
      And the user logs in
     When the "Pending Query Response" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Pending Query Response" table on the "Funding Requests Pending Query Response" page displays correctly
      And a search box is present on the "Funding Requests Pending Query Response" page
      And the "Funding Requests Pending Query Response" table can be sorted on all columns
      And the user is unable to interact with the jobs on this screen
    Examples: 
      | profile                   | 
      | Additional Final Approver |
      
  @bugRainbow @mcp
  Scenario Outline: "<profile>" views Funding Requests Pending Query Response [bug: MCP-13654 on ukrb]
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Pending Query Response"
      And the user logs in
     When the "Pending Query Response" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Pending Query Response" table on the "Funding Requests Pending Query Response" page displays correctly
      And a search box is present on the "Funding Requests Pending Query Response" page
      And the "Funding Requests Pending Query Response" table can be sorted on all columns
      And the user is unable to interact with the jobs on this screen
    Examples: 
      | profile             |  
      | Additional Approver | 

  @uswm @ukrb @usah
  Scenario Outline: Verify as a "<profile>" the Funding Requests Pending Query Response Grid displays the data correctly
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Pending Query Response"
      And the user logs in
     When the "Pending Query Response" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Pending Query Response" table on the "Funding Requests Pending Query Response" page displays the latest job row correctly
    Examples: 
      | profile                   | 
      | Additional Final Approver | 
      
  @mcp
  Scenario Outline: Verify as a "<profile>" the Funding Requests Pending Query Response Grid displays the data correctly
    Given a "<profile>" with a "single" "Quote" in state "Funding Requests Pending Query Response"
      And the user logs in
     When the "Pending Query Response" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests Pending Query Response" table on the "Funding Requests Pending Query Response" page displays the latest job row correctly
    Examples: 
      | profile             | 
      | Additional Approver |