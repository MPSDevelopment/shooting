FROM openjdk:11-jre

#RUN mkdir /usr/share/servers
#RUN mkdir /usr/share/servers/ipsc

# Add libs
ADD lib /usr/share/servers/ipsc/lib

# Add shell script
ADD service-docker.sh /usr/share/servers/ipsc/service.sh

# Add the service itself
# ARG JAR_FILE
COPY ipsc.jar /usr/share/servers/ipsc/ipsc.jar

EXPOSE 9901

# ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/servers/ipsc/ipsc.jar"]

# RUN chmod 755 /usr/share/servers/ipsc/service.sh

#VOLUME /usr/share/servers/ipsc/logs
#ENTRYPOINT ["sh", "service.sh"]

WORKDIR /usr/share/servers/ipsc

# CMD ["java", "-Dspring.profiles.active=docker", "-jar", "ipsc.jar"]
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "ipsc.jar"]