--liquibase formatted sql

--changeset saem:1 endDelimiter:";;"

CREATE OR REPLACE FUNCTION update_modified_column()
  RETURNS TRIGGER AS
$$
BEGIN
  NEW.modified_at = now();
  RETURN NEW;
END;
$$ language 'plpgsql';;

--changeset saem:2

CREATE TABLE contact
(
  no          BIGSERIAL                           NOT NULL UNIQUE PRIMARY KEY,
  id          uuid                                NOT NULL UNIQUE,
  first_name  text                                NOT NULL,
  last_name   text                                NOT NULL,
  created_at  timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  version     BIGINT    DEFAULT 0                 NOT NULL,
  trace_id    text                                NOT NULL,
  CHECK ( modified_at >= created_at )
);

CREATE TABLE contact_phone
(
  no           BIGSERIAL                           NOT NULL UNIQUE PRIMARY KEY,
  id           uuid                                NOT NULL UNIQUE,
  contact_no   BIGINT                              NOT NULL REFERENCES contact (no) ON DELETE CASCADE ON UPDATE CASCADE,
  type         text                                NOT NULL,
  country_code integer                             NOT NULL,
  area_code    text                                NOT NULL,
  number       BIGINT                              NOT NULL,
  extension    text      DEFAULT ''                NOT NULL,
  raw          text                                NOT NULL,
  created_at   timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified_at  timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  version      BIGINT    DEFAULT 0                 NOT NULL,
  trace_id     text                                NOT NULL,
  CHECK ( modified_at >= created_at )
);

CREATE TRIGGER update_contact_modified
  BEFORE UPDATE
  ON contact
  FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER update_phone_modified
  BEFORE UPDATE
  ON contact_phone
  FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();