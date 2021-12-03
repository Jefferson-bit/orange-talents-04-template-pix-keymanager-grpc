FROM openjdk:11
EXPOSE 8080
WORKDIR /pix
COPY ./build/libs/pix-0.1-all.jar pix.jar
ENTRYPOINT ["java", "-jar", "pix.jar"]