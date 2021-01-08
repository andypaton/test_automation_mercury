IF EXISTS (
Select * FROM sys.Objects
where name =('usp_Update_InvoiceTemplateCoordinatesInvoicing') AND type = 'P'
)
EXEC ('DROP PROCEDURE [UAT_USWM].[usp_Update_InvoiceTemplateCoordinatesInvoicing]')
--
EXEC('

CREATE Procedure  [UAT_USWM].[usp_Update_InvoiceTemplateCoordinatesInvoicing]
(
@templateId int,
@taxWord VARCHAR(50)
)
AS
BEGIN
  update %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 76 , fld_int_Y1 = 77 , fld_int_Width = 988, fld_int_Height = 36
  where fld_str_FieldName = 'Legal Entity' and fld_int_InvoiceTemplateID = @templateId

  update %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 74 , fld_int_Y1 = 123 , fld_int_Width = 992, fld_int_Height = 36
  where fld_str_FieldName = 'Invoice Number' and fld_int_InvoiceTemplateID = @templateId

  update %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 76 , fld_int_Y1 = 171 , fld_int_Width = 992, fld_int_Height = 31
  where fld_str_FieldName = 'Invoice Date' and fld_int_InvoiceTemplateID = @templateId

  update %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 76 , fld_int_Y1 = 214, fld_int_Width = 988, fld_int_Height = 34
  where fld_str_FieldName = 'Net Amount' and fld_int_InvoiceTemplateID = @templateId

  update %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 76 , fld_int_Y1 = 255 , fld_int_Width = 992, fld_int_Height = 41
  where fld_str_FieldName = CONCAT(@taxWord, ' Amount') and fld_int_InvoiceTemplateID = @templateId

  update %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 79 , fld_int_Y1 = 301 , fld_int_Width = 988, fld_int_Height = 38
  where fld_str_FieldName = 'Gross Amount' and fld_int_InvoiceTemplateID = @templateId

END


')
--GO
