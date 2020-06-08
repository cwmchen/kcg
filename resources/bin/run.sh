#!/bin/sh

export SERVER="kcg-dashboard"
export JAVA_HOME
# 可以手动指定 java 环境地址 如：JAVA="/opt/java/bin/java"
export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=`cd $(dirname $0)/..; pwd`
export DEFAULT_SEARCH_LOCATIONS="classpath:/,classpath:/config/,file:./,file:./config/"
export CUSTOM_SEARCH_LOCATIONS=${DEFAULT_SEARCH_LOCATIONS},file:${BASE_DIR}/conf/


function start() {
	JAVA_MAJOR_VERSION=$($JAVA -version 2>&1 | sed -E -n 's/.* version "([0-9]*).*$/\1/p')
	if [[ "$JAVA_MAJOR_VERSION" -ge "9" ]] ; then
	  JAVA_OPT="${JAVA_OPT} -Xlog:gc*:file=${BASE_DIR}/logs/${SERVER}_gc.log:time,tags:filecount=10,filesize=102400"
	else
	  JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${JAVA_HOME}/lib/ext"
	  JAVA_OPT="${JAVA_OPT} -Xloggc:${BASE_DIR}/logs/${SERVER}_gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
	fi

	JAVA_OPT="${JAVA_OPT} -Xms512m -Xmx512m -Xmn256m"
	JAVA_OPT="${JAVA_OPT} -Dfile.encoding=utf-8"
	JAVA_OPT="${JAVA_OPT} -Dloader.path=${BASE_DIR}/lib/ -jar ${BASE_DIR}/lib/${SERVER}.jar"
	JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
	JAVA_OPT="${JAVA_OPT} --spring.config.location=${CUSTOM_SEARCH_LOCATIONS}"
	JAVA_OPT="${JAVA_OPT} --logging.config=${BASE_DIR}/conf/logback-spring.xml"
	JAVA_OPT="${JAVA_OPT} --logging.path=${BASE_DIR}/logs"
	JAVA_OPT="${JAVA_OPT} --server.max-http-header-size=524288"

	if [ ! -d "${BASE_DIR}/logs" ]; then
	  mkdir ${BASE_DIR}/logs
	fi

	echo "$JAVA ${JAVA_OPT}"

	# check the start.out log output file
	if [ ! -f "${BASE_DIR}/logs/start.out" ]; then
	  touch "${BASE_DIR}/logs/start.out"
	fi
	# start
	echo "$JAVA ${JAVA_OPT}" > ${BASE_DIR}/logs/start.out 2>&1 &
	nohup $JAVA ${JAVA_OPT} ${SERVER} >> ${BASE_DIR}/logs/start.out 2>&1 &
	echo "${SERVER} 正在启动，您可以检查 ${BASE_DIR}/logs/start.out 获取信息"
}

function stop() {
	pid=`ps ax | grep -i ${SERVER} |grep java | grep -v grep | awk '{print $1}'`
	if [ -z "$pid" ] ; then
		echo "没有运行${SERVER}服务."
		exit -1;
	fi

	echo "${SERVER}服务(${pid}) 正在运行..."
	kill ${pid}

	echo "关闭${SERVER}服务(${pid})已经成功"
}

function restart(){
	pid=`ps ax | grep -i ${SERVER} |grep java | grep -v grep | awk '{print $1}'`
	
	if [ ! -z "$pid" ]; then
		stop
	fi
	
	start
}

function help() {
	echo "1、启动服务使用 start   参数"
	echo "2、关闭服务使用 stop    参数"
	echo "3、重启服务使用 restart 参数"
	echo "4、没有参数默认启动服务"
}

function end(){
	echo "没有进行任何操作,请检查您的输入参数，您可以使用 help 获取帮助！"
}

export RUN=$1

case "$RUN" in 
    start)
        start
        ;;
    stop)
        stop
        ;;
	restart)
        restart
        ;;
    help)
        help
        ;;
	*)
		end
		;;
esac


