@serviceChannel
Feature: Service Channel

  #Below test will trigger - QuoteRequestRaised, QuoteRequestApproverSet, ResourcesInvitedToQuote, ItqAccepted, AwaitingApproval, JobQuoteApproved 
  #Scenario 5
  @serviceChannel_timelineEvents
  Scenario: Quotes Approved Timeline events 
     Given an IT user has logged in
      When the "JobQuoteApproved" timeline event is triggered
      
  #Scenario 8
  @serviceChannel_timelineEvents
  Scenario: Quotes Query Timeline events 
     Given an IT user has logged in
      When the "QuoteResourceQueried" timeline event is triggered
       And the "ResourceQueryResponse" timeline event is triggered
       
  #Scenario 9
  @serviceChannel_timelineEvents
  Scenario: Job Type Changed to Warranty Timeline event
     Given an IT user has logged in
      When the "JobTypeChangedToWarranty" timeline event is triggered 
      
  #Scenario 9
  @serviceChannel_timelineEvents
  Scenario: Job Type Changed to Reactive Timeline event
     Given a "RFM" with a "single" "Quote" in state "Awaiting Quote Request Review"
       And the user logs in 
      When the "JobTypeChangedToReactive" timeline event is triggered 
      
  #Scenario 12
  @serviceChannel_timelineEvents
  Scenario: Job linked/unlinked to Incident 
     Given an IT user has logged in
      When the "JobLinkedToIncident" timeline event is triggered 
       And the "JobUnlinkedFromIncident" timeline event is triggered    
       
  #Scenario 24
  @serviceChannel_timelineEvents
  Scenario: Declined Job
     Given an IT user has logged in
      When the "DeclinedJob" timeline event is triggered   
  
  #Scenario 29
  #Below test will trigger Resource Removed and Notified Timeline events
  @serviceChannel_timelineEvents
  Scenario: Resourse removed and notified
     Given an IT user has logged in
      When the "ResourceRemoved" timeline event is triggered       
  
  #Scenario 28
  @serviceChannel_timelineEvents
  Scenario: Removal Requested, Rejected and Additional Resource Assignment closed
     Given an IT user has logged in
      When the "RemovalRejected" timeline event is triggered
       And the "AdditionalResourceAssignmentClosed" timeline event is triggered  
       
  #Scenario 18
  #Below test will trigger ARR Removed, Job Resource Notified and Resource Funding Request Cancelled Timeline events
  @serviceChannel_timelineEvents
  Scenario: ARR Removed, Job Resource notified and Resource Funding Request Cancelled
     Given an IT user has logged in
      When the "AdditionalResourceRequiredRemoved" timeline event is triggered
      
  #Scenario 30
  @serviceChannel_timelineEvents
  Scenario: Cancelled Job and FaultCancelled
     Given an IT user has logged in
      When the "Cancelled" timeline event is triggered
      
  #Scenario 17
  @serviceChannel_timelineEvents
  Scenario: Outbound Call
     Given an IT user has logged in
      When the "OutboundCall" timeline event is triggered
      
  #Scenario 19
  @serviceChannel_timelineEvents
  Scenario: ETA Acknowledged
     Given an IT user has logged in
      When the "ETAAcknowledged" timeline event is triggered
      
  #Scenario 33
  @serviceChannel_timelineEvents
  Scenario: Work Transferred
     Given an IT user has logged in
      When the "WorkTransferred" timeline event is triggered   
      