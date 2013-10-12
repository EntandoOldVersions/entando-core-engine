SQL notes

```sql

-- transition from 'showlets' to 'widgets' - Start
ALTER TABLE showletcatalog RENAME TO widgetcatalog;
ALTER TABLE showletconfig RENAME showletcode TO widgetcode;
ALTER TABLE showletconfig RENAME TO widgetconfig;

ALTER TABLE widgetconfig RENAME COLUMN showletcode to widgetcode;


--- Bundle widgets rename // start ---

--- For widget 'language_choose' // start ---
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('entando-widget-language_choose', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Choose a Language</property>
<property key="it">Choose a Language</property>
</properties>', NULL, NULL, NULL, NULL, 1, NULL);
UPDATE widgetconfig SET widgetcode= 'entando-widget-language_choose' WHERE widgetcode='entando-showlet-language_choose';
DELETE FROM widgetcatalog WHERE code='entando-showlet-language_choose';
UPDATE sysconfig SET config = replace(config, 'entando-showlet-language_choose', 'entando-widget-language_choose') WHERE item = 'entandoComponentsReport';
UPDATE pagemodels SET frames = replace(frames, 'entando-showlet-language_choose', 'entando-widget-language_choose');
--- For widget 'language_choose' // end ---

--- For widget 'login_form' // start ---
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('entando-widget-login_form', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Dropdown Sign In</property>
<property key="it">Dropdown Sign In</property>
</properties>', NULL, NULL, NULL, NULL, 1, NULL);
UPDATE widgetconfig SET widgetcode= 'entando-widget-login_form' WHERE widgetcode='entando-showlet-login_form';
DELETE FROM widgetcatalog WHERE code='entando-showlet-login_form';
UPDATE sysconfig SET config = replace(config, 'entando-showlet-login_form', 'entando-widget-login_form') WHERE item = 'entandoComponentsReport';
UPDATE pagemodels SET frames = replace(frames, 'entando-showlet-login_form', 'entando-widget-login_form');
--- For widget 'login_form' // end ---

--- For widget 'navigation_bar' // start ---
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('entando-widget-navigation_bar', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Navigation - Bar</property>
<property key="it">Navigazione - Barra Orizzontale</property>
</properties>', '<config>
    <parameter name="navSpec">Rules for the Page List auto-generation</parameter>
    <action name="navigatorConfig" />
</config>', NULL, NULL, NULL, 1, NULL);
UPDATE widgetconfig SET widgetcode= 'entando-widget-navigation_bar' WHERE widgetcode='entando-showlet-navigation_bar';
DELETE FROM widgetcatalog WHERE code='entando-showlet-navigation_bar';
UPDATE sysconfig SET config = replace(config, 'entando-showlet-navigation_bar', 'entando-widget-navigation_bar') WHERE item = 'entandoComponentsReport';
UPDATE pagemodels SET frames = replace(frames, 'entando-showlet-navigation_bar', 'entando-widget-navigation_bar');
--- For widget 'navigation_bar' // end ---

--- For widget 'navigation_breadcrumbs' // start ---
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('entando-widget-navigation_breadcrumbs', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Navigation - Breadcrumbs</property>
<property key="it">Navigazione - Briciole di Pane</property>
</properties>', NULL, NULL, NULL, NULL, 1, NULL);
UPDATE widgetconfig SET widgetcode= 'entando-widget-navigation_breadcrumbs' WHERE widgetcode='entando-showlet-navigation_breadcrumbs';
DELETE FROM widgetcatalog WHERE code='entando-showlet-navigation_breadcrumbs';
UPDATE sysconfig SET config = replace(config, 'entando-showlet-navigation_breadcrumbs', 'entando-widget-navigation_breadcrumbs') WHERE item = 'entandoComponentsReport';
UPDATE pagemodels SET frames = replace(frames, 'entando-showlet-navigation_breadcrumbs', 'entando-widget-navigation_breadcrumbs');
--- For widget 'navigation_breadcrumbs' // end ---

--- For widget 'navigation_menu' // start ---
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('entando-widget-navigation_menu', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Navigation - Vertical Menu</property>
<property key="it">Navigazione - Menù Verticale</property>
</properties>', '<config>
    <parameter name="navSpec">Rules for the Page List auto-generation</parameter>
    <action name="navigatorConfig" />
</config>', NULL, NULL, NULL, 1, NULL);
UPDATE widgetconfig SET widgetcode= 'entando-widget-navigation_menu' WHERE widgetcode='entando-showlet-navigation_menu';
DELETE FROM widgetcatalog WHERE code='entando-showlet-navigation_menu';
UPDATE sysconfig SET config = replace(config, 'entando-showlet-navigation_menu', 'entando-widget-navigation_menu') WHERE item = 'entandoComponentsReport';
UPDATE pagemodels SET frames = replace(frames, 'entando-showlet-navigation_menu', 'entando-widget-navigation_menu');
--- For widget 'navigation_menu' // end ---

--- For widget 'search_form' // start ---
INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('entando-widget-search_form', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Search Form</property>
<property key="it">Search Form</property>
</properties>', NULL, NULL, NULL, NULL, 1, NULL);
UPDATE widgetconfig SET widgetcode= 'entando-widget-search_form' WHERE widgetcode='entando-showlet-search_form';
DELETE FROM widgetcatalog WHERE code='entando-showlet-search_form';
UPDATE sysconfig SET config = replace(config, 'entando-showlet-search_form', 'entando-widget-search_form') WHERE item = 'entandoComponentsReport';
--- For widget 'search_form' // end ---

--- page model // start ---
UPDATE pagemodels SET frames = replace(frames, 'entando-showlet-search_form', 'entando-widget-search_form');

--- defaultWidget
UPDATE pagemodels SET frames = replace(frames, 'defaultShowlet', 'defaultWidget');

--- Bundle widgets rename // end ---

--- plugin My Portal configuration // start ---

UPDATE sysconfig SET config = replace(config, '<showlets>', '<widgets>') WHERE item = 'jpmyportalplus_config';
UPDATE sysconfig SET config = replace(config, '</showlets>', '</widgets>') WHERE item = 'jpmyportalplus_config';
UPDATE sysconfig SET config = replace(config, '<showlet', '<widget') WHERE item = 'jpmyportalplus_config';

--- plugin My Portal configuration // end ---

--- plugin Share with configuration // start ---

INSERT INTO widgetcatalog (code, titles, parameters, plugincode, parenttypecode, defaultconfig, locked, maingroup) VALUES ('entando-widget-sharewith', '<?xml version="1.0" encoding="UTF-8"?>
<properties>
<property key="en">Share With...</property>
<property key="it">Condividi con...</property>
</properties>', 
NULL, NULL, NULL, NULL, 1, NULL);
UPDATE widgetconfig SET widgetcode= 'entando-widget-sharewith' WHERE widgetcode='entando-showlet-sharewith';
DELETE FROM widgetcatalog WHERE code='entando-showlet-sharewith';
UPDATE sysconfig SET config = replace(config, 'entando-showlet-sharewith', 'entando-widget-sharewith') WHERE item = 'entandoComponentsReport';
UPDATE pagemodels SET frames = replace(frames, 'entando-showlet-sharewith', 'entando-widget-sharewith');

--- plugin Share with configuration // end ---


-- transition from 'showlets' to 'widgets' - End


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

-- IF "User Profile" plugin installed:
UPDATE sysconfig SET item = 'userProfileTypes' WHERE item = 'jpuserprofileProfileType';
UPDATE sysconfig SET config = replace(config, '<role>jpuserprofile:mail</role>', '<role>userprofile:email</role>') WHERE item = 'userProfileTypes';
UPDATE widgetconfig SET widgetcode= 'userprofile_editCurrentUser' WHERE widgetcode='jpuserprofile_editCurrentUser';
DELETE FROM widgetcatalog WHERE code='jpuserprofile_editCurrentUser';
UPDATE pagemodels SET frames = replace(frames, 'jpuserprofile_editCurrentUser', 'userprofile_editCurrentUser');

UPDATE widgetconfig SET widgetcode= 'userprofile_editCurrentUser_password' WHERE widgetcode='jpuserprofile_editCurrentUser_password';
DELETE FROM widgetcatalog WHERE code='jpuserprofile_editCurrentUser_password';
UPDATE pagemodels SET frames = replace(frames, 'jpuserprofile_editCurrentUser_password', 'userprofile_editCurrentUser_password');

UPDATE widgetconfig SET widgetcode= 'userprofile_editCurrentUser_profile' WHERE widgetcode='jpuserprofile_editCurrentUser_profile';
DELETE FROM widgetcatalog WHERE code='jpuserprofile_editCurrentUser_profile';
UPDATE pagemodels SET frames = replace(frames, 'jpuserprofile_editCurrentUser_profile', 'userprofile_editCurrentUser_profile');

-- IF "User Profile" plugin NOT installed:
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
					<role>userprofile:email</role>
				</roles>
			</attribute>
		</attributes>
	</profiletype>
</profiletypes>');

-- for "User Profile" plugin - END


```

```sql

INSERT INTO authpermissions (permissionname, descr) VALUES ('viewUsers', 'View Users and Profiles');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUsers', 'User Editing');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUserProfile', 'User Profile Editing');

CREATE TABLE actionlogrecords
(
  id integer NOT NULL,
  username character varying(20),
  actiondate timestamp without time zone,
  namespace text,
  actionname character varying(250),
  parameters text,
  activitystreaminfo text,
  CONSTRAINT actionlogrecords_pkey PRIMARY KEY (id )
);
CREATE TABLE actionlogrelations
(
  recordid integer NOT NULL,
  refgroup character varying(20),
  CONSTRAINT actionlogrelations_recid_fkey FOREIGN KEY (recordid)
      REFERENCES actionlogrecords (id)
);
CREATE TABLE actionloglikerecords
(
  recordid integer NOT NULL,
  username character varying(20) NOT NULL,
  likedate timestamp without time zone NOT NULL,
  CONSTRAINT actionloglikerec_recid_fkey FOREIGN KEY (recordid)
      REFERENCES actionlogrecords (id)
);

-- IF "User Profile" plugin installed:
ALTER TABLE jpuserprofile_authuserprofiles RENAME TO authuserprofiles;
ALTER TABLE jpuserprofile_profilesearch RENAME TO authuserprofilesearch;
ALTER TABLE jpuserprofile_attroles RENAME TO authuserprofileattrroles;
-- IF "User Profile" plugin NOT installed:
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




