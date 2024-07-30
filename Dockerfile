FROM openjdk:22-jdk-slim

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY ./InventoryManagementSystem .~/.m2/repository/com/pawar/inventory
#RUN ./mvnw spring-javaformat:apply
#RUN ./mvnw clean install -U 
#RUN  ./mvnw dependency:resolve
CMD ["./mvnw", "spring-boot:run"]
#RUN npm install -g live-server
#RUN cd src/main/resources/templates/webapp
#RUN live-server
#CMD ["./mvnw", "clean","package"]
#RUN ./mvnw clean install
#RUN ls -ltr
#RUN java -jar /target/weatherapp-0.0.1-SNAPSHOT.jar
#SHELL [ "cd target" ]
#ARG JAR_FILE=target/*.jar
#SHELL [ "ls -ltr" ]
#ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8085