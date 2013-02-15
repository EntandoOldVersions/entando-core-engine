--
-- PostgreSQL database dump
--

-- Started on 2012-09-28 22:14:54 CEST

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 460 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 135 (class 1259 OID 6028873)
-- Dependencies: 3
-- Name: api_oauth_consumers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE api_oauth_consumers (
    consumerkey character varying(100) NOT NULL,
    consumersecret character varying(100) NOT NULL,
    description text NOT NULL,
    callbackurl text,
    expirationdate timestamp without time zone
);


--
-- TOC entry 136 (class 1259 OID 6028881)
-- Dependencies: 3
-- Name: api_oauth_tokens; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE api_oauth_tokens (
    accesstoken character varying(100) NOT NULL,
    tokensecret character varying(100) NOT NULL,
    consumerkey character varying(100) NOT NULL,
    lastaccess timestamp without time zone NOT NULL,
    username character varying(40) NOT NULL
);


--
-- TOC entry 137 (class 1259 OID 6028886)
-- Dependencies: 3
-- Name: apicatalog_methods; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE apicatalog_methods (
    resource character varying(100) NOT NULL,
    httpmethod character varying(6) NOT NULL,
    isactive smallint NOT NULL,
    ishidden smallint NOT NULL,
    authenticationrequired smallint,
    authorizationrequired character varying(30)
);


--
-- TOC entry 138 (class 1259 OID 6028896)
-- Dependencies: 3
-- Name: apicatalog_services; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE apicatalog_services (
    servicekey character varying(100) NOT NULL,
    resource character varying(100) NOT NULL,
    description text NOT NULL,
    parameters text,
    tag character varying(100),
    freeparameters text,
    isactive smallint NOT NULL,
    ishidden smallint NOT NULL,
    myentando smallint NOT NULL,
    authenticationrequired smallint,
    requiredpermission character varying(30),
    requiredgroup character varying(20)
);


--
-- TOC entry 127 (class 1259 OID 6028816)
-- Dependencies: 3
-- Name: authgroups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authgroups (
    groupname character varying(20) NOT NULL,
    descr character varying(50) NOT NULL
);


--
-- TOC entry 128 (class 1259 OID 6028821)
-- Dependencies: 3
-- Name: authpermissions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authpermissions (
    permissionname character varying(30) NOT NULL,
    descr character varying(50) NOT NULL
);


--
-- TOC entry 130 (class 1259 OID 6028831)
-- Dependencies: 3
-- Name: authrolepermissions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authrolepermissions (
    rolename character varying(30) NOT NULL,
    permissionname character varying(30) NOT NULL
);


--
-- TOC entry 129 (class 1259 OID 6028826)
-- Dependencies: 3
-- Name: authroles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authroles (
    rolename character varying(20) NOT NULL,
    descr character varying(50) NOT NULL
);


--
-- TOC entry 132 (class 1259 OID 6028849)
-- Dependencies: 3
-- Name: authusergroups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusergroups (
    username character varying(40) NOT NULL,
    groupname character varying(20) NOT NULL
);


--
-- TOC entry 133 (class 1259 OID 6028857)
-- Dependencies: 3
-- Name: authuserroles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authuserroles (
    username character varying(40) NOT NULL,
    rolename character varying(20) NOT NULL
);


--
-- TOC entry 131 (class 1259 OID 6028844)
-- Dependencies: 3
-- Name: authusers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusers (
    username character varying(40) NOT NULL,
    passwd character varying(40),
    registrationdate timestamp without time zone NOT NULL,
    lastaccess timestamp without time zone,
    lastpasswordchange timestamp without time zone,
    active smallint
);


--
-- TOC entry 134 (class 1259 OID 6028865)
-- Dependencies: 3
-- Name: authusershortcuts; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusershortcuts (
    username character varying(40) NOT NULL,
    config text NOT NULL
);


--
-- TOC entry 1789 (class 2606 OID 6028880)
-- Dependencies: 135 135
-- Name: api_oauth_consumers_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY api_oauth_consumers
    ADD CONSTRAINT api_oauth_consumers_pkey PRIMARY KEY (consumerkey);


--
-- TOC entry 1791 (class 2606 OID 6028885)
-- Dependencies: 136 136
-- Name: api_oauth_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY api_oauth_tokens
    ADD CONSTRAINT api_oauth_tokens_pkey PRIMARY KEY (accesstoken);


--
-- TOC entry 1793 (class 2606 OID 6028890)
-- Dependencies: 137 137 137
-- Name: apicatalog_methods_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_methods
    ADD CONSTRAINT apicatalog_methods_pkey PRIMARY KEY (resource, httpmethod);


--
-- TOC entry 1795 (class 2606 OID 6028903)
-- Dependencies: 138 138
-- Name: apicatalog_services_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_services
    ADD CONSTRAINT apicatalog_services_pkey PRIMARY KEY (servicekey);


--
-- TOC entry 1779 (class 2606 OID 6028820)
-- Dependencies: 127 127
-- Name: authgroups_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authgroups
    ADD CONSTRAINT authgroups_pkey PRIMARY KEY (groupname);


--
-- TOC entry 1781 (class 2606 OID 6028825)
-- Dependencies: 128 128
-- Name: authpermissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authpermissions
    ADD CONSTRAINT authpermissions_pkey PRIMARY KEY (permissionname);


--
-- TOC entry 1783 (class 2606 OID 6028830)
-- Dependencies: 129 129
-- Name: authroles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authroles
    ADD CONSTRAINT authroles_pkey PRIMARY KEY (rolename);


--
-- TOC entry 1785 (class 2606 OID 6028848)
-- Dependencies: 131 131
-- Name: authusers_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusers
    ADD CONSTRAINT authusers_pkey PRIMARY KEY (username);


--
-- TOC entry 1787 (class 2606 OID 6028872)
-- Dependencies: 134 134
-- Name: authusershortcuts_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusershortcuts
    ADD CONSTRAINT authusershortcuts_pkey PRIMARY KEY (username);


--
-- TOC entry 1800 (class 2606 OID 6028891)
-- Dependencies: 1780 137 128
-- Name: apicatalog_methods_authorizationrequired_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY apicatalog_methods
    ADD CONSTRAINT apicatalog_methods_authorizationrequired_fkey FOREIGN KEY (authorizationrequired) REFERENCES authpermissions(permissionname);


--
-- TOC entry 1801 (class 2606 OID 6028904)
-- Dependencies: 127 138 1778
-- Name: apicatalog_services_requiredgroup_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY apicatalog_services
    ADD CONSTRAINT apicatalog_services_requiredgroup_fkey FOREIGN KEY (requiredgroup) REFERENCES authgroups(groupname);


--
-- TOC entry 1802 (class 2606 OID 6028909)
-- Dependencies: 128 138 1780
-- Name: apicatalog_services_requiredpermission_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY apicatalog_services
    ADD CONSTRAINT apicatalog_services_requiredpermission_fkey FOREIGN KEY (requiredpermission) REFERENCES authpermissions(permissionname);


--
-- TOC entry 1796 (class 2606 OID 6028834)
-- Dependencies: 130 1780 128
-- Name: authrolepermissions_permissionname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_permissionname_fkey FOREIGN KEY (permissionname) REFERENCES authpermissions(permissionname);


--
-- TOC entry 1797 (class 2606 OID 6028839)
-- Dependencies: 1782 130 129
-- Name: authrolepermissions_rolename_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename);


--
-- TOC entry 1798 (class 2606 OID 6028852)
-- Dependencies: 127 132 1778
-- Name: authusergroups_groupname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authusergroups
    ADD CONSTRAINT authusergroups_groupname_fkey FOREIGN KEY (groupname) REFERENCES authgroups(groupname);


--
-- TOC entry 1799 (class 2606 OID 6028860)
-- Dependencies: 133 129 1782
-- Name: authuserroles_rolename_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authuserroles
    ADD CONSTRAINT authuserroles_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename);


--
-- TOC entry 1807 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2012-09-28 22:14:54 CEST

--
-- PostgreSQL database dump complete
--

