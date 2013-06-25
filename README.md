```sql

```

```sql

ALTER TABLE authusers ALTER COLUMN registrationdate TYPE timestamp without time zone;
ALTER TABLE authusers ALTER COLUMN lastaccess TYPE timestamp without time zone;
ALTER TABLE authusers ALTER COLUMN lastpasswordchange TYPE timestamp without time zone;

-- for "jpsurvey" plugin - Start
ALTER TABLE jpsurvey ADD COLUMN checkusername smallint;
update jpsurvey SET checkusername = 0;
ALTER TABLE jpsurvey ALTER COLUMN checkusername SET NOT NULL;
-- for "jpsurvey" plugin - End


```



