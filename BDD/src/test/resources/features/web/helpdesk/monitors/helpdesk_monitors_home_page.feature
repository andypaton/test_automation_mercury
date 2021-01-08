@helpdesk @helpdesk_monitors @helpdesk_monitors_home_page
Feature: Helpdesk - Monitors - Home Page
         
  @sanity
  @mcp
  Scenario: Home Page - login with "Helpdesk Operator" profile
    Given a "Helpdesk Operator" has logged in
     Then the "helpdesk" page is displayed with the user profiles configured monitor tiles
      And each tile contains TO DO and FOR INFO totals for MY TEAM and NATIONAL                       
             
  @mcp
  Scenario Outline: Home Page - login with "<profile>" profile
    Given a "<profile>" has logged in
     Then the "helpdesk" page is displayed with the user profiles configured monitor tiles
    Examples: 
      | profile             |
     #| Helpdesk Director   |
      | Helpdesk Manager    |
      | Helpdesk Supervisor |
      | Helpdesk Teamleader |
      | IT                  |
     #| Helpdesk Trainer    |

  @mcp @deprecated @wip
  Scenario: Home Page - login with "Project Team" profile
    Given a "Project Team" has logged in
     Then the "helpdesk" page is displayed with the user profiles configured monitor tiles
  
  #@MTA-450
  @mcp
  Scenario: Home Page - Tile contents
    Given a "IT" has logged in
     Then each tile contains totals for MY TEAM and NATIONAL:
      | Jobs              | To Do, For Info | 
      | Incidents         | To Do, For Info | 
      | Health & Safety   | To Do, For Info | 
      | Admin             | For Info        | 
      #| Bureau            | To Do           | 
      
  @ukrb
  Scenario: Home Page - Tile contents - Managed Contracts
    Given a "IT" has logged in
     Then each tile contains totals for MY TEAM and NATIONAL:
      | Managed Contracts     | To Do, For Info | 
       
  @uswm @usad
  Scenario: Home Page - Tile contents 
    Given a "IT" has logged in
     Then each tile contains totals for MY TEAM and NATIONAL:
      | One Stop Shop     | To Do, For Info | 
      | Focus             | To Do           |   

  #@MTA-537
  @mcp @deprecated @wip
  Scenario: Home Page - Tile contents - Landlord Jobs
    Given a "IT" has logged in
     Then each tile contains totals for MY TEAM and NATIONAL:
      | Landlord Jobs   | To Do, For Info | 
      
  #@MTA-450
  @mcp
  Scenario: Home Page - Tile selected
    Given a "Helpdesk Operator" has logged in
     When a random tile is selected
     Then the selected tile's monitors tab has focus
      And only one tab for the selected tile can be opened at any time