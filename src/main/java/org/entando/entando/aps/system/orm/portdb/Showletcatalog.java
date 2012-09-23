/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

/**
 *
 * @author eu
 */
public class Showletcatalog {
	
}
/*
CREATE TABLE showletcatalog
(
  code character varying(40) NOT NULL,
  titles character varying NOT NULL,
  parameters character varying,
  plugincode character varying(30),
  parenttypecode character varying(40),
  defaultconfig character varying,
  locked smallint NOT NULL,
  maingroup character varying(20),
  CONSTRAINT showletcatalog_pkey PRIMARY KEY (code )
)
 */