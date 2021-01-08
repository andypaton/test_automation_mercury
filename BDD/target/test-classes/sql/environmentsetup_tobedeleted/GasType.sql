USE [%testdb]
GO

SET IDENTITY_INSERT GasType ON; 
GO
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('24', 'Not in Plant with Asset', '1', '0', NULL, NULL, 'system', '2018-08-15 15:38:12.257', NULL, '2018-08-15 15:38:12.257', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('25', 'In Plant with Asset', '1', '0', NULL, NULL, 'system', '2018-08-15 15:38:12.257', NULL, '2018-08-15 15:38:12.257', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('26', 'In Plant without Asset', '1', '0', NULL, NULL, 'system', '2018-08-15 15:38:12.257', NULL, '2018-08-15 15:38:12.257', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('27', 'Not in Plant without Asset', '1', '0', NULL, NULL, 'system', '2018-08-15 15:38:12.257', NULL, '2018-08-15 15:38:12.257', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('28', 'FGAS Appliance', '1', '0', NULL, NULL, 'system', '2018-08-20 10:35:59.09', NULL, '2018-08-20 10:35:59.09', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('30', 'Refrigerant Source', '1', '0', NULL, NULL, 'system', '2018-08-24 11:09:06.21', NULL, '2018-08-24 11:09:06.21', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('32', 'Leak Check Questions', '1', '0', NULL, NULL, 'system', '2018-08-24 11:09:06.217', NULL, '2018-08-24 11:09:06.217', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('33', 'Leak Site Information', '1', '0', '', '1900-01-01 00:00:00.0', 'system', '2018-08-24 11:09:06.217', NULL, '2018-08-24 11:09:06.217', '1', '17');
insert into GasType (Id, Description, IsActive, IsFollowupRequired, LockingUser, LockingTime, Createdby, Createdon, Lastmodifiedby, Lastmodifiedon, IsActiveForLogging, ObjectTypeID) values ('35', 'Maximum Charge', '1', '0', '', '1900-01-01 00:00:00.0', 'system', '2018-08-24 11:09:06.217', '', '2018-08-24 11:09:06.217', '1', '17');
GO
SET IDENTITY_INSERT GasType OFF; 
GO