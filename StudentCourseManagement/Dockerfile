FROM tomcat:9.0-jdk17-corretto
# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy our built WAR file to the ROOT of Tomcat
COPY target/StudentCourseManagement.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
