--
-- PostgreSQL database dump
--

-- Started on 2012-09-28 22:14:55 CEST

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- TOC entry 1811 (class 0 OID 6028873)
-- Dependencies: 135
-- Data for Name: api_oauth_consumers; Type: TABLE DATA; Schema: public; Owner: agile
--



--
-- TOC entry 1812 (class 0 OID 6028881)
-- Dependencies: 136
-- Data for Name: api_oauth_tokens; Type: TABLE DATA; Schema: public; Owner: agile
--



--
-- TOC entry 1813 (class 0 OID 6028886)
-- Dependencies: 137
-- Data for Name: apicatalog_methods; Type: TABLE DATA; Schema: public; Owner: agile
--



--
-- TOC entry 1814 (class 0 OID 6028896)
-- Dependencies: 138
-- Data for Name: apicatalog_services; Type: TABLE DATA; Schema: public; Owner: agile
--



--
-- TOC entry 1803 (class 0 OID 6028816)
-- Dependencies: 127
-- Data for Name: authgroups; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authgroups (groupname, descr) VALUES ('administrators', 'Amministratori');
INSERT INTO authgroups (groupname, descr) VALUES ('coach', 'Coach');
INSERT INTO authgroups (groupname, descr) VALUES ('customers', 'Customers');
INSERT INTO authgroups (groupname, descr) VALUES ('free', 'Accesso Libero');
INSERT INTO authgroups (groupname, descr) VALUES ('helpdesk', 'Helpdesk');
INSERT INTO authgroups (groupname, descr) VALUES ('management', 'Management');


--
-- TOC entry 1804 (class 0 OID 6028821)
-- Dependencies: 128
-- Data for Name: authpermissions; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authpermissions (permissionname, descr) VALUES ('managePages', 'Operazioni su Pagine');
INSERT INTO authpermissions (permissionname, descr) VALUES ('enterBackend', 'Accesso all''Area di Amministrazione');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageResources', 'Operazioni su Risorse');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editContents', 'Redazione di Contenuti');
INSERT INTO authpermissions (permissionname, descr) VALUES ('validateContents', 'Supervisione di Contenuti');
INSERT INTO authpermissions (permissionname, descr) VALUES ('superuser', 'Tutte le funzioni');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageCategories', 'Operazioni su Categorie');


--
-- TOC entry 1806 (class 0 OID 6028831)
-- Dependencies: 130
-- Data for Name: authrolepermissions; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('admin', 'superuser');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('pageManager', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('supervisor', 'enterBackend');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('pageManager', 'managePages');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('supervisor', 'editContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'editContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('supervisor', 'validateContents');
INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('editor', 'manageResources');


--
-- TOC entry 1805 (class 0 OID 6028826)
-- Dependencies: 129
-- Data for Name: authroles; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authroles (rolename, descr) VALUES ('admin', 'Tutte le funzioni');
INSERT INTO authroles (rolename, descr) VALUES ('editor', 'Gestore di Contenuti e Risorse');
INSERT INTO authroles (rolename, descr) VALUES ('supervisor', 'Supervisore di Contenuti');
INSERT INTO authroles (rolename, descr) VALUES ('pageManager', 'Gestore di Pagine');


--
-- TOC entry 1808 (class 0 OID 6028849)
-- Dependencies: 132
-- Data for Name: authusergroups; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authusergroups (username, groupname) VALUES ('pageManagerCoach', 'coach');
INSERT INTO authusergroups (username, groupname) VALUES ('pageManagerCustomers', 'customers');
INSERT INTO authusergroups (username, groupname) VALUES ('supervisorCoach', 'coach');
INSERT INTO authusergroups (username, groupname) VALUES ('supervisorCustomers', 'customers');
INSERT INTO authusergroups (username, groupname) VALUES ('editorCoach', 'coach');
INSERT INTO authusergroups (username, groupname) VALUES ('editorCustomers', 'customers');
INSERT INTO authusergroups (username, groupname) VALUES ('supervisorCoach', 'customers');
INSERT INTO authusergroups (username, groupname) VALUES ('editorCoach', 'customers');
INSERT INTO authusergroups (username, groupname) VALUES ('mainEditor', 'administrators');
INSERT INTO authusergroups (username, groupname) VALUES ('pageManagerCoach', 'customers');
INSERT INTO authusergroups (username, groupname) VALUES ('admin', 'administrators');


--
-- TOC entry 1809 (class 0 OID 6028857)
-- Dependencies: 133
-- Data for Name: authuserroles; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authuserroles (username, rolename) VALUES ('admin', 'admin');
INSERT INTO authuserroles (username, rolename) VALUES ('editorCoach', 'editor');
INSERT INTO authuserroles (username, rolename) VALUES ('editorCustomers', 'editor');
INSERT INTO authuserroles (username, rolename) VALUES ('mainEditor', 'editor');
INSERT INTO authuserroles (username, rolename) VALUES ('supervisorCoach', 'supervisor');
INSERT INTO authuserroles (username, rolename) VALUES ('supervisorCustomers', 'supervisor');
INSERT INTO authuserroles (username, rolename) VALUES ('pageManagerCoach', 'pageManager');
INSERT INTO authuserroles (username, rolename) VALUES ('pageManagerCustomers', 'pageManager');


--
-- TOC entry 1807 (class 0 OID 6028844)
-- Dependencies: 131
-- Data for Name: authusers; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('supervisorCoach', 'supervisorCoach', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('mainEditor', 'mainEditor', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('pageManagerCoach', 'pageManagerCoach', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('supervisorCustomers', 'supervisorCustomers', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('pageManagerCustomers', 'pageManagerCustomers', '2008-09-25 00:00:00', '2009-01-30 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('editorCustomers', 'editorCustomers', '2008-09-25 00:00:00', '2009-07-02 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('editorCoach', 'editorCoach', '2008-09-25 00:00:00', '2009-07-02 00:00:00', NULL, 1);
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('admin', 'admin', '2008-09-25 00:00:00', '2009-12-16 00:00:00', NULL, 1);


--
-- TOC entry 1810 (class 0 OID 6028865)
-- Dependencies: 134
-- Data for Name: authusershortcuts; Type: TABLE DATA; Schema: public; Owner: agile
--

INSERT INTO authusershortcuts (username, config) VALUES ('admin', '<shortcuts>
	<box pos="0"></box>
	<box pos="1">core.component.user.list</box>
	<box pos="2">jacms.content.new</box>
	<box pos="3">jacms.content.list</box>
	<box pos="4">core.portal.pageTree</box>
	<box pos="5">core.portal.showletType</box>
	<box pos="6">core.tools.setting</box>
	<box pos="7">core.tools.entities</box>
</shortcuts>');


-- Completed on 2012-09-28 22:14:55 CEST

--
-- PostgreSQL database dump complete
--

