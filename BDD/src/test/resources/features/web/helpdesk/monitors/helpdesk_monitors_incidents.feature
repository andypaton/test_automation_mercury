@helpdesk @helpdesk_monitors @helpdesk_monitors_incidents
Feature: Helpdesk - Monitors - Incidents

  #@MTA-772
  @ukrb @uswm
  Scenario: To Do/Pending To Do - will display any incident
    Given a "Helpdesk Operator" has logged in
     When the "Incidents" tile is selected
      And All teams are selected from Settings
     Then the monitor displays "Settings, To Do, For Info" sections
      And the Pending To Do monitor will display the incidents with below status
      |Incident Initial Review                |
      |Immediate Escalation                   |
      |Incident Followup                      |
      |Incident Telephone Escalation Callback |
      
  #@MTA-772
  @ukrb @uswm
  Scenario: To Do/Pending To Do - will not display any incident
    Given a "Helpdesk Operator" has logged in
     When the "Incidents" tile is selected
      And All teams are selected from Settings
     Then the Pending To Do monitor will not display the incidents with below status
     |Incident has been reviewed           |
     |Incident has no escalations required |
     |Incident has no follow ups required  |
     |Escalation call back is not required |
         
  #@MTA-851
  @ukrb @uswm
  Scenario: To Do/Initial Escalations monitor
    Given a "Helpdesk Operator" has logged in
     When the "Incidents" tile is selected
      And All teams are selected from Settings
      And the "Initial Escalations" monitor is selected from "To Do" section
     Then the "Initial Escalations" monitor will display the incidents with "Immediate Escalation, Incident Telephone Escalation Callback" status
      And the "Initial Escalations" monitor will not display the incidents with status "Logged, Cancelled, Incident Followup"   
     
  #@MTA-780
  @ukrb @uswm
  Scenario: To Do/Reviews monitor
    Given a "Helpdesk Operator" has logged in
     When the "Incidents" tile is selected
      And All teams are selected from Settings
      And the "Reviews" monitor is selected from "To Do" section
     Then the "Reviews" monitor will display the incidents with status "Incident Initial Review"
      And the "Reviews" monitor will not display the incidents with status "Logged, Cancelled, Incident Followup" 

  #@MTA-790
  @uswm
  Scenario: To Do/Follow Ups monitor
    Given a "Helpdesk Operator" has logged in
     When the "Incidents" tile is selected
      And All teams are selected from Settings
      And the "Follow ups" monitor is selected from "To Do" section
     Then the "Follow ups" monitor will display the incidents with status "Incident Followup"
      And the "Follow ups" monitor will not display the incidents with status "Logged, Cancelled" 
     
  #@MTA-792 @MTA-1045
  @uswm
  Scenario: To Do/Jobs For Review monitor
    Given a "Helpdesk Operator" has logged in
     When the "Incidents" tile is selected
      And the "Jobs For Review" monitor is selected from "To Do" section
      And All teams are selected from Settings
     Then the Jobs For Review monitor will display the jobs with Forced Incident criteria with no linked incidents
      And the Jobs For Review monitor will not display the jobs with Forced Incident criteria with linked incidents
      
  #@MTA-797 @MTA-906
  # functionality not possible for UKRB : RDATA-2
  @uswm
  Scenario: Upcoming To Do monitor
    Given a "Helpdesk Operator" has logged in
      And the user has incidents with upcoming follow up
     When the "Incidents" tile is selected
      And All teams are selected from Settings
      And the "Upcoming To Do" monitor is selected from "To Do" section
     Then the "Upcoming To Do" monitor will display only the incidents where the follow up time is greater than "15" minutes
      And the "Upcoming To Do" monitor will not display the incidents with status "Logged, Cancelled"