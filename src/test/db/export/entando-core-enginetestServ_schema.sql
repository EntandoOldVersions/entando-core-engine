--
-- PostgreSQL database dump
--

-- Started on 2012-02-25 14:30:52 CET

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
-- TOC entry 137 (class 1259 OID 83959)
-- Dependencies: 6
-- Name: api_oauth_consumers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE api_oauth_consumers (
    consumerkey character varying(100) NOT NULL,
    consumersecret character varying(100) NOT NULL,
    description character varying(500) NOT NULL,
    callbackurl character varying(500),
    expirationdate date
);


--
-- TOC entry 138 (class 1259 OID 83967)
-- Dependencies: 6
-- Name: api_oauth_tokens; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE api_oauth_tokens (
    accesstoken character(100) NOT NULL,
    tokensecret character varying(100) NOT NULL,
    consumerkey character varying(100) NOT NULL,
    lastaccess date NOT NULL,
    username character varying(40) NOT NULL
);


--
-- TOC entry 128 (class 1259 OID 83585)
-- Dependencies: 6
-- Name: apicatalog_methods; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE apicatalog_methods (
    resource character varying(100) NOT NULL,
    httpmethod character varying(6) NOT NULL,
    isactive smallint,
    authenticationrequired smallint,
    authorizationrequired character varying(30)
);


--
-- TOC entry 127 (class 1259 OID 83579)
-- Dependencies: 6
-- Name: apicatalog_services; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE apicatalog_services (
    servicekey character varying(100) NOT NULL,
    resource character varying(100) NOT NULL,
    description character varying NOT NULL,
    parameters character varying,
    tag character varying(100),
    freeparameters character varying,
    isactive smallint NOT NULL,
    ispublic smallint NOT NULL,
    myentando smallint NOT NULL,
    authenticationrequired smallint,
    requiredpermission character varying(30),
    requiredgroup character varying(20)
);


SET default_with_oids = true;

--
-- TOC entry 129 (class 1259 OID 83588)
-- Dependencies: 6
-- Name: authgroups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authgroups (
    groupname character varying(20) NOT NULL,
    descr character varying(50)
);


--
-- TOC entry 130 (class 1259 OID 83591)
-- Dependencies: 6
-- Name: authpermissions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authpermissions (
    permissionname character varying(30) NOT NULL,
    descr character varying(50)
);


--
-- TOC entry 131 (class 1259 OID 83594)
-- Dependencies: 6
-- Name: authrolepermissions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authrolepermissions (
    rolename character varying(30) NOT NULL,
    permissionname character varying(30) NOT NULL
);


--
-- TOC entry 132 (class 1259 OID 83597)
-- Dependencies: 6
-- Name: authroles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authroles (
    rolename character varying(20) NOT NULL,
    descr character varying(50)
);


--
-- TOC entry 133 (class 1259 OID 83600)
-- Dependencies: 6
-- Name: authusergroups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusergroups (
    username character varying(40) NOT NULL,
    groupname character varying(20) NOT NULL
);


SET default_with_oids = false;

--
-- TOC entry 134 (class 1259 OID 83603)
-- Dependencies: 6
-- Name: authuserroles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authuserroles (
    username character varying(40) NOT NULL,
    rolename character varying(20) NOT NULL
);


SET default_with_oids = true;

--
-- TOC entry 135 (class 1259 OID 83606)
-- Dependencies: 6
-- Name: authusers; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusers (
    username character varying(40) NOT NULL,
    passwd character varying(40),
    registrationdate date NOT NULL,
    lastaccess date,
    lastpasswordchange date,
    active smallint
);


--
-- TOC entry 136 (class 1259 OID 83609)
-- Dependencies: 6
-- Name: authusershortcuts; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusershortcuts (
    username character varying(40) NOT NULL,
    config character varying NOT NULL
);


--
-- TOC entry 1799 (class 2606 OID 83966)
-- Dependencies: 137 137
-- Name: api_oauth_consumers_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY api_oauth_consumers
    ADD CONSTRAINT api_oauth_consumers_pkey PRIMARY KEY (consumerkey);


--
-- TOC entry 1801 (class 2606 OID 83971)
-- Dependencies: 138 138
-- Name: api_oauth_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY api_oauth_tokens
    ADD CONSTRAINT api_oauth_tokens_pkey PRIMARY KEY (accesstoken);


--
-- TOC entry 1779 (class 2606 OID 83616)
-- Dependencies: 127 127
-- Name: apicatalog_services_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_services
    ADD CONSTRAINT apicatalog_services_pkey PRIMARY KEY (servicekey);


--
-- TOC entry 1781 (class 2606 OID 83973)
-- Dependencies: 128 128 128
-- Name: apicatalog_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_methods
    ADD CONSTRAINT apicatalog_status_pkey PRIMARY KEY (resource, httpmethod);


--
-- TOC entry 1783 (class 2606 OID 83620)
-- Dependencies: 129 129
-- Name: authgroups_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authgroups
    ADD CONSTRAINT authgroups_pkey PRIMARY KEY (groupname);


--
-- TOC entry 1785 (class 2606 OID 83622)
-- Dependencies: 130 130
-- Name: authpermissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authpermissions
    ADD CONSTRAINT authpermissions_pkey PRIMARY KEY (permissionname);


--
-- TOC entry 1787 (class 2606 OID 83624)
-- Dependencies: 131 131 131
-- Name: authrolepermissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_pkey PRIMARY KEY (rolename, permissionname);


--
-- TOC entry 1789 (class 2606 OID 83626)
-- Dependencies: 132 132
-- Name: authroles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authroles
    ADD CONSTRAINT authroles_pkey PRIMARY KEY (rolename);


--
-- TOC entry 1791 (class 2606 OID 83628)
-- Dependencies: 133 133 133
-- Name: authusergroups_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusergroups
    ADD CONSTRAINT authusergroups_pkey PRIMARY KEY (username, groupname);


--
-- TOC entry 1793 (class 2606 OID 83630)
-- Dependencies: 134 134 134
-- Name: authuserroles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authuserroles
    ADD CONSTRAINT authuserroles_pkey PRIMARY KEY (username, rolename);


--
-- TOC entry 1795 (class 2606 OID 83632)
-- Dependencies: 135 135
-- Name: authusers_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusers
    ADD CONSTRAINT authusers_pkey PRIMARY KEY (username);


--
-- TOC entry 1797 (class 2606 OID 83634)
-- Dependencies: 136 136
-- Name: authusershortcuts_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusershortcuts
    ADD CONSTRAINT authusershortcuts_pkey PRIMARY KEY (username);


--
-- TOC entry 1797 (class 2606 OID 83634)
-- Dependencies: 136 136
-- Name: apicatalog_methods_authorizationrequired_fkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_methods
  ADD CONSTRAINT apicatalog_methods_authorizationrequired_fkey FOREIGN KEY (authorizationrequired) REFERENCES authpermissions (permissionname) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1797 (class 2606 OID 83634)
-- Dependencies: 136 136
-- Name: apicatalog_services_requiredpermission_fkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_services
  ADD CONSTRAINT apicatalog_services_requiredpermission_fkey FOREIGN KEY (requiredpermission) REFERENCES authpermissions (permissionname) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1797 (class 2606 OID 83634)
-- Dependencies: 136 136
-- Name: apicatalog_services_requiredgroup_fkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_services
  ADD CONSTRAINT apicatalog_services_requiredgroup_fkey FOREIGN KEY (requiredgroup) REFERENCES authgroups (groupname) ON UPDATE RESTRICT ON DELETE RESTRICT;



--
-- TOC entry 1802 (class 2606 OID 83635)
-- Dependencies: 130 1784 131
-- Name: authrolepermissions_permissionname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_permissionname_fkey FOREIGN KEY (permissionname) REFERENCES authpermissions(permissionname) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1803 (class 2606 OID 83640)
-- Dependencies: 1788 131 132
-- Name: authrolepermissions_rolename_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1804 (class 2606 OID 83645)
-- Dependencies: 133 1782 129
-- Name: authusergroups_groupname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authusergroups
    ADD CONSTRAINT authusergroups_groupname_fkey FOREIGN KEY (groupname) REFERENCES authgroups(groupname) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1805 (class 2606 OID 83650)
-- Dependencies: 134 1788 132
-- Name: authuserroles_rolename_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authuserroles
    ADD CONSTRAINT authuserroles_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1810 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2012-02-25 14:30:52 CET

--
-- PostgreSQL database dump complete
--

