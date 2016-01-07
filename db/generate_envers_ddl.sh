#!/bin/bash

cd ..

if [ ! -f target/classes/br/com/redhat/consulting/model/Project.class  ] ; then
  mvn -q compile hibernate4:export
  echo "---------------"
  echo "-- use only the instructions below this line"
fi
grep -E 'REVINFO|audit' target/envers_ddl.sql > tmp_envers_ddl.sql

grep create tmp_envers_ddl.sql |awk '{print $3}'| awk '{print "drop table "$1" cascade;"}' > tmp_drop_envers.sql
cat tmp_drop_envers.sql tmp_envers_ddl.sql > envers_ddl.sql
psql -U timekeeper -h 127.0.0.1 -p 5432 postgres -f envers_ddl.sql

rm -f tmp_drop_envers.sql tmp_envers_ddl.sql envers_ddl.sql
cd db


