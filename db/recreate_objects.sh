psql -U timekeeper -h 127.0.0.1 -p 5432 postgres -f timekeeper_ddl.sql
psql -U timekeeper -h 127.0.0.1 -p 5432 postgres -f dml/intial_data.sql
