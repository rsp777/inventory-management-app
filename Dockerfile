FROM openjdk:22-jdk-slim
MAINTAINER ravindra
WORKDIR /inventoryapp
COPY /target/InventoryManagementApp-0.0.1-SNAPSHOT.jar /inventoryapp/InventoryManagementApp-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/inventoryapp/InventoryManagementApp-0.0.1-SNAPSHOT.jar"]
EXPOSE 8086
