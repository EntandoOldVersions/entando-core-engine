INSERT INTO authgroups (groupname, descr) VALUES ('administrators', 'Administrators');
INSERT INTO authgroups (groupname, descr) VALUES ('free', 'Free Access');




INSERT INTO authpermissions (permissionname, descr) VALUES ('superuser', 'All functions');
INSERT INTO authpermissions (permissionname, descr) VALUES ('validateContents', 'Supervision of contents');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageResources', 'Operations on Resources');
INSERT INTO authpermissions (permissionname, descr) VALUES ('managePages', 'Operations on Pages');
INSERT INTO authpermissions (permissionname, descr) VALUES ('enterBackend', 'Access to Administration Area');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageCategories', 'Operations on Categories');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editContents', 'Content Editing');




INSERT INTO authroles (rolename, descr) VALUES ('admin', 'Administrator');




INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('admin', 'superuser');




INSERT INTO authusergroups (username, groupname) VALUES ('admin', 'administrators');




INSERT INTO authuserroles (username, rolename) VALUES ('admin', 'admin');




INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('admin', 'adminadmin', '2008-10-10', '2011-02-05', NULL, 1);




INSERT INTO authusershortcuts (username, config) VALUES ('admin', '<?xml version="1.0" encoding="UTF-8"?>
<shortcuts>
	<box pos="0">core.component.user.list</box>
	<box pos="1">core.component.categories</box>
	<box pos="2">core.component.labels.list</box>
	<box pos="3">jacms.content.new</box>
	<box pos="4">jacms.content.list</box>
	<box pos="5">jacms.contentType</box>
	<box pos="6">core.portal.pageTree</box>
	<box pos="7">core.portal.showletType</box>
	<box pos="8">core.tools.entities</box>
	<box pos="9">core.tools.setting</box>
</shortcuts>

');