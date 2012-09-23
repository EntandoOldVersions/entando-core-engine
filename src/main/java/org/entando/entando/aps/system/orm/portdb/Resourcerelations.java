/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.portdb;

/**
 *
 * @author eu
 */
public class Resourcerelations {
	
}
/*
CREATE TABLE resourcerelations
(
  resid character varying(16) NOT NULL,
  refcategory character varying(30),
  CONSTRAINT resourcerelations_refcategory_fkey FOREIGN KEY (refcategory)
      REFERENCES categories (catcode) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT resourcerelations_resid_fkey FOREIGN KEY (resid)
      REFERENCES resources (resid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
 */