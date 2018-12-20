call docker-compose down
call docker-compose -f docker-compose-ppm-nsv.yml up --build -d
call docker-compose ps