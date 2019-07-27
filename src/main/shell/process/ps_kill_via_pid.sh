#! /bin/bash
set -e
if [[ $# -ne 2 ]]; then
    echo "Usage: $0 <pid> <signal>"
    exit 1;
fi
# kill Signal set:
# HUP    1    终端断线, 对进程提供服务无影响；
# INT     2    中断（同 Ctrl + C），进程会退出；
# QUIT    3    退出（同 Ctrl + \）.对进程提供服务无影响
# TERM   15    终止， 进程会退出
# KILL    9    强制终止
# CONT   18    继续（与STOP相反， fg/bg命令）
# STOP    19    暂停（同 Ctrl + Z）

# flag Mapping to SERVICE <pid>.
pid=$1
signal=$2

log_print(){
    LOG_VAL=$1
    echo "$(date "+%Y-%m-%d %H:%M:%S") "${LOG_VAL}
}

# return 1 means FAILED, 0 means SUCCEED.
function ps_kill(){
    pid=$1
    signal=$2

    kill -$signal $pid

    case $signal in
        19 )
            sleep 0.3s
            ps_status=`ps aux|grep -v $0 |grep $pid | grep -v grep | awk '{print$8}'`
            # fault injection succeed.
            if [[ $ps_status == T* ]]; then
                return 0;
            # fault injection failed.
            else
                return 1;
            fi
            ;;
        18 )
            sleep 0.3s
            ps_status=`ps aux|grep -v $0 |grep $pid | grep -v grep | awk '{print$8}'`
            # fault injection succeed.
            if [[ $ps_status == S* ]]; then
                return 0;
            # fault injection failed.
            else
                return 1;
            fi
            ;;
        9 )
            sleep 0.8s
            ps_num=`ps aux|grep -v $0 |grep $pid | grep -v grep | wc -l`

            if [[ $ps_num == 0 ]]; then
                return 0;
            else
                return 1;
            fi
            ;;
        15 | 2 )
            sleep 1s
            ps_num=`ps aux|grep -v $0 |grep $pid | grep -v grep | wc -l`
            if [[ $ps_num -eq 0 ]]; then
                return 0;
            elif [[ $ps_num -eq 1 ]]; then
                ps_status=`ps aux|grep $pid | grep -v grep |grep -v $0 | awk '{print$8}'`
                if [[ $ps_status == S* ]]; then
                    return 1;
                elif [[ $ps_status == R* ]]; then
                    return 1;
                else
                    return 0;
                fi
            fi
            ;;

        * )
            return 1;
            ;;
    esac
}

log_print "Begin process fault injection, pid: ${pid}, signal: ${signal}"
kill_status=`ps_kill $pid $signal`

if [[ $kill_status -eq 0 ]]; then
    log_print "Process fault injection SUCCEED, pid: ${pid}, signal: ${signal}"
    exit 0;
else
    log_print "Process fault injection FAILED, pid: ${pid}, signal: ${signal}"
#   fault_uuid=`cat /proc/sys/kernel/random/uuid`
    exit 1;
fi