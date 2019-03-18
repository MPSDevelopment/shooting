FROM openjdk:11-jre

RUN mkdir /usr/share/servers/ipsc
WORKDIR /usr/share/servers/ipsc

# Add libs
ADD target/lib           /usr/share/servers/ipsc/lib

# Add shell script
ADD target/service-docker.sh           /usr/share/servers/ipsc/service.sh

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/servers/ipsc/ipsc.jar

EXPOSE 9901

# ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/servers/ipsc/ipsc.jar"]

ENTRYPOINT ["sh", "service.sh"]