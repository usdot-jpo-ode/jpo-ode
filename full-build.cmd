cd ..
call git clone https://usdot-jpo-ode@bitbucket.org/usdot-jpo-ode/jpo-ode-private.git
cd jpo-ode-private
call mvn clean
call mvn install
cd ../jpo-ode
call mvn clean install
call mvn --projects jpo-s3-deposit package assembly:single
call docker-compose rm -f -v
call docker-compose build
