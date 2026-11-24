#!/bin/sh

# start 启动 stop 停止 restart 重启 status 状态
## TODO 修改包名和启动参数
APP_NAME=wety-demo-api-1.0.0.jar
# JVM参数
JVM_OPTS="-Dname=$APP_NAME"
# 应用目录
APP_HOME=$(dirname "$(readlink -f $0)")
# 日志目录
LOG_PATH=$APP_HOME/logs/info.log

# 脚本提示信息
help(){
    echo "-------------------------------------"
    echo ""
    echo "jar所在目录: ${APP_HOME}"
    echo "项目jar包名: ${APP_NAME}"
    echo ""
    echo "你可以使用如下参数进行操作"
    echo "status     查看当前项目运行状态"
    echo "start      启动当前项目"
    echo "stop       停止当前项目"
    echo "restart    重启当前项目"
    echo ""
    echo "-------------------------------------"
}

start(){
    MY_PROFILE=""
    # 如果存在命令行参数，则加入启动命令
    if [ -n "$1" ]; then
        MY_PROFILE="$1"
    fi

    PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | awk '{print $2}')

    if [ x"$PID" != x"" ]; then
        echo "$APP_NAME is running..."
    else
        cd "$APP_HOME" || exit
        nohup java $JVM_OPTS -jar $MY_PROFILE $APP_NAME >> /dev/null 2>&1 &
        echo "Start $APP_NAME success..."

        # 循环等待日志文件生成，同时通过动态转动字符表示等待
        spin='-\|/'
        i=0
        until [ -f "$LOG_PATH"  ]
        do
            i=$(( (i+1) %4 ))
            printf "\r[%s]" ${spin:$i:1}
            sleep 0.1
        done
        printf "\ntail -f %s\n" "$LOG_PATH"

        # 显示日志内容
        tail -f "$LOG_PATH"
    fi
}

stop(){
    echo "Stop $APP_NAME"

    PID=""
    query(){
        PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | awk '{print $2}')
    }
    query
    if [ x"$PID" != x"" ]; then
        kill -TERM "$PID"
        echo "$APP_NAME (pid:$PID) exiting..."
        while [ x"$PID" != x"" ]
        do
            sleep 1
            query
        done
        echo "$APP_NAME exited."
    else
        echo "$APP_NAME already stopped."
    fi
}

restart(){
    stop
    sleep 2
    start "$1"
}

status(){
    PID=$(ps -ef | grep java | grep $APP_NAME | grep -v grep | wc -l)
    if [ "$PID" != 0 ];then
        echo "$APP_NAME is running..."
    else
        echo "$APP_NAME is not running..."
    fi
}

# 参数选项
case $1 in
    "status")
    status
    ;;
    "start")
    start "$2"
    ;;
    "stop")
    stop
    ;;
    "restart")
    restart "$2"
    ;;
    *)
    help
    ;;
esac
