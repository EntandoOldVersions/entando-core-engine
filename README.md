SQL notes

```sql 

-- sql - port database


CREATE TABLE contentattributeroles
(
  contentid character varying(16) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT contentattrroles_contid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE workcontentattributeroles
(
  contentid character varying(16) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT workcontentattrroles_contid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


-- for "Address book" plugin - Start
CREATE TABLE jpaddressbook_attroles
(
  contactkey character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT jpaddressbook_attroles_fkey FOREIGN KEY (contactkey)
      REFERENCES jpaddressbook_contacts (contactkey) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- for "Address book" plugin - End


-- for "User Profile" plugin - Start
CREATE TABLE jpuserprofile_attroles
(
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT jpuserprofile_attroles_fkey FOREIGN KEY (username)
      REFERENCES jpuserprofile_authuserprofiles (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- for "User Profile" plugin - End


-- for "Web Dynamic Form" plugin - Start
CREATE TABLE jpwebdynamicform_attroles
(
  messageid character varying(16) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT jpwebdynamicform_attroles_fkey FOREIGN KEY (messageid)
      REFERENCES jpwebdynamicform_messages (messageid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- for "Web Dynamic Form" plugin - End


-- for "jpsurvey" plugin - Start
ALTER TABLE jpsurvey ADD COLUMN checkusername smallint;
update jpsurvey SET checkusername = 0;
ALTER TABLE jpsurvey ALTER COLUMN checkusername SET NOT NULL;
-- for "jpsurvey" plugin - End

-- update localstrings

INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_CONFIGURATION','it','Configura il profile');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_CONFIGURATION','en','Edit profile');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_CONFIRM_NEWPASS','it','Conferma nuova password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_CONFIRM_NEWPASS','en','Confirm new password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_EDITPASSWORD','it','Modifica Password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_EDITPASSWORD','en','Edit Password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_EDITPASSWORD_TITLE','it','Modifica Password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_EDITPASSWORD_TITLE','en','Edit Password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_EDITPROFILE_TITLE','it','Modifica profilo');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_EDITPROFILE_TITLE','en','Edit Profile');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEUP','it','Sposta su');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEUP','en','Move up');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEUP_IN','it','Sposta su in posizione');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEUP_IN','en','Move at position');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEDOWN','it','Sposta giu');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEDOWN','en','Move down');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEDOWN_IN','it','Sposta giu in posizione');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_MOVEDOWN_IN','en','Move down at position');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_REMOVE','it','Rimuovi dalla lista');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ITEM_REMOVE','en','Remove from list');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_MESSAGE_TITLE_FIELDERRORS','it','Attenzione, si sono verificati i seguenti errori nella compilazione del modulo');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_MESSAGE_TITLE_FIELDERRORS','en','Warning, please check the module');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_NEWPASS','it','Nuova password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_NEWPASS','en','New password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_OLDPASSWORD','it','Vecchia password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_OLDPASSWORD','en','Old password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PASSWORD_UPDATED','it','La password è stata aggiornata correttamente.');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PASSWORD_UPDATED','en','Your password updated successfully.');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PLEASE_LOGIN','it','E'' necessario effettuare l''accesso');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PLEASE_LOGIN','en','Please login');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PLEASE_LOGIN_AGAIN','it','E'' necessario riloggarsi.');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PLEASE_LOGIN_AGAIN','en','Please logout and login again.');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PLEASE_LOGIN_TO_EDIT_PASSWORD','it','E'' necessario effettuare l''accesso per cambiare la password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PLEASE_LOGIN_TO_EDIT_PASSWORD','en','Please login in order to change your password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PROFILE_UPDATED','it','Profilo aggiornato correttamente.');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_PROFILE_UPDATED','en','Your profile is now updated.');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_SAVE_PASSWORD','it','Salva password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_SAVE_PASSWORD','en','Save password');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_SAVE_PROFILE','it','Salva il profilo');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_SAVE_PROFILE','en','Save profile');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ADDITEM_LIST','it','Aggiungi nuovo elemento alla lista');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ADDITEM_LIST','en','Add an element to the list');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_YES','it','Si');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_YES','en','Yes');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_NO','it','No');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_NO','en','No');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_BOTH_YES_AND_NO','it','Indifferente');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_BOTH_YES_AND_NO','en','Both');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MANDATORY_SHORT', 'it', '*');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MANDATORY_SHORT', 'en', '*');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MANDATORY_FULL', 'it', 'Obbligatorio');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MANDATORY_FULL', 'en', 'Mandatory');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MINLENGTH_SHORT', 'it', 'Min');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MINLENGTH_SHORT', 'en', 'Min');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MINLENGTH_FULL', 'it', 'Lunghezza Minima');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MINLENGTH_FULL', 'en', 'Minimum length');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MAXLENGTH_SHORT', 'it', 'Max');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MAXLENGTH_SHORT', 'en', 'Max');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MAXLENGTH_FULL', 'it', 'Lunghezza Massima');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_ENTITY_ATTRIBUTE_MAXLENGTH_FULL', 'en', 'Maximum length');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_CURRENT_USER_WITHOUT_PROFILE', 'it', 'Utente corrente senza profilo');
INSERT INTO localstrings (keycode, langcode, stringvalue) VALUES ('userprofile_CURRENT_USER_WITHOUT_PROFILE', 'en', 'Current user without profile');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_KEY','en','Key');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_KEY','it','Id');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARENT_API','en','Parent API');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARENT_API','it','API Padre');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTHORIZATION','en','Authorization');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTHORIZATION','it','Autorizzazione');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_FREE','en','Free');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_FREE','it','Accesso Libero');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_URI','en','URI');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_URI','it','URI');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAMETERS','en','Parameters');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAMETERS','it','Parametri');



-- transition from 'showlets' to 'widgets' - Start
ALTER TABLE showletcatalog RENAME TO widgetcatalog;
ALTER TABLE showletconfig RENAME COLUMN showletcode TO widgetcode;
ALTER TABLE showletconfig RENAME TO widgetconfig;

UPDATE sysconfig SET config = replace(config, '</SpecialPages>', '</SpecialPages>
  <FeaturesOnDemand>
    <Param name="groupsOnDemand">false</Param>
    <Param name="categoriesOnDemand">false</Param>
    <Param name="contentTypesOnDemand">false</Param>
    <Param name="contentModelsOnDemand">false</Param>
    <Param name="apisOnDemand">false</Param>
    <Param name="resourceArchivesOnDemand">false</Param>

  </FeaturesOnDemand>') WHERE item = 'params';

UPDATE sysconfig SET config = replace(config, '<Param name="startLangFromBrowser">false</Param>', '<Param name="startLangFromBrowser">false</Param>
  <Param name="firstTimeMessages">true</Param>') WHERE item = 'params';


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

-- sql - serv database

INSERT INTO authpermissions (permissionname, descr) VALUES ('viewUsers', 'View Users and Profiles');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUsers', 'User Editing');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUserProfile', 'User Profile Editing');

INSERT INTO authuserprofiles (username, profiletype, profilexml, publicprofile) VALUES ('admin', 'PFL', '<?xml version="1.0" encoding="UTF-8"?>
<profile id="admin" typecode="PFL" typedescr="Default user profile"><descr /><groups /><categories /><attributes><attribute name="fullname" attributetype="Monotext" /><attribute name="email" attributetype="Monotext" /></attributes></profile>
', 0);

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
      REFERENCES authuserprofiles (username)
);

CREATE TABLE authuserprofileattrroles (
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT authuserprofileattrroles_fkey FOREIGN KEY (username)
      REFERENCES authuserprofiles (username)
);
-- for "User Profile" plugin - END

-- actionlogrecords - START
INSERT INTO actionlogrecords (id, username, actiondate, namespace, actionname, parameters, activitystreaminfo) VALUES (1, 'admin', '2013-09-27 10:58:38', '/do/Page', 'save', 'selectedNode=service
model=service
strutsAction=1
extraGroupName=free
charset=
parentPageCode=service
defaultShowlet=true
copyPageCode=
langit=Accedi
groupSelectLock=false
langen=Sign In
group=free
mimeType=
pageCode=sign_in
', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<activityStreamInfo>
    <objectTitles>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">en</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">Sign In</value>
        </entry>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">it</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">Accedi</value>
        </entry>
    </objectTitles>
    <groups>
        <group>free</group>
    </groups>
    <actionType>1</actionType>
    <linkNamespace>/do/Page</linkNamespace>
    <linkActionName>edit</linkActionName>
    <linkParameters>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">selectedNode</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">sign_in</value>
        </entry>
    </linkParameters>
    <linkAuthPermission>managePages</linkAuthPermission>
    <linkAuthGroup>free</linkAuthGroup>
</activityStreamInfo>
');

INSERT INTO actionlogrecords (id, username, actiondate, namespace, actionname, parameters, activitystreaminfo) VALUES (2, 'admin', '2013-09-27 11:00:12', '/do/Page', 'save', 'selectedNode=service
model=service
strutsAction=1
extraGroupName=free
charset=
parentPageCode=service
defaultShowlet=true
copyPageCode=
langit=Pagina non trovata
groupSelectLock=false
langen=Page not found
group=free
mimeType=
pageCode=notfound
', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<activityStreamInfo>
    <objectTitles>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">en</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">Page not found</value>
        </entry>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">it</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">Pagina non trovata</value>
        </entry>
    </objectTitles>
    <groups>
        <group>free</group>
    </groups>
    <actionType>1</actionType>
    <linkNamespace>/do/Page</linkNamespace>
    <linkActionName>edit</linkActionName>
    <linkParameters>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">selectedNode</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">notfound</value>
        </entry>
    </linkParameters>
    <linkAuthPermission>managePages</linkAuthPermission>
    <linkAuthGroup>free</linkAuthGroup>
</activityStreamInfo>
');



INSERT INTO actionlogrecords (id, username, actiondate, namespace, actionname, parameters, activitystreaminfo) VALUES (3, 'admin', '2013-09-27 11:00:12', '/do/Page', 'save', 'selectedNode=service
model=service
strutsAction=1
extraGroupName=free
charset=
parentPageCode=service
defaultShowlet=true
copyPageCode=
langit=Errore di Sistema
groupSelectLock=false
langen=System Error
group=free
mimeType=
pageCode=errorpage
', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<activityStreamInfo>
    <objectTitles>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">en</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">System Error</value>
        </entry>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">it</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">Errore di Sistema</value>
        </entry>
    </objectTitles>
    <groups>
        <group>free</group>
    </groups>
    <actionType>1</actionType>
    <linkNamespace>/do/Page</linkNamespace>
    <linkActionName>edit</linkActionName>
    <linkParameters>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">selectedNode</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">errorpage</value>
        </entry>
    </linkParameters>
    <linkAuthPermission>managePages</linkAuthPermission>
    <linkAuthGroup>free</linkAuthGroup>
</activityStreamInfo>
');

INSERT INTO actionlogrelations (recordid, refgroup) VALUES (1, 'free');
INSERT INTO actionlogrelations (recordid, refgroup) VALUES (2, 'free');
INSERT INTO actionlogrelations (recordid, refgroup) VALUES (3, 'free');

```
