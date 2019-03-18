FROM openjdk:11-jre

RUN mkdir /usr/share/servers/ipsc
WORKDIR /usr/share/servers/ipsc

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/servers/ipsc/ipsc.jar"]

# Add libs
ADD target/lib           /usr/share/servers/ipsc/lib

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/servers/ipsc/ipsc.jar
