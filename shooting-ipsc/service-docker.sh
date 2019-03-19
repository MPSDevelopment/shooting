#!/bin/bash

cmd="/usr/bin/java 
	-server  
	-Djava.net.preferIPv4Stack=true
	-Xms1g 
	-Xmx8g
	-XX:+HeapDumpOnOutOfMemoryError 
	-XX:HeapDumpPath=heapdumps/heap_dump_`date \"+%d.%m.%y\"`.hprof  
	-XX:+CMSParallelRemarkEnabled 
	-XX:+UseCMSInitiatingOccupancyOnly 
	-XX:CMSInitiatingOccupancyFraction=70 
	-XX:+ScavengeBeforeFullGC 
	-XX:+CMSScavengeBeforeRemark
	-verbose:gc 
	-cp ipsc.jar tech.shooting.ipsc.IpscApplication"

$cmd