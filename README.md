SQL notes

```sql

-- transition from 'showlets' to 'widgets' - Start
ALTER TABLE showletcatalog RENAME TO widgetcatalog;
ALTER TABLE showletconfig RENAME TO widgetconfig;

ALTER TABLE widgetconfig RENAME COLUMN showletcode to widgetcode;
-- transition from 'showlets' to 'widgets' - End

```

```sql

INSERT INTO authpermissions (permissionname, descr) VALUES ('viewUsers', 'View Users and Profiles');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUsers', 'User Editing');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editUserProfile', 'User Profile Editing');

-- for "User Profile" plugin - Start
ALTER TABLE jpuserprofile_authuserprofiles RENAME TO authuserprofiles;
ALTER TABLE jpuserprofile_profilesearch RENAME TO userprofile_profilesearch;
ALTER TABLE jpuserprofile_attroles RENAME TO userprofile_attroles;
-- OR
CREATE TABLE authuserprofiles (
  username character varying(40) NOT NULL,
  profiletype character varying(30) NOT NULL,
  profilexml text NOT NULL,
  publicprofile smallint NOT NULL,
  CONSTRAINT authuserprofiles_pkey PRIMARY KEY (username )
);

CREATE TABLE authuserprofilesearch (
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  textvalue character varying(255),
  datevalue timestamp without time zone,
  numvalue integer,
  langcode character varying(3),
  CONSTRAINT authuserprofilesearch_fkey FOREIGN KEY (username)
      REFERENCES authuserprofiles (username);
);

CREATE TABLE authuserprofileattrroles (
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  rolename character varying(50) NOT NULL,
  CONSTRAINT authuserprofileattrroles_fkey FOREIGN KEY (username)
      REFERENCES authuserprofiles (username);
);
-- for "User Profile" plugin - END

```




