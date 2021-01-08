@serviceChannel
Feature: Service Channel

  # Scenario 25
  # This will also trigger - Awaiting Funding Authorisation
  @serviceChannel_timelineEvents
  Scenario: Funding Approved Timeline events
    Given an IT user has logged in
     When the "FundingApproved" timeline event is triggered
  
  # Scenario 26
  @serviceChannel_timelineEvents
  Scenario: Funding Declined Timeline events
    Given an IT user has logged in
     When the "FundingDeclined" timeline event is triggered
     
  # Scenario 6
  # This will also trigger - Parts Requested Onsite, ETA Created, Follow Up ETA Received
  @serviceChannel_timelineEvents
  Scenario: Parts Order Approved Timeline events
    Given a portal user with a "RFM" profile and with "reactive" "Parts Awaiting Approval" Jobs
      And the user logs in
     When the "PartsOrderApproved" timeline event is triggered
     
  # Scenario 35
  # This will trigger - Parts Requested Offsite, Resource Returning
  #@serviceChannel_timelineEvents
  Scenario: Parts Order Rejected Timeline events
    Given a portal user with a "RFM" profile and with "reactive" "Parts Requested Offsite" Jobs
      And the user logs in
     When the "PartsOrderRejected" timeline event is triggered
     
  # Scenario 16
  @serviceChannel_timelineEvents
  Scenario: Invoice Attachment Added/ Removed Timeline events
    Given an IT user has logged in
     When the "AttachmentAdded" timeline event is triggered
      And the "AttachmentRemoved" timeline event is triggered
      
  # Scenario 37
  # This will trigger - Extreme Weather Flag
  #@serviceChannel_timelineEvents
  Scenario: Extreme Weather Invoice Approved Timeline events
    Given an Invoice Approver with "Invoices Awaiting Approval" Jobs
      And the user logs in
     When the "ExtremeWeatherInvoiceApproved" timeline event is triggered
     
  # Scenario 7
  @geolocation
  Scenario: Job Linked/ Unlinked To Quote Job Timeline events
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
      And the user logs in
     When the "JobLinkedToQuoteJob" timeline event is triggered
      And the "JobUnlinkedFromQuoteJob" timeline event is triggered
     
  # Scenario 20
  # This will also trigger - MultiQuoteBypassRequested, QuoteRequiresFinalApproval, ResourceDeclinedInvitationToQuote
  Scenario: Quote Final Approver Rejected Timeline events
    Given a "Additional Final Approver" with a "multi" "Quote" in state "AwaitingBypassApproval"
      And the user logs in
     When the "QuoteFinalApproverRejected" timeline event is triggered
     
  # Scenario 38
  # This will also trigger - Alternative Quote Requested, ResourceQuoteRejectionEmailSent
  Scenario: Resource Quote Rejected and Email Timeline events
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review"
      And the user logs in
     When the "ResourceQuoteRejected" timeline event is triggered
     
  # Scenario 34
  Scenario: Helpdesk Chase Timeline events
    Given a "Helpdesk Operator" has logged in
     When the "UpdateETAChase" timeline event is triggered
      And the "ETAChaseResolved" timeline event is triggered
      And the "ChaseComplaint" timeline event is triggered
      And the "ChaseWorksCompleteQuery" timeline event is triggered
      And the "ChaseWorksheetQuery" timeline event is triggered
      And the "ChasePartsEquipmentLeftOnSite" timeline event is triggered
      And the "ChaseManagerQuery" timeline event is triggered
     
  # Scenario 42
  # This will also trigger - Assigned, Initial ETA Received, OnSiteConfirmed, AssignmentCompleted
  @geolocation
  Scenario: Job Status Changed Timeline events
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
      And the user logs in
     When the "JobStatusChanged" timeline event is triggered
     
  # Scenario 21
  Scenario: Funding Route Updated Timeline events
    Given a "RFM" with a "single" "Quote" in state "Quotes Awaiting Review" with a "OPEX" funding route less than budget
      And the user logs in
     When the "FundingRouteUpdated" timeline event is triggered
     
  # Scenario 27
  # This will also trigger - ETAUpdated
  Scenario: Callback / ETA Timeline events
    Given a "Helpdesk Operator" has logged in
     When the "CallbackRequired" timeline event is triggered
      And the "CallbackCompleted" timeline event is triggered     
      And the "SiteNotifiedOfETA" timeline event is triggered