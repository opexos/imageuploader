# image service 

This service will allow upload images to the server using three different methods:
* multipart/form-data
* JSON with URLs
* JSON with Base64 encoded images

Also in this application you will find:
* graceful shutdown (tomcat server)
* creating preview images
* downloading images from the service
* service statistics
* a lot of tests

Technologies used:
* Java 8
* Spring Framework
* Lombok
* Memcached
* Redis
* Swagger
* Hibernate
* Liquibase
* PostgreSQL
* Docker
* MDC

### How to start service:

1. Install [docker](https://docs.docker.com/install/)
2. Install [docker-compose](https://docs.docker.com/compose/install/)
3. Clone repository
4. Inside app folder run: docker-compose up
5. Run browser and go to http://localhost:60000


### How to start tests:

1. Disable 'imageservice' in docker-compose.yml
2. Run 'docker-compose up'
3. Switch to 'dev' profile
4. Now you can run tests. Some tests are integration tests and they refer to the database.