FROM eclipse-temurin:21-jdk-alpine

# expose the port to use for access to the service
EXPOSE 8080

# create group and user "imperator-server" to avoid the use of root inside the container
RUN addgroup -S imperator-server && adduser -S imperator-server -G imperator-server

# create the directory where the application will be installed
RUN mkdir -p /opt/imperator-server

# move to the application directory
WORKDIR /opt/imperator-server

# copy the jar previously created into the application directory
COPY target/*.jar /opt/imperator-server

# change the owner of the imperator-server directory
RUN chown -R imperator-server:imperator-server /opt/imperator-server

# switch to imperator-server user
USER imperator-server

# execute the application
ENTRYPOINT [ "java", "-jar", "imperator-server.jar" ]
