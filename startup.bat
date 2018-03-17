@ECHO OFF

SET PACKAGE=mvn clean -DskipTests=true package
call %PACKAGE%

cd %cd%/target

SET JAVA_OPTS=-Xms3072m -Xmx3072m -Xss128k -XX:NewRatio=1 -XX:SurvivorRatio=9 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5 -XX:MaxTenuringThreshold=15

SET CLASS_PATH=./;./API-Server-1.0-SNAPSHOT.jar

java %JAVA_OPTS% -Dfile.encoding=UTF-8 -classpath %CLASS_PATH% io.nuls.main.Main

PAUSE