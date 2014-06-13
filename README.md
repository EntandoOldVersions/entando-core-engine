SQL notes

```sql 

-- sql - port database


CREATE TABLE guifragment
(
  code character varying(50) NOT NULL,
  widgettypecode character varying(40),
  plugincode character varying(30),
  gui text NOT NULL,
  CONSTRAINT guifragment_pkey PRIMARY KEY (code ),
  CONSTRAINT guifragment_wdgtypecode_fkey FOREIGN KEY (widgettypecode)
      REFERENCES widgetcatalog (code) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


```

```sql

-- sql - serv database





INSERT INTO actionlogrecords (id, username, actiondate, namespace, actionname, parameters, activitystreaminfo) VALUES (3, 'admin', '2013-09-27 11:00:12', '/do/Page', 'save', 'selectedNode=service
model=service
strutsAction=1
extraGroupName=free
charset=
parentPageCode=service
defaultShowlet=true
copyPageCode=
langit=Errore di Sistema
groupSelectLock=false
langen=System Error
group=free
mimeType=
pageCode=errorpage
', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<activityStreamInfo>
    <objectTitles>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">en</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">System Error</value>
        </entry>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">it</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">Errore di Sistema</value>
        </entry>
    </objectTitles>
    <groups>
        <group>free</group>
    </groups>
    <actionType>1</actionType>
    <linkNamespace>/do/Page</linkNamespace>
    <linkActionName>edit</linkActionName>
    <linkParameters>
        <entry>
            <key xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">selectedNode</key>
            <value xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">errorpage</value>
        </entry>
    </linkParameters>
    <linkAuthPermission>managePages</linkAuthPermission>
    <linkAuthGroup>free</linkAuthGroup>
</activityStreamInfo>
');

INSERT INTO actionlogrelations (recordid, refgroup) VALUES (1, 'free');
INSERT INTO actionlogrelations (recordid, refgroup) VALUES (2, 'free');
INSERT INTO actionlogrelations (recordid, refgroup) VALUES (3, 'free');

ALTER TABLE actionlogrecords ADD COLUMN updatedate timestamp without time zone;
UPDATE actionlogrecords SET updatedate = actiondate;

```
