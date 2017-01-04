call mvn clean install
call docker-compose stop
call docker-compose up --build --no-recreate -d