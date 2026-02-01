#!/bin/sh
echo "MicroService Starting..."
# 环境参数处理
binPath=$(dirname $0)
cd $binPath
binPath=$(pwd)
push_home=`cd ../;pwd`
echo $push_home
for i in `ls $push_home/libs`
  do
    JAVA_OPTIONS=$push_home/libs/$i
  done

echo "$JAVA_OPTIONS"
  REST_PORT=8003
  DSF_PORT=7003
  export REST_PORT DSF_PORT

java $JAVA_OPTS -XX:MaxMetaSpaceSize=512m $CATALINA_OPTS -Denv.port=$1 -jar $JAVA_OPTIONS
echo "MicroService End Of Start..."