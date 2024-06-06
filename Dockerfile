FROM ubuntu:latest

RUN apt update

RUN apt install openjdk-17-jdk -y

RUN apt install maven -y

WORKDIR /app

COPY . /app/


ENTRYPOINT [ "mvn","spring-boot:run" ]