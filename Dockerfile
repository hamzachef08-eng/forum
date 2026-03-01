FROM tomcat:11.0-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*
RUN sed -i 's/<Server port="8005"/<Server port="-1"/' /usr/local/tomcat/conf/server.xml

# Exploded WAR layout from Eclipse project (serve as ROOT)
COPY src/main/webapp/ /usr/local/tomcat/webapps/ROOT/
COPY src/main/java/ /tmp/src-java/
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    && find /tmp/src-java -name "*.java" -print0 \
    | xargs -0 javac -encoding UTF-8 \
      -cp "/usr/local/tomcat/lib/*:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib/*" \
      -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    && rm -rf /tmp/src-java

# Keep /forum path available too
RUN ln -s /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/forum

EXPOSE 8080
