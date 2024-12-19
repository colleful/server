FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} colleful.jar
ENTRYPOINT ["java", "-jar", "colleful.jar"]
