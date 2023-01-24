CREATE TABLE arbeidssokerperiode (
                                    id SERIAL primary key,
                                    foedselsnummer varchar(11),
                                    fra TIMESTAMP(6) NOT NULL,
                                    til TIMESTAMP(6),
                                    opprettet TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    endret TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX arbeidssokerperiode_foedselsnummer_index ON arbeidssokerperiode (foedselsnummer);

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.endret = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER arbeidssokerperiode_endret
    BEFORE UPDATE ON arbeidssokerperiode
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();