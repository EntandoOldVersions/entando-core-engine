/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

/**
 *
 * @author eu
 */
public class ShowletConfig {
	
}
/*
CREATE TABLE showletconfig
(
  pagecode character varying(30) NOT NULL,
  framepos integer NOT NULL,
  showletcode character varying(40) NOT NULL,
  config character varying,
  publishedcontent character varying(30),
  CONSTRAINT showletconfig_pkey PRIMARY KEY (pagecode , framepos ),
  CONSTRAINT showletconfig_pagecode_fkey FOREIGN KEY (pagecode)
      REFERENCES pages (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT showletconfig_showletcode_fkey FOREIGN KEY (showletcode)
      REFERENCES showletcatalog (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */
