#! /bin/bash
set -e
if [ $# -ne 2 ];then
        echo "USEAGE: $0 <service_flag> <signal> "
        exit 1
fi

flag=$1
signal=$2

log_print(){
    LOG_VAL=$1
    echo "$(date "+%Y-%m-%d %H:%M:%S") "${LOG_VAL}
}

ps_num=`ps aux | grep $flag | grep -v grep | grep -v $0 | wc -l`
pid=`ps aux | grep $flag | grep -v grep | grep -v $0 | awk '{print $2}'`

if [ ${ps_num} -eq 1 ]; then
    log_print "pid: $pid, BEGIN kill -${signal} $pid"
    nohup kill -$signal $pid 1>/dev/null 2>&1 &
    log_print "kill -${signal} $pid Finished."
    exit 0;
else
    log_print "process num larger than 1. pids: $pid, BEGIN batch kill -${signal} $pid"
    nohup echo $pid | xargs kill -${signal} 1>/dev/null 2>&1 &
    log_print "batch kill -${signal} $pid Finished."
    exit 0;
fi