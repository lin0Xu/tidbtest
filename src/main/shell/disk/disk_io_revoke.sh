#! /bin/bash
set -e

if [ $# -ne 2 ];then
        echo "USEAGE: $0 <device_dir> <type> <inject_shll_flag>"
        exit 1
fi

flag1=$1
flag2=$2

ps_num=`ps aux | grep "filename=$flag1" | grep "rw=$flag2" |grep -v grep | grep -v $0 |grep -v ${inject_shll_flag}| grep fio | wc -l`
pid=`ps aux | grep "filename=$flag1" | grep "$flag2" | grep -v grep | grep -v $0 |grep -v ${inject_shll_flag} |grep fio | awk '{print $2}'`

if [ ${ps_num} -eq 1 ]; then
    echo "$(date "+%Y-%m-%d %H:%M:%S") pid: $pid, BEGIN kill -9 $pid"
    nohup kill -9 $pid 1>/dev/null 2>&1 &
    echo "$(date "+%Y-%m-%d %H:%M:%S") END kill -9 $pid"
    exit 0;
else
    echo "$(date "+%Y-%m-%d %H:%M:%S") process num larger than 1. pids: $pid, BEGIN batch kill -9 $pid"
    nohup echo $pid | xargs kill -9 1>/dev/null 2>&1 &
    echo "$(date "+%Y-%m-%d %H:%M:%S") END batch kill -9 $pid"
    exit 0;
fi