#!/bin/sh

mvn clean -DskipTests=true package

cd target

if [ ! -d "logs" ]; then
    mkdir logs
fi
status=$?
if [ $status -ne 0 ]; then
    echo "start the Server failed."
    exit $status
fi

JAVA_OPTS="-Xms3072m -Xmx3072m -Xss160k -XX:NewRatio=1 -XX:SurvivorRatio=9 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5 -XX:MaxTenuringThreshold=15"

CLASS_PATH="./:./API-Server-1.0-SNAPSHOT.jar"

echo "now starting the API-Server..."

# (java -Dfile.encoding=UTF-8 $JAVA_OPTS -classpath $CLASS_PATH io.nuls.main.Main &)
nohup java -Dfile.encoding=UTF-8 $JAVA_OPTS -classpath $CLASS_PATH io.nuls.main.Main >./logs/starting.log 2>&1 &

if [ $? -ne 0 ]; then
    echo "start failed."
else
    echo "start completed."
fi
exit 0
