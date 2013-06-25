```sql

CREATE TABLE contentattributeroles
(
  contentid character varying(16) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT contentattrroles_contid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE workcontentattributeroles
(
  contentid character varying(16) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT workcontentattrroles_contid_fkey FOREIGN KEY (contentid)
      REFERENCES contents (contentid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

```

```sql

-- for "Address book" plugin - Start
CREATE TABLE jpaddressbook_attroles
(
  contactkey character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT jpaddressbook_attroles_fkey FOREIGN KEY (contactkey)
      REFERENCES jpaddressbook_contacts (contactkey) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- for "Address book" plugin - End


-- for "User Profile" plugin - Start
CREATE TABLE jpuserprofile_attroles
(
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT jpuserprofile_attroles_fkey FOREIGN KEY (username)
      REFERENCES jpuserprofile_authuserprofiles (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- for "User Profile" plugin - End


-- for "Web Dynamic Form" plugin - Start
CREATE TABLE jpwebdynamicform_attroles
(
  messageid character varying(16) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT jpwebdynamicform_attroles_fkey FOREIGN KEY (messageid)
      REFERENCES jpwebdynamicform_messages (messageid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- for "Web Dynamic Form" plugin - End


-- for "jpsurvey" plugin - Start
ALTER TABLE jpsurvey ADD COLUMN checkusername smallint;
update jpsurvey SET checkusername = 0;
ALTER TABLE jpsurvey ALTER COLUMN checkusername SET NOT NULL;
-- for "jpsurvey" plugin - End

```




