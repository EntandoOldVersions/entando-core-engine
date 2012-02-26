-- from 2.4.0.1 to 3.0

CREATE TABLE api_oauth_consumers
(
  consumerkey character varying(100) NOT NULL,
  consumersecret character varying(100) NOT NULL,
  description character varying(500) NOT NULL,
  callbackurl character varying(500),
  expirationdate date,
  CONSTRAINT api_oauth_consumers_pkey PRIMARY KEY (consumerkey)
);

CREATE TABLE api_oauth_tokens
(
  accesstoken character(100) NOT NULL,
  tokensecret character varying(100) NOT NULL,
  consumerkey character varying(100) NOT NULL,
  lastaccess date NOT NULL,
  username character varying(40) NOT NULL,
  CONSTRAINT api_oauth_tokens_pkey PRIMARY KEY (accesstoken)
);


ALTER TABLE apicatalog_status RENAME method TO resource;
ALTER TABLE apicatalog_status ADD COLUMN httpmethod character varying(6);
UPDATE apicatalog_status SET httpmethod = 'GET';
ALTER TABLE apicatalog_status ALTER COLUMN httpmethod SET NOT NULL;
ALTER TABLE apicatalog_status ADD COLUMN isactive_temp smallint;
UPDATE apicatalog_status SET isactive_temp = isactive;
ALTER TABLE apicatalog_status DROP COLUMN isactive;
ALTER TABLE apicatalog_status RENAME isactive_temp TO isactive;
ALTER TABLE apicatalog_status DROP CONSTRAINT apicatalog_status_pkey;
ALTER TABLE apicatalog_status
  ADD CONSTRAINT apicatalog_status_pkey PRIMARY KEY(resource, httpmethod);
ALTER TABLE apicatalog_status ADD COLUMN authenticationrequired smallint;
ALTER TABLE apicatalog_status ADD COLUMN authorizationrequired character varying(100);

ALTER TABLE apicatalog_status RENAME TO apicatalog_methods;

ALTER TABLE apicatalog_services RENAME parentapi TO resource;
