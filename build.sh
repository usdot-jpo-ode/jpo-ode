mvn clean install
mvn --projects jpo-s3-deposit package assembly:single
docker-compose build
