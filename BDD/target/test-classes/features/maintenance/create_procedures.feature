@maintenance
Feature: create procedures in the Staging schema

  @MTA-306
  Scenario Outline: Create Staging procedure for "<procedure>"
    Given a resource file for procedure "<procedure>"
     When procedure is created
     Then the procedure returns results
    Examples: 
      | procedure                                      | 
      | UpdateUploadedInvoiceTemplate                  | 

