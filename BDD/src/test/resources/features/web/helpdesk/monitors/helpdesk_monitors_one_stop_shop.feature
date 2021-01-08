@helpdesk @helpdesk_monitors @helpdesk_monitors_one_stop_shop
@mcp
Feature: Helpdesk - Monitors - One Stop Shop

  Scenario: One Stop Shop - To Do - Awaiting Quote Request Review monitor
    Given a "IT" has logged in
      And there is a recently created "Quote" job with client status "Awaiting Quote Request Review"
     When the "One Stop Shop" tile is selected
      And All teams are selected from Settings
      And the "Awaiting Quote Request Review" monitor is selected from "To Do" section
     Then the "Awaiting Quote Request Review To Do" monitor will display the jobs where priority and response date has been elapsed
      And the "Awaiting Quote Request Review To Do" monitor will display the jobs with "Awaiting Quote Request Review" client status
      And the "Awaiting Quote Request Review To Do" monitor will not display the jobs where priority and response date has not been elapsed
  
  Scenario: One Stop Shop - To Do - Awaiting Resource Quote monitor
    Given a "IT" has logged in
     When the "One Stop Shop" tile is selected
      And All teams are selected from Settings
      And the "Awaiting Resource Quote" monitor is selected from "To Do" section
     Then the "Awaiting Resource Quote To Do" monitor will display the jobs where priority and response date has been elapsed
      And the "Awaiting Resource Quote To Do" monitor will display the jobs with "Awaiting Resource Quote" client status
      And the "Awaiting Resource Quote To Do" monitor will not display the jobs where priority and response date has not been elapsed
  
  Scenario: One Stop Shop - For Info - Awaiting Quote Request Review monitor
    Given a "IT" has logged in
      And there is a recently created "Quote" job with client status "Awaiting Quote Request Review"
     When the "One Stop Shop" tile is selected
      And All teams are selected from Settings
      And the "Awaiting Quote Request Review" monitor is selected from "For Info" section
     Then the "Awaiting Quote Request Review For Info" monitor will display the jobs where priority and response date has not been elapsed
      And the "Awaiting Quote Request Review For Info" monitor will display the jobs with "Awaiting Quote Request Review" client status
      And the "Awaiting Quote Request Review For Info" monitor will not display the jobs where priority and response date has been elapsed
   
  Scenario: One Stop Shop - For Info - Awaiting Resource Quote monitor
    Given a "IT" has logged in
     When the "One Stop Shop" tile is selected
      And All teams are selected from Settings
      And the "Awaiting Resource Quote" monitor is selected from "For Info" section
     Then the "Awaiting Resource Quote For Info" monitor will display the jobs where priority and response date has not been elapsed
      And the "Awaiting Resource Quote For Info" monitor will display the jobs with "Awaiting Resource Quote" client status
      And the "Awaiting Resource Quote For Info" monitor will not display the jobs where priority and response date has been elapsed
 
  Scenario: One Stop Shop - To Do - ITQ Awaiting Acceptance monitor
    Given a "IT" has logged in
      And there is a recently created "Quote" job with client status "ITQ Awaiting Acceptance"    
     When the "One Stop Shop" tile is selected
      And All teams are selected from Settings
      And the "ITQ Awaiting Acceptance" monitor is selected from "To Do" section
     Then the "ITQ Awaiting Acceptance To Do" monitor will display the jobs where time has been elapsed
      And the "ITQ Awaiting Acceptance To Do" monitor will display the jobs with "ITQ Awaiting Acceptance" client status
      And the "ITQ Awaiting Acceptance To do" monitor will not display the jobs where time has not been elapsed
  
  Scenario: One Stop Shop - For Info - ITQ Awaiting Acceptance monitor
    Given a "IT" has logged in
      And there is a recently created "Quote" job with client status "ITQ Awaiting Acceptance"
     When the "One Stop Shop" tile is selected
      And All teams are selected from Settings
      And the "ITQ Awaiting Acceptance" monitor is selected from "For Info" section
     Then the "ITQ Awaiting Acceptance For Info" monitor will display the jobs where time has not been elapsed
      And the "ITQ Awaiting Acceptance For Info" monitor will display the jobs with "ITQ Awaiting Acceptance" client status
      And the "ITQ Awaiting Acceptance For Info" monitor will not display the jobs where time has been elapsed
  
  Scenario: One Stop Shop - For Info - Awaiting Quote Approval monitor
    Given a "IT" has logged in
      And there is a recently created "Quote" job with client status "Awaiting Approval"
     When the "One Stop Shop" tile is selected
      And All teams are selected from Settings
      And the "Awaiting Quote Approval" monitor is selected from "For Info" section
     Then the "Awaiting Quote Approval For Info" monitor will display the jobs where time has not been elapsed
      And the "Awaiting Quote Approval For Info" monitor will display the jobs with "Awaiting Approval" client status
      And the "Awaiting Quote Approval For Info" monitor will not display any "Awaiting Quote Request Review" jobs
      And the "Awaiting Quote Approval For Info" monitor will not display any "Awaiting Resource Quote" jobs
      And the "Awaiting Quote Approval For Info" monitor will not display any "ITQ Awaiting Acceptance" jobs