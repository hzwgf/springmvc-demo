#!/bin/sh

cd `dirname $0`

BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
LIB_DIR=$DEPLOY_DIR/lib
CONF_DIR=$DEPLOY_DIR/conf

##SERVER_NAME=`sed '/app.name/!d;s/.*=//' conf/nsq.properties | tr -d '\r'`
SERVER_NAME=springmvc-demo

JMX_PORT=9000

case $1 in
start)
	echo  "Starting $SERVER_NAME ... "
	JAVA_OPTS=" -server -Xmx2g -Xms2g -Xmn256m -Xss256k 
								-XX:MaxMetaspaceSize=128m  
								-XX:+DisableExplicitGC 
								-XX:+UseConcMarkSweepGC 
								-XX:+CMSParallelRemarkEnabled 
								-XX:LargePageSizeInBytes=128m 
								-XX:+UseFastAccessorMethods 
								-XX:+UseCMSInitiatingOccupancyOnly 
								-XX:CMSInitiatingOccupancyFraction=70 "
								
	JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=$JMX_PORT 
					-Dcom.sun.management.jmxremote.ssl=false 
					-Dcom.sun.management.jmxremote.authenticate=false "
	LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
	cd bin
	nohup java $JAVA_OPTS $JAVA_JMX_OPTS -classpath $CONF_DIR:$LIB_JARS com.youzan.Launcher >/dev/null 2>&1 &
	
	COUNT=0
	while [ $COUNT -lt 1 ]; do    
	    echo -e ".\c"
	    sleep 1 
	    COUNT=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
	    if [ $COUNT -gt 0 ]; then
	        break
	    fi
	done
	
	echo "start OK!"
	PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
	echo "PID: $PIDS"
	;;
	
stop)
	PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" |awk '{print $2}'`
	if [ -z "$PIDS" ]; then
	    echo "ERROR: The $SERVER_NAME does not started!"
	    exit 1
	fi
	
	echo -e "Stopping the $SERVER_NAME ...\c"
	for PID in $PIDS ; do
	    kill $PID > /dev/null 2>&1
	done
	
	COUNT=0
	while [ $COUNT -lt 1 ]; do    
	    echo -e ".\c"
	    sleep 1
	    COUNT=1
	    for PID in $PIDS ; do
	        PID_EXIST=`ps -f -p $PID | grep java`
	        if [ -n "$PID_EXIST" ]; then
	            COUNT=0
	            break
	        fi
	    done
	done
	
	echo "stop OK!"
	echo "PID: $PIDS"
	;;
	
*)
    echo "ignore ... "
    ;;

esac 

