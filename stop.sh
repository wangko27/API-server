#!/bin/bash

echo "beginning to stop the Server..."
pid=`ps -ef|grep -i io.nuls.main.Main |grep java|awk '{print $2}'`
status=$?
if [ $status -ne 0 ]; then
    echo "stop the Server failed."
    exit $status
fi

if [ -z $pid ]; then
    echo "Server is not running."
    exit 0
fi

echo "the API-Server's pid is $pid"
kill -9 $pid
status=$?
if [ $status -ne 0 ]; then
    echo "stop the Server failed."
    exit $status
fi
echo "stop the Server completed."

exit 0
