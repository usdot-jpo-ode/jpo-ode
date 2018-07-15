call mvn clean install
call mvn --projects jpo-s3-deposit package assembly:single
call docker-compose rm -f -v
call docker-compose build
