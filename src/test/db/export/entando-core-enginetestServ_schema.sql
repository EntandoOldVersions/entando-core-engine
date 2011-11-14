--
-- PostgreSQL database dump
--

-- Started on 2011-11-10 17:08:27 CET

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 311 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1483 (class 1259 OID 2939747)
-- Dependencies: 6
-- Name: apicatalog_services; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE apicatalog_services (
    servicekey character varying(100) NOT NULL,
    parentapi character varying(100) NOT NULL,
    description character varying NOT NULL,
    parameters character varying,
    tag character varying(100),
    freeparameters character varying,
    isactive smallint NOT NULL,
    ispublic smallint NOT NULL,
    myentando smallint NOT NULL
);


--
-- TOC entry 1484 (class 1259 OID 2939753)
-- Dependencies: 6
-- Name: apicatalog_status; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE apicatalog_status (
    method character varying(100) NOT NULL,
    isactive smallint NOT NULL
);


SET default_with_oids = true;

--
-- TOC entry 1485 (class 1259 OID 2939756)
-- Dependencies: 6
-- Name: authgroups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authgroups (
    groupname character varying(20) NOT NULL,
    descr character varying(50)
);


--
-- TOC entry 1486 (class 1259 OID 2939759)
-- Dependencies: 6
-- Name: authpermissions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authpermissions (
    permissionname character varying(30) NOT NULL,
    descr character varying(50)
);


--
-- TOC entry 1487 (class 1259 OID 2939762)
-- Dependencies: 6
-- Name: authrolepermissions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authrolepermissions (
    rolename character varying(30) NOT NULL,
    permissionname character varying(30) NOT NULL
);


--
-- TOC entry 1488 (class 1259 OID 2939765)
-- Dependencies: 6
-- Name: authroles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authroles (
    rolename character varying(20) NOT NULL,
    descr character varying(50)
);


--
-- TOC entry 1489 (class 1259 OID 2939768)
-- Dependencies: 6
-- Name: authusergroups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusergroups (
    username character varying(40) NOT NULL,
    groupname character varying(20) NOT NULL
);


SET default_with_oids = false;

--
-- TOC entry 1490 (class 1259 OID 2939771)
-- Dependencies: 6
-- Name: authuserroles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authuserroles (
    username character varying(40) NOT NULL,
    rolename character varying(20) NOT NULL
);


SET default_with_oids = true;

--
-- TOC entry 1491 (class 1259 OID 2939774)
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
-- TOC entry 1492 (class 1259 OID 2939777)
-- Dependencies: 6
-- Name: authusershortcuts; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authusershortcuts (
    username character varying(40) NOT NULL,
    config character varying NOT NULL
);


--
-- TOC entry 1760 (class 2606 OID 2939784)
-- Dependencies: 1483 1483
-- Name: apicatalog_services_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_services
    ADD CONSTRAINT apicatalog_services_pkey PRIMARY KEY (servicekey);


--
-- TOC entry 1762 (class 2606 OID 2939786)
-- Dependencies: 1484 1484
-- Name: apicatalog_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY apicatalog_status
    ADD CONSTRAINT apicatalog_status_pkey PRIMARY KEY (method);


--
-- TOC entry 1764 (class 2606 OID 2939788)
-- Dependencies: 1485 1485
-- Name: authgroups_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authgroups
    ADD CONSTRAINT authgroups_pkey PRIMARY KEY (groupname);


--
-- TOC entry 1766 (class 2606 OID 2939790)
-- Dependencies: 1486 1486
-- Name: authpermissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authpermissions
    ADD CONSTRAINT authpermissions_pkey PRIMARY KEY (permissionname);


--
-- TOC entry 1768 (class 2606 OID 2939792)
-- Dependencies: 1487 1487 1487
-- Name: authrolepermissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_pkey PRIMARY KEY (rolename, permissionname);


--
-- TOC entry 1770 (class 2606 OID 2939794)
-- Dependencies: 1488 1488
-- Name: authroles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authroles
    ADD CONSTRAINT authroles_pkey PRIMARY KEY (rolename);


--
-- TOC entry 1772 (class 2606 OID 2939796)
-- Dependencies: 1489 1489 1489
-- Name: authusergroups_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusergroups
    ADD CONSTRAINT authusergroups_pkey PRIMARY KEY (username, groupname);


--
-- TOC entry 1774 (class 2606 OID 2939798)
-- Dependencies: 1490 1490 1490
-- Name: authuserroles_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authuserroles
    ADD CONSTRAINT authuserroles_pkey PRIMARY KEY (username, rolename);


--
-- TOC entry 1776 (class 2606 OID 2939800)
-- Dependencies: 1491 1491
-- Name: authusers_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusers
    ADD CONSTRAINT authusers_pkey PRIMARY KEY (username);


--
-- TOC entry 1778 (class 2606 OID 2939802)
-- Dependencies: 1492 1492
-- Name: authusershortcuts_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authusershortcuts
    ADD CONSTRAINT authusershortcuts_pkey PRIMARY KEY (username);


--
-- TOC entry 1779 (class 2606 OID 2939803)
-- Dependencies: 1765 1486 1487
-- Name: authrolepermissions_permissionname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_permissionname_fkey FOREIGN KEY (permissionname) REFERENCES authpermissions(permissionname) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1780 (class 2606 OID 2939808)
-- Dependencies: 1769 1487 1488
-- Name: authrolepermissions_rolename_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1781 (class 2606 OID 2939813)
-- Dependencies: 1489 1485 1763
-- Name: authusergroups_groupname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authusergroups
    ADD CONSTRAINT authusergroups_groupname_fkey FOREIGN KEY (groupname) REFERENCES authgroups(groupname) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1782 (class 2606 OID 2939818)
-- Dependencies: 1488 1490 1769
-- Name: authuserroles_rolename_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authuserroles
    ADD CONSTRAINT authuserroles_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1787 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2011-11-10 17:08:27 CET

--
-- PostgreSQL database dump complete
--

