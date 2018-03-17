#!/bin/sh

mvn clean -DskipTests=true package

cd target

JAVA_OPTS="-Xms3072m -Xmx3072m -Xss128k -XX:NewRatio=1 -XX:SurvivorRatio=9 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5 -XX:MaxTenuringThreshold=15"

CLASS_PATH="./;./API-Server-1.0-SNAPSHOT.jar"

nohup java -Dfile.encoding=UTF-8 $JAVA_OPTS -classpath $CLASS_PATH io.nuls.main.Main >nohup.log 2>&1 &

exit 0
