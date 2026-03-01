FROM tomcat:11.0-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

# Exploded WAR layout from Eclipse project (serve as ROOT)
COPY src/main/webapp/ /usr/local/tomcat/webapps/ROOT/
COPY build/classes/ /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/

# Keep /forum path available too
RUN ln -s /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/forum

EXPOSE 8080
