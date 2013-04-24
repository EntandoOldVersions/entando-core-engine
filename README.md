```sql
UPDATE showletcatalog SET parameters = '<config>
  <parameter name="contentType">Content Type (mandatory)</parameter>
	<parameter name="modelId">Content Model</parameter>
	<parameter name="userFilters">Front-End user filter options</parameter>
	<parameter name="category">Content Category **deprecated**</parameter>
	<parameter name="categories">Content Category codes (comma separeted)</parameter>
	<parameter name="orClauseCategoryFilter" />
	<parameter name="maxElemForItem">Contents for each page</parameter>
	<parameter name="maxElements">Number of contents</parameter>
	<parameter name="filters" />
	<parameter name="title_{lang}">Showlet Title in lang {lang}</parameter>
	<parameter name="pageLink">The code of the Page to link</parameter>
	<parameter name="linkDescr_{lang}">Link description in lang {lang}</parameter>
	<action name="listViewerConfig"/>
</config>' WHERE code = 'content_viewer_list';

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_NO_SERVICES', 'it', 'Nessun Servizio API disponibile');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_NO_SERVICES', 'en', 'No API Service availables');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_NO_SERVICES', 'it', 'Nessun Servizio API disponibile');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_NO_SERVICES', 'en', 'No API Service availables');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_GOTO_SERVICE_LIST', 'it', 'Lista Servizi API');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_GOTO_SERVICE_LIST', 'en', 'API Service List');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICES', 'it', 'Servizi API');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICES', 'en', 'API Services');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE', 'it', 'Servizio API');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE', 'en', 'API Service');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARENT_API', 'it', 'Risorsa API');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARENT_API', 'en', 'Parent API Resource');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_KEY', 'it', 'Chiave');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_KEY', 'en', 'Key');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTHORIZATION', 'it', 'Autorizzazione');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTHORIZATION', 'en', 'Authority');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_FREE', 'it', 'Libera');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_FREE', 'en', 'Free');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_SIMPLE', 'it', 'Autenticazione');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_SIMPLE', 'en', 'Simple Autentication');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_WITH_PERM', 'it', 'Autenticazione con permesso');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_WITH_PERM', 'en', 'Authentication with permission');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_WITH_GROUP', 'it', 'Autenticazione con gruppo');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_AUTH_WITH_GROUP', 'en', 'Authentication with group');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_URI', 'it', 'Service URI');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_URI', 'en', 'Service URI');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_SCHEMAS', 'it', 'XML Schema');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_SCHEMAS', 'en', 'XML Schema');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_SCHEMA_RESP', 'it', 'Risposta');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_SCHEMA_RESP', 'en', 'Response');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAMETERS_SUMMARY', 'it', 'Summary..');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAMETERS_SUMMARY', 'en', 'Summary..');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAMETERS', 'it', 'Parametri');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAMETERS', 'en', 'Parameters');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_NAME', 'it', 'Nome');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_NAME', 'en', 'Name');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_DESCRIPTION', 'it', 'Descrizione');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_DESCRIPTION', 'en', 'Description');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_REQUIRED', 'it', 'Obbligatorio');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_REQUIRED', 'en', 'Required');

INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_DEFAULT_VALUE', 'it', 'Default');
INSERT INTO localstrings(keycode, langcode, stringvalue) VALUES ('ENTANDO_API_SERVICE_PARAM_DEFAULT_VALUE', 'en', 'Default');

--------------------

ALTER TABLE contentsearch ALTER COLUMN datevalue TYPE timestamp without time zone;

ALTER TABLE workcontentsearch ALTER COLUMN datevalue TYPE timestamp without time zone;
```

```sql
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

------

ALTER TABLE api_oauth_consumers ALTER COLUMN expirationdate TYPE timestamp without time zone;

ALTER TABLE api_oauth_tokens ALTER COLUMN accesstoken TYPE character varying(100);
ALTER TABLE api_oauth_tokens ALTER COLUMN lastaccess TYPE timestamp without time zone;

ALTER TABLE authusers ALTER COLUMN registrationdate TYPE timestamp without time zone;
ALTER TABLE authusers ALTER COLUMN lastaccess TYPE timestamp without time zone;
ALTER TABLE authusers ALTER COLUMN lastpasswordchange TYPE timestamp without time zone;

-- for "jpsurvey" plugin - Start
ALTER TABLE jpsurvey ADD COLUMN checkusername smallint;
update jpsurvey SET checkusername = 0;
ALTER TABLE jpsurvey ALTER COLUMN checkusername SET NOT NULL;
-- for "jpsurvey" plugin - End


```




