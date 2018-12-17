# image uploader 

This web application will allow you to upload images to the server using three different methods:

* multipart/form-data
* JSON with URLs
* JSON with Base64 encoded images

Also in this application you will find:
* graceful shutdown (tomcat server)
* creating preview images
* a lot of tests

Technologies used:
* Java 8
* Spring Framework
* Memcached
* Redis

#### How to start application:

1. Install [docker](https://docs.docker.com/install/)
2. Install [docker-compose](https://docs.docker.com/compose/install/)
3. Inside app folder run: docker-compose up
4. Run browser and go to http://localhost:8080/
