/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

/**
 *
 * @author eu
 */
public class Pages {
	
}
/*
CREATE TABLE pages
(
  code character varying(30) NOT NULL,
  parentcode character varying(30),
  pos integer NOT NULL,
  modelcode character varying(40) NOT NULL,
  titles character varying,
  groupcode character varying(30) NOT NULL,
  showinmenu smallint NOT NULL,
  extraconfig character varying,
  CONSTRAINT pages_pkey PRIMARY KEY (code ),
  CONSTRAINT pages_modelcode_fkey FOREIGN KEY (modelcode)
      REFERENCES pagemodels (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */
