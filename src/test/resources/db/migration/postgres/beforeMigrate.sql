DO
$do$
BEGIN
   IF EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'cloudsqliamuser') THEN

      RAISE NOTICE 'Role "cloudsqliamuser" already exists. Skipping.';
ELSE
CREATE ROLE cloudsqliamuser LOGIN PASSWORD 'my_password';
END IF;
IF EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'dataprodukt_paw') THEN

      RAISE NOTICE 'Role "dataprodukt_paw" already exists. Skipping.';
ELSE
CREATE ROLE dataprodukt_paw LOGIN PASSWORD 'my_password';
END IF;
END
$do$;