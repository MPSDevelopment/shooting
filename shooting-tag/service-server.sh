#!/bin/bash

cmd="/usr/lib/jvm/jdk-11/bin/java
	-server  
	-Djava.net.preferIPv4Stack=true
	-jar tag.jar"

$cmd