#!/bin/bash
serverName="product-center"
suffix="-0.0.1-SNAPSHOT"
type=".jar"
cd ./target || exit
targetPath=$(pwd)


echo "==================================="
echo "============传输最新JAR包==========="
echo "==================================="
# shellcheck disable=SC2086
scp $targetPath/$serverName$suffix$type root@106.55.239.37:/root/project


ssh root@106.13.206.235 << eeooff
cd /root/project
echo "==================================="
echo "=============停止现有应用==========="
echo "==================================="
./stop.sh
echo "==================================="
echo "=============启动最新应用==========="
echo "==================================="
./start.sh
exit
eeooff

exit

