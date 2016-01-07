sed in.sql \
  -e '/DROP SEQUENCE/s/DROP SEQUENCE/DROP SEQUENCE if exists /'  \
  -e '/DROP SEQUENCE/s/;/ cascade;/g' \
  -e '/DROP TABLE/s/CONSTRAINTS PURGE//' \
  -e '/DROP TABLE/s/DROP TABLE/DROP TABLE if exists /' \
  -e '/ADD (CONSTRAINT/s/ADD (CONSTRAINT/ADD CONSTRAINT/' \
  -e '/ADD CONSTRAINT/s/NULL);/NULL;/' \
  -e '/ADD CONSTRAI/s/));/);/'  \
  > timekeeper_ddl.sql

