FROM tomcat:11.0-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

# Exploded WAR layout from Eclipse project
COPY src/main/webapp/ /usr/local/tomcat/webapps/forum/
COPY build/classes/ /usr/local/tomcat/webapps/forum/WEB-INF/classes/

EXPOSE 8080
