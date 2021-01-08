@helpdesk @helpdesk_monitors @helpdesk_monitors_todo_forinfo
Feature: Helpdesk - Monitors - To Do/For Info

  #@MTA-843
  @mcp
  Scenario: Helpdesk To Do/For Info - Sections
    Given a "IT" has logged in
     Then for each monitor the following sections are collapsable 
      | monitor         | sections        | 
      | Jobs            | To Do, For Info | 
      | Incidents       | To Do, For Info | 
      | Health & Safety | To Do, For Info | 
      | Admin           | For Info        | 
     # | Bureau          | To Do           | 
      | One Stop Shop   | To Do, For Info |     
      | Landlord Jobs   | To Do, For Info | 
       
  @uswm
  Scenario: Helpdesk To Do - Section
    Given a "Helpdesk Operator" has logged in
     Then for each monitor the following sections are collapsable 
      | monitor         | sections        | 
      | Focus           | To Do           | 
      
  #@MTA-843
  @mcp
  Scenario: Helpdesk To Do/For Info - monitor contents
    Given a "IT" has logged in
     Then the each monitor displays My List and Team List heading for the below sections
      | monitor         | sections        | 
      | Jobs            | To Do, For Info | 
      | Incidents       | To Do, For Info | 
      | Health & Safety | To Do, For Info | 
      | Admin           | For Info        | 
    #  | Bureau          | To Do           | 
      | One Stop Shop   | To Do, For Info |   
      | Landlord Jobs   | To Do, For Info | 
           
  @uswm
  Scenario: Helpdesk To Do/For Info - monitor contents
    Given a "Helpdesk Operator" has logged in
     Then the each monitor displays My List and Team List heading for the below sections
      | monitor         | sections        | 
      | Focus           | To Do           |  
       
  #@MTA-846
  @mcp
  Scenario: Helpdesk To Do - My List count check
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
      And the "Awaiting Assignment" monitor is selected from "To Do" section
     Then the "To Do" "My List" team count reflects the count as per the monitor grid and filters added by user
  
  #@MTA-846
  @mcp
  Scenario: Helpdesk To Do - Team List count check
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
      And the "Chase" monitor is selected from "To Do" section
     Then the "To Do" "Team List" team count reflects the count as per the monitor grid and filters added by user
  
  #@MTA-846
  @mcp
  Scenario: Helpdesk For Info - My List count check
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
      And the "Watched Jobs" monitor is selected from "For Info" section
     Then the "For Info" "My List" team count reflects the count as per the monitor grid and filters added by user
  
  #@MTA-846
  @mcp
  Scenario: Helpdesk For Info - Team List count check
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
      And the "Watched Jobs" monitor is selected from "For Info" section
     Then the "For Info" "Team List" team count reflects the count as per the monitor grid and filters added by user
  
  #@MTA-861 
  @mcp
  Scenario: One Stop Shop - To Do - My List count check
    Given a "IT" has logged in
     When the "One Stop Shop" tile is selected
      And a random team is added to Settings
      And the "Awaiting Quote Request Review" monitor is selected from "To Do" section
     Then the "To Do" "My List" team count reflects the count as per the monitor grid and filters added by user
  
  #@MTA-861
  @mcp
  Scenario: One Stop Shop - To Do - Team List count check
    Given a "IT" has logged in
     When the "One Stop Shop" tile is selected
      And the "Awaiting Resource Quote" monitor is selected from "To Do" section
     Then the "To Do" "Team List" team count reflects the count as per the monitor grid and filters added by user
  
  #@MTA-861 
  @mcp
  Scenario: One Stop Shop For Info - My List count check
    Given a "IT" has logged in
     When the "One Stop Shop" tile is selected
      And a random team is added to Settings
      And the "Awaiting Resource Quote" monitor is selected from "For Info" section
     Then the "For Info" "My List" team count reflects the count as per the monitor grid and filters added by user
  
  #@MTA-861
  @mcp
  Scenario: One Stop Shop For Info - Team List count check
    Given a "IT" has logged in
     When the "One Stop Shop" tile is selected
      And the "Awaiting Resource Quote" monitor is selected from "For Info" section
     Then the "For Info" "Team List" team count reflects the count as per the monitor grid and filters added by user
