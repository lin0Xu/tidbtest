#! /bin/bash
set -e
if [ $# -ne 2 ];then
        echo "USEAGE: $0 <service_flag> <priority> "
        exit 1
fi

flag=$1
priority=$2

log_print(){
    LOG_VAL=$1
    echo "$(date "+%Y-%m-%d %H:%M:%S") "${LOG_VAL}
}

ps_num=`ps aux | grep $flag | grep -v grep | grep -v $0 | wc -l`
pid=`ps aux | grep $flag | grep -v grep | grep -v $0 | awk '{print $2}'`

if [ ${ps_num} -eq 1 ]; then
    log_print "pid: $pid, BEGIN renice ${priority} -p $pid"
    nohup renice ${priority} -p ${pid} 1>/dev/null 2>&1 &
    log_print "renice ${priority} -p $pid Finished."
    # TODO：需补充renice执行之后，进程优先级的校验，根据校验结果决定exit code.
    exit 0;
else
    log_print "process num larger than 1. pids: $pid, BEGIN renice ${priority} -p $pid"
    nohup echo $pid | xargs kill -${signal} 1>/dev/null 2>&1 &
    log_print "batch renice ${priority} -p $pid Finished."
    exit 0;
fi