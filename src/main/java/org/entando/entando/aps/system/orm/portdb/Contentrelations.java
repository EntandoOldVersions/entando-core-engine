/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

/**
 * @author eu
 */
public class Contentrelations {
	
}
/*
CREATE TABLE contentrelations
(
  contentid character varying(16) NOT NULL,
  refpage character varying(30),
  refcontent character varying(16),
  refresource character varying(16),
  refcategory character varying(30),
  refgroup character varying(20),
  CONSTRAINT contentrelations_contentid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refcategory_fkey FOREIGN KEY (refcategory)
      REFERENCES categories (catcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refcontent_fkey FOREIGN KEY (refcontent)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refpage_fkey FOREIGN KEY (refpage)
      REFERENCES pages (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT contentrelations_refresource_fkey FOREIGN KEY (refresource)
      REFERENCES resources (resid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */