call docker-compose stop
call docker-compose rm -f -v
call mvn %1 clean install
call mvn --projects jpo-s3-deposit package assembly:single
call docker-compose up --build -d
call docker-compose ps