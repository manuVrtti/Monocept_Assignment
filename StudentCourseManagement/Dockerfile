# Stage 1: Build the Java application using Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copy the pom.xml and download dependencies
COPY pom.xml .
# Copy the source code
COPY src ./src
# Build the WAR file
RUN mvn clean package -DskipTests

# Stage 2: Deploy to Tomcat
FROM tomcat:9.0-jdk17-corretto
# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*
# Copy the WAR file from the build stage
COPY --from=build /app/target/StudentCourseManagement.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
