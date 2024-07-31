FROM openjdk:22-jdk-slim
MAINTAINER ravindra
WORKDIR /inventoryapp
COPY target/InventoryManagementApp-0.0.1-SNAPSHOT.jar /inventoryapp/InventoryManagementApp-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/InventoryManagementApp-0.0.1-SNAPSHOT.jar"]
<<<<<<< HEAD
EXPOSE 8086
=======
EXPOSE 8086
>>>>>>> 4959e52e6281d1c54789c2e20490b51cfd515565
