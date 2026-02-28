FROM tomcat:11.0-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

# Exploded WAR layout from Eclipse project
COPY src/main/webapp/ /usr/local/tomcat/webapps/forum/
COPY src/main/java/ /tmp/src/
ADD https://jdbc.postgresql.org/download/postgresql-42.7.4.jar /usr/local/tomcat/webapps/forum/WEB-INF/lib/postgresql-42.7.4.jar
RUN mkdir -p /usr/local/tomcat/webapps/forum/WEB-INF/classes \
    && find /tmp/src -name "*.java" > /tmp/sources.txt \
    && javac -encoding UTF-8 \
      -cp "/usr/local/tomcat/lib/*:/usr/local/tomcat/webapps/forum/WEB-INF/lib/*" \
      -d /usr/local/tomcat/webapps/forum/WEB-INF/classes \
      @/tmp/sources.txt \
    && rm -rf /tmp/src /tmp/sources.txt

EXPOSE 8080
