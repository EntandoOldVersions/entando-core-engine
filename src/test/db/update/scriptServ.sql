ALTER TABLE apicatalog_methods ALTER authorizationrequired TYPE character varying(30);
ALTER TABLE apicatalog_methods
  ADD CONSTRAINT apicatalog_methods_authorizationrequired_fkey FOREIGN KEY (authorizationrequired)
      REFERENCES authpermissions (permissionname);

ALTER TABLE apicatalog_methods ADD COLUMN ishidden smallint;
UPDATE apicatalog_methods SET ishidden = 0 WHERE ishidden IS NULL;
ALTER TABLE apicatalog_methods ALTER ishidden SET NOT NULL;
UPDATE apicatalog_methods SET isactive = 1 WHERE isactive IS NULL;
ALTER TABLE apicatalog_methods ALTER isactive SET NOT NULL;

ALTER TABLE apicatalog_services ADD COLUMN authenticationrequired smallint;

ALTER TABLE apicatalog_services ADD COLUMN requiredpermission character varying(30);
ALTER TABLE apicatalog_services
  ADD CONSTRAINT apicatalog_services_requiredpermission_fkey FOREIGN KEY (requiredpermission)
      REFERENCES authpermissions (permissionname);

ALTER TABLE apicatalog_services ADD COLUMN requiredgroup character varying(20);
ALTER TABLE apicatalog_services
  ADD CONSTRAINT apicatalog_services_requiredgroup_fkey FOREIGN KEY (requiredgroup)
      REFERENCES authgroups (groupname);

ALTER TABLE apicatalog_services ADD COLUMN ishidden smallint;
UPDATE apicatalog_services SET ishidden = 0 WHERE ispublic IS NULL;
UPDATE apicatalog_services SET ishidden = 1 WHERE ispublic = 0;
UPDATE apicatalog_services SET ishidden = 0 WHERE ispublic = 1;
ALTER TABLE apicatalog_methods ALTER ishidden SET NOT NULL;
ALTER TABLE apicatalog_services DROP COLUMN ispublic;



