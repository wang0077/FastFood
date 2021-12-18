#!/bin/bash
SERVER_NAME="product-center"

echo "查询进程id-->$SERVER_NAME"
PID=$(ps -ef | grep "$SERVER_NAME" | awk '{print $2}')
echo "得到进程ID：$PID"
echo "结束进程"
for id in $PID
do
    kill -9 $id
    echo "killed $id"
done
echo "结束进程完成"