/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

/**
 * @author E.Santoboni
 */
public class Workcontentsearch {
	
}
/*
CREATE TABLE workcontentsearch
(
  contentid character varying(16),
  attrname character varying(30) NOT NULL,
  textvalue character varying(255),
  datevalue date,
  numvalue integer,
  langcode character varying(2),
  CONSTRAINT workcontentsearch_contentid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */