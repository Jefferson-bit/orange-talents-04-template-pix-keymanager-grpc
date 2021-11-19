FROM openjdk:11
EXPOSE 8080
ADD ./build/libs/pix-*all.jar pix-0.1.jar
ENTRYPOINT ["java", "-jar", "pix-0.1.jar"]