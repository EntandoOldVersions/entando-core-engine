--
-- PostgreSQL database dump
--

-- Started on 2012-09-28 22:14:53 CEST

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 483 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 128 (class 1259 OID 6028658)
-- Dependencies: 3
-- Name: categories; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE categories (
    catcode character varying(30) NOT NULL,
    parentcode character varying(30) NOT NULL,
    titles text NOT NULL
);


--
-- TOC entry 129 (class 1259 OID 6028666)
-- Dependencies: 3
-- Name: contentmodels; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE contentmodels (
    modelid integer NOT NULL,
    contenttype character varying(30) NOT NULL,
    descr character varying(50) NOT NULL,
    model text,
    stylesheet character varying(50)
);


--
-- TOC entry 139 (class 1259 OID 6028763)
-- Dependencies: 3
-- Name: contentrelations; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE contentrelations (
    contentid character varying(16) NOT NULL,
    refpage character varying(30),
    refcontent character varying(16),
    refresource character varying(16),
    refcategory character varying(30),
    refgroup character varying(20)
);


--
-- TOC entry 130 (class 1259 OID 6028674)
-- Dependencies: 3
-- Name: contents; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE contents (
    contentid character varying(16) NOT NULL,
    contenttype character varying(30) NOT NULL,
    descr character varying(255) NOT NULL,
    status character varying(12) NOT NULL,
    workxml text NOT NULL,
    created character varying(20),
    lastmodified character varying(20),
    onlinexml text,
    maingroup character varying(20) NOT NULL,
    currentversion character varying(7) NOT NULL,
    lasteditor character varying(40)
);


--
-- TOC entry 140 (class 1259 OID 6028791)
-- Dependencies: 3
-- Name: contentsearch; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE contentsearch (
    contentid character varying(16) NOT NULL,
    attrname character varying(30) NOT NULL,
    textvalue character varying(255),
    datevalue timestamp without time zone,
    numvalue integer,
    langcode character varying(3)
);


--
-- TOC entry 131 (class 1259 OID 6028682)
-- Dependencies: 3
-- Name: localstrings; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE localstrings (
    keycode character varying(50) NOT NULL,
    langcode character varying(2) NOT NULL,
    stringvalue text NOT NULL
);


--
-- TOC entry 132 (class 1259 OID 6028690)
-- Dependencies: 3
-- Name: pagemodels; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pagemodels (
    code character varying(40) NOT NULL,
    descr character varying(50) NOT NULL,
    frames text,
    plugincode character varying(30)
);


--
-- TOC entry 133 (class 1259 OID 6028698)
-- Dependencies: 3
-- Name: pages; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE pages (
    code character varying(30) NOT NULL,
    parentcode character varying(30) NOT NULL,
    pos integer NOT NULL,
    modelcode character varying(40) NOT NULL,
    titles text NOT NULL,
    groupcode character varying(30) NOT NULL,
    showinmenu smallint NOT NULL,
    extraconfig text
);


--
-- TOC entry 135 (class 1259 OID 6028719)
-- Dependencies: 3
-- Name: resourcerelations; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE resourcerelations (
    resid character varying(16) NOT NULL,
    refcategory character varying(30) NOT NULL
);


--
-- TOC entry 134 (class 1259 OID 6028711)
-- Dependencies: 3
-- Name: resources; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE resources (
    resid character varying(16) NOT NULL,
    restype character varying(30) NOT NULL,
    descr character varying(255) NOT NULL,
    maingroup character varying(20) NOT NULL,
    resourcexml text NOT NULL,
    masterfilename character varying(100) NOT NULL
);


--
-- TOC entry 136 (class 1259 OID 6028732)
-- Dependencies: 3
-- Name: showletcatalog; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE showletcatalog (
    code character varying(40) NOT NULL,
    titles text NOT NULL,
    parameters text,
    plugincode character varying(30),
    parenttypecode character varying(40),
    defaultconfig text,
    locked integer NOT NULL,
    maingroup character varying(20)
);


--
-- TOC entry 137 (class 1259 OID 6028740)
-- Dependencies: 3
-- Name: showletconfig; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE showletconfig (
    pagecode character varying(30) NOT NULL,
    framepos integer NOT NULL,
    showletcode character varying(40) NOT NULL,
    config text,
    publishedcontent character varying(30)
);


--
-- TOC entry 127 (class 1259 OID 6028650)
-- Dependencies: 3
-- Name: sysconfig; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE sysconfig (
    version character varying(10) NOT NULL,
    item character varying(40) NOT NULL,
    descr character varying(100) NOT NULL,
    config text NOT NULL
);


--
-- TOC entry 138 (class 1259 OID 6028758)
-- Dependencies: 3
-- Name: uniquekeys; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE uniquekeys (
    id integer NOT NULL,
    keyvalue integer NOT NULL
);


--
-- TOC entry 141 (class 1259 OID 6028799)
-- Dependencies: 3
-- Name: workcontentrelations; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE workcontentrelations (
    contentid character varying(16) NOT NULL,
    refcategory character varying(30)
);


--
-- TOC entry 142 (class 1259 OID 6028807)
-- Dependencies: 3
-- Name: workcontentsearch; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE workcontentsearch (
    contentid character varying(16) NOT NULL,
    attrname character varying(30) NOT NULL,
    textvalue character varying(255),
    datevalue timestamp without time zone,
    numvalue integer,
    langcode character varying(3)
);


--
-- TOC entry 1804 (class 2606 OID 6028665)
-- Dependencies: 128 128
-- Name: categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (catcode);


--
-- TOC entry 1806 (class 2606 OID 6028673)
-- Dependencies: 129 129
-- Name: contentmodels_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY contentmodels
    ADD CONSTRAINT contentmodels_pkey PRIMARY KEY (modelid);


--
-- TOC entry 1808 (class 2606 OID 6028681)
-- Dependencies: 130 130
-- Name: contents_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY contents
    ADD CONSTRAINT contents_pkey PRIMARY KEY (contentid);


--
-- TOC entry 1810 (class 2606 OID 6028689)
-- Dependencies: 131 131 131
-- Name: localstrings_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY localstrings
    ADD CONSTRAINT localstrings_pkey PRIMARY KEY (keycode, langcode);


--
-- TOC entry 1812 (class 2606 OID 6028697)
-- Dependencies: 132 132
-- Name: pagemodels_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pagemodels
    ADD CONSTRAINT pagemodels_pkey PRIMARY KEY (code);


--
-- TOC entry 1814 (class 2606 OID 6028705)
-- Dependencies: 133 133
-- Name: pages_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY pages
    ADD CONSTRAINT pages_pkey PRIMARY KEY (code);


--
-- TOC entry 1816 (class 2606 OID 6028718)
-- Dependencies: 134 134
-- Name: resources_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY resources
    ADD CONSTRAINT resources_pkey PRIMARY KEY (resid);


--
-- TOC entry 1818 (class 2606 OID 6028739)
-- Dependencies: 136 136
-- Name: showletcatalog_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY showletcatalog
    ADD CONSTRAINT showletcatalog_pkey PRIMARY KEY (code);


--
-- TOC entry 1820 (class 2606 OID 6028747)
-- Dependencies: 137 137 137
-- Name: showletconfig_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY showletconfig
    ADD CONSTRAINT showletconfig_pkey PRIMARY KEY (pagecode, framepos);


--
-- TOC entry 1802 (class 2606 OID 6028657)
-- Dependencies: 127 127 127
-- Name: sysconfig_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY sysconfig
    ADD CONSTRAINT sysconfig_pkey PRIMARY KEY (version, item);


--
-- TOC entry 1822 (class 2606 OID 6028762)
-- Dependencies: 138 138
-- Name: uniquekeys_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY uniquekeys
    ADD CONSTRAINT uniquekeys_pkey PRIMARY KEY (id);


--
-- TOC entry 1828 (class 2606 OID 6028766)
-- Dependencies: 1807 139 130
-- Name: contentrelations_contentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY contentrelations
    ADD CONSTRAINT contentrelations_contentid_fkey FOREIGN KEY (contentid) REFERENCES contents(contentid);


--
-- TOC entry 1829 (class 2606 OID 6028771)
-- Dependencies: 139 1803 128
-- Name: contentrelations_refcategory_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY contentrelations
    ADD CONSTRAINT contentrelations_refcategory_fkey FOREIGN KEY (refcategory) REFERENCES categories(catcode);


--
-- TOC entry 1830 (class 2606 OID 6028776)
-- Dependencies: 139 130 1807
-- Name: contentrelations_refcontent_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY contentrelations
    ADD CONSTRAINT contentrelations_refcontent_fkey FOREIGN KEY (refcontent) REFERENCES contents(contentid);


--
-- TOC entry 1831 (class 2606 OID 6028781)
-- Dependencies: 133 1813 139
-- Name: contentrelations_refpage_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY contentrelations
    ADD CONSTRAINT contentrelations_refpage_fkey FOREIGN KEY (refpage) REFERENCES pages(code);


--
-- TOC entry 1832 (class 2606 OID 6028786)
-- Dependencies: 1815 139 134
-- Name: contentrelations_refresource_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY contentrelations
    ADD CONSTRAINT contentrelations_refresource_fkey FOREIGN KEY (refresource) REFERENCES resources(resid);


--
-- TOC entry 1833 (class 2606 OID 6028794)
-- Dependencies: 140 1807 130
-- Name: contentsearch_contentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY contentsearch
    ADD CONSTRAINT contentsearch_contentid_fkey FOREIGN KEY (contentid) REFERENCES contents(contentid);


--
-- TOC entry 1823 (class 2606 OID 6028706)
-- Dependencies: 1811 133 132
-- Name: pages_modelcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pages
    ADD CONSTRAINT pages_modelcode_fkey FOREIGN KEY (modelcode) REFERENCES pagemodels(code);


--
-- TOC entry 1824 (class 2606 OID 6028722)
-- Dependencies: 135 1803 128
-- Name: resourcerelations_refcategory_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resourcerelations
    ADD CONSTRAINT resourcerelations_refcategory_fkey FOREIGN KEY (refcategory) REFERENCES categories(catcode);


--
-- TOC entry 1825 (class 2606 OID 6028727)
-- Dependencies: 135 134 1815
-- Name: resourcerelations_resid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY resourcerelations
    ADD CONSTRAINT resourcerelations_resid_fkey FOREIGN KEY (resid) REFERENCES resources(resid);


--
-- TOC entry 1826 (class 2606 OID 6028748)
-- Dependencies: 1813 137 133
-- Name: showletconfig_pagecode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY showletconfig
    ADD CONSTRAINT showletconfig_pagecode_fkey FOREIGN KEY (pagecode) REFERENCES pages(code);


--
-- TOC entry 1827 (class 2606 OID 6028753)
-- Dependencies: 137 136 1817
-- Name: showletconfig_showletcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY showletconfig
    ADD CONSTRAINT showletconfig_showletcode_fkey FOREIGN KEY (showletcode) REFERENCES showletcatalog(code);


--
-- TOC entry 1834 (class 2606 OID 6028802)
-- Dependencies: 1807 130 141
-- Name: workcontentrelations_contentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workcontentrelations
    ADD CONSTRAINT workcontentrelations_contentid_fkey FOREIGN KEY (contentid) REFERENCES contents(contentid);


--
-- TOC entry 1835 (class 2606 OID 6028810)
-- Dependencies: 142 130 1807
-- Name: workcontentsearch_contentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workcontentsearch
    ADD CONSTRAINT workcontentsearch_contentid_fkey FOREIGN KEY (contentid) REFERENCES contents(contentid);


--
-- TOC entry 1840 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2012-09-28 22:14:53 CEST

--
-- PostgreSQL database dump complete
--

