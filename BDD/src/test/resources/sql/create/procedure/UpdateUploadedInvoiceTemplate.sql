IF EXISTS (
Select * FROM sys.Objects
where name =('usp_Update_InvoiceTemplateCoordinates') AND type = 'P'
)
EXEC ('DROP PROCEDURE [staging].[usp_Update_InvoiceTemplateCoordinates]')
--
EXEC('

CREATE Procedure  [staging].[usp_Update_InvoiceTemplateCoordinates]
(
@templateId int
)
AS
BEGIN
  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 69 , fld_int_Y1 = 287 , fld_int_Width = 149, fld_int_Height = 84
  where fld_str_FieldName = ''Legal Entity'' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 269 , fld_int_Y1 = 152 , fld_int_Width = 343, fld_int_Height = 50
  where fld_str_FieldName = ''Invoice Number'' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 71 , fld_int_Y1 = 154 , fld_int_Width = 196, fld_int_Height = 59
  where fld_str_FieldName = ''Invoice Date'' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 1023 , fld_int_Y1 = 1048, fld_int_Width = 130, fld_int_Height = 48
  where fld_str_FieldName = ''Net Amount'' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 1021 , fld_int_Y1 = 1107 , fld_int_Width = 125, fld_int_Height = 36
  where fld_str_FieldName = ''Tax Amount'' and fld_int_InvoiceTemplateID = @templateId

  update  %portaldb.tblInvoiceTemplateField
  set fld_int_X1 = 1021 , fld_int_Y1 = 1208 , fld_int_Width = 127, fld_int_Height = 41
  where fld_str_FieldName = ''Gross Amount'' and fld_int_InvoiceTemplateID = @templateId

END


')
--GO

