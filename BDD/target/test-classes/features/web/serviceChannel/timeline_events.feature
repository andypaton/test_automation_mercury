@serviceChannel
Feature: Service Channel Timeline Events

  @serviceChannel_Jenkins
  Scenario: Timeline events are displayed correctly on Service Channel front end
    Given an IT user has logged in
     When the "Logged" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel
      
  @serviceChannel_Jenkins
  Scenario: Quotes Approved Timeline events 
     When the "JobQuoteApproved" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel
      
  @serviceChannel_Jenkins
  Scenario: Job Type Changed to Reactive Timeline event
    Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
      And the user logs in
     When the "JobTypeChangedToReactive" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel

  #Scenario 1
  @serviceChannel_Jenkins
  Scenario: Timeline events are displayed correctly on Service Channel front end
    Given an IT user has logged in
     When the "Logged" timeline event is triggered
      And the "PublicNoteAdded" timeline event is triggered
      And the "PrivateNoteAdded" timeline event is triggered
      And the "Reopened" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel
       
  #Scenario 2
  @serviceChannel_Jenkins
  Scenario: Timeline events are displayed correctly on Service Channel front end
    Given an IT user has logged in
     When the "JobParked" timeline event is triggered
      And the "JobUnParked" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel
       
  #Scenario 3
  #Need to investigate why this is failing - will do when there is more time
  @wip
  Scenario: Timeline events are displayed correctly on Service Channel front end
    Given the TestAutomationSite exists 
      And an IT user has logged in
     When the "JobCancellationAdded" timeline event is triggered
      
  #Scenario 10
  #This scenario also covers Viewed event
  @serviceChannel_Jenkins
  Scenario: Timeline events are displayed correctly on Service Channel front end
    Given an IT user has logged in
     When the "Edited" timeline event is triggered
      And the "JobLinked" timeline event is triggered
      And the "JobUnlinked" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel
  
  #Scenario 29
  #Below test will trigger Resource Removed and Notified Timeline events
  @serviceChannel_Jenkins
  Scenario: Resource removed and notified
    Given an IT user has logged in
     When the "ResourceRemoved" timeline event is triggered 
     Then the "Create Work Order" action has been completed in Service Channel

  # Scenario 34
  @serviceChannel_Jenkins
  Scenario: Helpdesk Chase Timeline events
    Given a "Helpdesk Operator" has logged in
     When the "UpdateETAChase" timeline event is triggered
      And the "ETAChaseResolved" timeline event is triggered
      And the "ChaseComplaint" timeline event is triggered
      And the "ChaseWorksCompleteQuery" timeline event is triggered
      And the "ChaseWorksheetQuery" timeline event is triggered
      And the "ChasePartsEquipmentLeftOnSite" timeline event is triggered
      And the "ChaseManagerQuery" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel
     
  # Scenario 7
  @geolocation @serviceChannel_Jenkins
  Scenario: Job Linked/ Unlinked To Quote Job Timeline events
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
      And the user logs in
     When the "JobLinkedToQuoteJob" timeline event is triggered
      And the "JobUnlinkedFromQuoteJob" timeline event is triggered
     Then the "Create Work Order" action has been completed in Service Channel
