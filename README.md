SQL notes

```sql - port database

-- transition from 'showlets' to 'widgets' - Start
ALTER TABLE showletcatalog RENAME TO widgetcatalog;
ALTER TABLE showletconfig RENAME showletcode TO widgetcode;
ALTER TABLE showletconfig RENAME TO widgetconfig;

ALTER TABLE widgetconfig RENAME COLUMN showletcode to widgetcode;
-- transition from 'showlets' to 'widgets' - End


-- for "User Profile" plugin - Start
UPDATE sysconfig SET item = 'userProfileTypes' WHERE item = 'jpuserprofileProfileType';
UPDATE widgetcatalog SET item = 'userprofile_editCurrentUser' WHERE item = 'jpuserprofile_editCurrentUser';
UPDATE widgetcatalog SET item = 'userprofile_editCurrentUser_password' WHERE item = 'jpuserprofile_editCurrentUser_password';
UPDATE widgetcatalog SET item = 'userprofile_editCurrentUser_profile' WHERE item = 'jpuserprofile_editCurrentUser_profile';
-- OR
INSERT INTO sysconfig (version, item, descr, config) values ('production', 'userProfileTypes', 'User Profile Types Definitions', '<?xml version="1.0" encoding="UTF-8"?>
<profiletypes>
	<profiletype typecode="PFL" typedescr="Default user profile">
		<attributes>
			<attribute name="fullname" attributetype="Monotext" searcheable="true">
				<validations>
					<required>true</required>
				</validations>
				<roles>
					<role>userprofile:fullname</role>
				</roles>
			</attribute>
			<attribute name="email" attributetype="Monotext" searcheable="true">
				<validations>
					<required>true</required>
					<regexp><![CDATA[.+@.+.[a-z]+]]></regexp>
				</validations>
				<roles>
					<role>userprofile:mail</role>
				</roles>
			</attribute>
		</attributes>
	</profiletype>
</profiletypes>');
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked) VALUES ('userprofile_editCurrentUser', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Edit Current User</property>
<property key="it">Edita Utente Corrente</property>
</properties>', NULL, NULL, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/ExtStr2/do/Front/CurrentUser/edit.action</property>
</properties>', 1);
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked) VALUES ('userprofile_editCurrentUser_password', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Edit Current User Password</property>
<property key="it">Edita Password Utente Corrente</property>
</properties>', NULL, NULL, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/ExtStr2/do/Front/CurrentUser/editPassword.action</property>
</properties>', 1);
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked) VALUES ('userprofile_editCurrentUser_profile', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Edit Current User Profile</property>
<property key="it">Edita Profilo Utente Corrente</property>
</properties>', NULL, NULL, 'formAction', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="actionPath">/ExtStr2/do/Front/CurrentUser/Profile/edit.action</property>
</properties>', 1);
-- for "User Profile" plugin - END


```

```sql - serv database

INSERT INTO authpermissions (permissionname, descr) VALUES ('viewUsers', 'View Users and Profiles');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUsers', 'User Editing');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUserProfile', 'User Profile Editing');

-- for "User Profile" plugin - Start
ALTER TABLE jpuserprofile_authuserprofiles RENAME TO authuserprofiles;
ALTER TABLE jpuserprofile_profilesearch RENAME TO userprofile_profilesearch;
ALTER TABLE jpuserprofile_attroles RENAME TO userprofile_attroles;
-- OR
CREATE TABLE authuserprofiles (
  username character varying(40) NOT NULL,
  profiletype character varying(30) NOT NULL,
  profilexml text NOT NULL,
  publicprofile smallint NOT NULL,
  CONSTRAINT authuserprofiles_pkey PRIMARY KEY (username )
);

CREATE TABLE authuserprofilesearch (
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  textvalue character varying(255),
  datevalue timestamp without time zone,
  numvalue integer,
  langcode character varying(3),
  CONSTRAINT authuserprofilesearch_fkey FOREIGN KEY (username)
      REFERENCES authuserprofiles (username);
);

CREATE TABLE authuserprofileattrroles (
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT authuserprofileattrroles_fkey FOREIGN KEY (username)
      REFERENCES authuserprofiles (username);
);
-- for "User Profile" plugin - END

```




