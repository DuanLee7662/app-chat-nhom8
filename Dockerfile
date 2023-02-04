#base image
FROM openjdk:8
COPY . /src/server
WORKDIR /src/java
RUN ["javac", ""]