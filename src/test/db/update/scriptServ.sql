ALTER TABLE apicatalog_methods ALTER authorizationrequired TYPE character varying(30);
ALTER TABLE apicatalog_methods
  ADD CONSTRAINT apicatalog_methods_authorizationrequired_fkey FOREIGN KEY (authorizationrequired)
      REFERENCES authpermissions (permissionname);

ALTER TABLE apicatalog_services ADD COLUMN authenticationrequired smallint;

ALTER TABLE apicatalog_services ADD COLUMN requiredpermission character varying(30);
ALTER TABLE apicatalog_services
  ADD CONSTRAINT apicatalog_services_requiredpermission_fkey FOREIGN KEY (requiredpermission)
      REFERENCES authpermissions (permissionname);

ALTER TABLE apicatalog_services ADD COLUMN requiredgroup character varying(20);
ALTER TABLE apicatalog_services
  ADD CONSTRAINT apicatalog_services_requiredgroup_fkey FOREIGN KEY (requiredgroup)
      REFERENCES authgroups (groupname);




