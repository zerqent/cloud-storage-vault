CREATE TABLE write_enablers (
    storage_index character varying(51) NOT NULL,
    write_enabler character varying(51) NOT NULL
);

ALTER TABLE ONLY write_enablers
    ADD CONSTRAINT write_enablers_pkey PRIMARY KEY (storage_index);
