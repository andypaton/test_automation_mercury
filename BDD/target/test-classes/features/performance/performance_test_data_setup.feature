@performance @adhoc
Feature: Setup Test Data

  Scenario: Setup data for View, Accept and ETA - Contractor Admin
     When a list of "Contractor Admins"
     Then "View, Accept and ETA - Contractor Admin" jobs are created "2" times
     
  Scenario: Setup data for Update Job - Contractor Admin No Tech
     When a list of "Contractor Admins without Techs"
     Then "Update Job - Contractor Admin No Tech" jobs are created "2" times
  
  Scenario: Setup data for Start Work - Contractor Tech
     When a list of "Contractor Techs"
     Then "Start Work - Contractor Tech" jobs are created "2" times
     
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Setup data for Approve Funding Request - RFM
     When a list of "Contractor Admins"
     Then "Approve Funding Request - RFM" jobs are created "2" times
     
  Scenario: Setup data for Close Job - Contractor Admin
     When a list of "Contractor Techs"
     Then "Close Job - Contractor Admin" jobs are created "2" times     
     
     #    Given system property "numberOfJobsRequired" is "3"
#      And system property "assignToResourceIds" is "12845, 12846, 12848"
      
      
  @perfData1
  Scenario: Setup data for View, Accept and ETA - Contractor Admin
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToNumResources"
      And the job is to be assigned to a "Contractor Admin"
      And the list of resourceIds are identified
     When "View, Accept and ETA - Contractor Admin" jobs are created for the number requested
     Then the number of jobs created is verified

  @perfData2
  Scenario: Setup data for Update Job - Contractor Admin No Tech
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToResourceIds"
      And the job is to be assigned to a "Contractor Admin"
      And the list of resourceIds are identified
     When "Update Job - Contractor Admin No Tech" jobs are created for the number requested
     Then the number of jobs created is verified
     
  @perfData3
  Scenario: Setup data for Start Work - Contractor Tech
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToResourceIds"
      And the job is to be assigned to a "Contractor Technician"
      And the list of resourceIds are identified
     When "Start Work - Contractor Tech" jobs are created for the number requested
     Then the number of jobs created is verified
     
  @perfData4
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Setup data for Approved Funding Request - RFM
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToSiteIds"
      And the job is to be assigned to a "Contractor Admin"
      And the list of resourceIds are identified
     When "Approved Funding Request - RFM" jobs are created for the number requested
     Then the number of jobs created is verified
     
  @perfData5
  Scenario: Setup data for Close Job - Contractor Admin
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToResourceIds"
      And the job is to be assigned to a "Contractor Technician"
      And the list of resourceIds are identified
     When "Close Job - Contractor Admin" jobs are created for the number requested
     Then the number of jobs created is verified
     
  @perfData6
  Scenario: Setup data for Complete / Orders Awaiting Invoice With No Invoice - Contractor Admin
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToResourceIds"
      And the job is to be assigned to a "Contractor Admin"
      And the list of resourceIds are identified
     When "Complete / Orders Awaiting Invoice With No Invoice" jobs are created for the number requested
     Then the number of jobs created is verified
     
  @perfData7
  Scenario: Setup data for Update Job - City Tech
#     Given system property "numberOfJobsRequired" is "10"
#       And system property "assignToResourceIds" is "11512,11524,11528,11529,11531,11535,11536,11537,11549,11552,11557,11569,11582,11583,11588,11589,11592,11597,11598,11602,11604,11605,11607,11611,11617,11620,11621,11623,11624,11625,11628,11629,11631,11635,11636,11639,11644,11645,11648,11649,11651,11656,11658,11662,11665,11668,11674,11682,11683,11691"
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToResourceIds"
      And the job is to be assigned to a "City Technician"
      And the list of resourceIds are identified
     When "Update Job - City Tech" jobs are created for the number requested
     Then the number of jobs created is verified   
     
  @perfData8
  Scenario: Add invoices to the accounts payable carousel
#    Given system property "numInvoicesToUpload" is "2"
    Given a system property for "numInvoicesToUpload"
     When the invoice PDFs are uploaded to the carousel
     Then all uploaded invoice PDFs are on the carousel
     
  @perfData9
  Scenario: Import Purchase Orders
#     Given system property "numPurchaseOrdersToImport" is "1"
#      And system property "numSuppliers" is "20"
    Given a system property for "numPurchaseOrdersToImport"
      And a system property for "numSuppliers"
      And a "IT" user has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Import Configuration" tile is selected
      And the user selects "Purchase Order Configuration" from the sub menu
     When purchase order CSV files are created
      And the Purchase Order CSV files are uploaded, processed and imported
     Then the imported purchase orders are in the database

  @perfData10
  Scenario: Setup data for Approve Quote - RFM
#     Given system property "numberOfJobsRequired" is "2"
#      And system property "assignToSiteIds" is "1,2"
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToSiteIds"
      And the job is to be assigned to a "City Resource"
      And the list of resourceIds are identified
     When "Quotes Awaiting Review" jobs are created for the number requested
     Then the number of jobs created is verified 
     
  @perfData11
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Setup data for Approve Funding Request - RFM
#     Given system property "numberOfJobsRequired" is "3"
#       And system property "assignToSiteIds" is "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToSiteIds"
      And the job is to be assigned to a "City Resource"
      And the list of resourceIds are identified
     When "Parts Awaiting Approval" jobs are created for the number requested
     Then the number of jobs created is verified
     
  @perfData12
  Scenario: Setup data for Funding Request Awaiting Approval- RFM
#     Given system property "numberOfJobsRequired" is "1"
#       And system property "assignToSiteIds" is "5"    
    Given a system property for "numberOfJobsRequired"
      And a system property for "assignToSiteIds"
      And the job is to be assigned to a "Contractor Admin"
      And the list of resourceIds are identified
     When "Funding Request Awaiting Approval - RFM" jobs are created for the number requested
     Then the number of jobs created is verified
     