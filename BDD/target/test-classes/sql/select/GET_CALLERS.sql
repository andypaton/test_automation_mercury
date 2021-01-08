 SELECT  
   c.Id, 
   cc.Name, cc.Department, cc.PhoneNumber, cc.Extension, cc.JobTitle AS JobRole, 
   ct.Name AS CallerType, 
   s.name AS SiteName, s.Id AS SiteId 
   FROM Caller c 
   INNER JOIN CallerType ct ON c.CallerTypeId = ct.Id 
   INNER JOIN ClientCaller cc ON c.Id = cc.Id 
   INNER JOIN Site s ON cc.SiteId = s.Id 
   WHERE c.CallerTypeId = 1 
 UNION 
 SELECT  
    c.Id, 
    r.Name, 
    rp.name AS Department, 
    rpn.PhoneNumber, NULL AS Extension, NULL AS JobRole, 
    ct.Name AS CallerType, 
    s.name AS SiteName, s.Id AS SiteId 
   FROM Caller c 
   INNER JOIN CallerType ct ON c.CallerTypeId = ct.Id 
   INNER JOIN ResourceCaller rc ON c.Id = rc.Id 
   INNER JOIN Resource r ON rc.ResourceId = r.Id 
   INNER JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId 
   INNER JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id 
   INNER JOIN SiteResource sr ON sr.ResourceId = r.Id 
   INNER JOIN Site s ON sr.SiteId = s.Id 
   WHERE c.CallerTypeId = 2 