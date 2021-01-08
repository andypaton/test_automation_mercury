@portal @portal_invoices @portal_invoices_supplier_orders_awaiting_invoice
@toggles @Invoicing @MandatoryInvoiceGeneral @InvoicingLineFulfilled 
@ukrb
Feature: Portal - Invoices - Supplier Orders Awaiting Invoice

Background: 
   Given the system feature toggle "Invoicing" is "enabled" 
     And the system sub feature toggle "MandatoryInvoiceGeneral" is "enabled" 
     And the system sub feature toggle "InvoicingLineFulfilled" is "enabled"

  #@MP7-925 @smoke - add smoke tag once supply only job creation issue is fixed. 
  @grid
  Scenario: Change the fulfillment status of an order that has been added
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
      And the "invoice" page is displayed
     When the order line is added to the invoice
      And the user clicks edit on an order line
      And the user edits the invoice line fulfillment status
     Then the line fulfillment status is updated
     
  #@MP7-621
  Scenario: Submit an invoice for an order awaiting invoice with supplier profile - existing invoices
    Given a portal user with a "Supply Only" profile and with "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice" Jobs
      And the user logs in
      And the "Orders Awaiting Invoice" sub menu is selected from the "Invoices and Credits" top menu
      And the "Supplier Orders Awaiting Invoice" is searched for and opened
      And the "invoice" page is displayed
     When the order line is added to the invoice  
      And the user submits the invoice
     Then the user is taken to the invoice submitted page