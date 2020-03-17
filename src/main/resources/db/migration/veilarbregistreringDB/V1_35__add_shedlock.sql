CREATE TABLE SHEDLOCK (
  name       VARCHAR(64),
  lock_until TIMESTAMP(3),
  locked_at  TIMESTAMP(3),
  locked_by  VARCHAR(255),
  PRIMARY KEY (name)
);
