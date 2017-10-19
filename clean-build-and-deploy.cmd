call docker-compose stop
call docker-compose rm -f -v
call mvn -DskipTests clean install
call docker-compose up --build -d
call docker-compose ps