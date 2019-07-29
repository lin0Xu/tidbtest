#! /bin/bash

set -e

if [ $# -ne 1 ];then
        echo "USEAGE: $0 <load_percent> "
        exit 1
fi

load_percent=$1

## 以loadavg_1为参考，增加cpu load；
cpu_busy_simulate() {
    CPU_CORES=`/bin/cat /proc/cpuinfo |grep processor | wc -l`
    curr_loadAvg1=`/bin/cat /proc/loadavg | awk '{print $1}'`
    curr_loadAvg5=`/bin/cat /proc/loadavg | awk '{print $2}'`
#    curr_loadAvg15=`/bin/cat /proc/loadavg | awk '{print $3}'`
    current_time=$(date "+%Y-%m-%d %H:%M:%S")
    ## 构造cpu高负载时长： [60, 630]s；
#    rnd_cpu_busy_time=$(($RANDOM%600+60))
    echo "$(date "+%Y-%m-%d %H:%M:%S") current loadavg1: " ${curr_loadAvg1}
#    echo "$(date "+%Y-%m-%d %H:%M:%S") current loadavg5: " ${curr_loadAvg5}
#    echo "$(date "+%Y-%m-%d %H:%M:%S") current loadavg15: " ${curr_loadAvg15}
    # curr_loadAvg1取整：${curr_loadAvg1%.*}

    percent=$1
    load_reference=`echo "${CPU_CORES} * $percent" | bc`
    load_reference=${load_reference%.*}

    if [ ${curr_loadAvg5%.*} -lt ${load_reference} ]; then
      gap=$((${load_reference}-${curr_loadAvg5%.*}))
      echo "$(date "+%Y-%m-%d %H:%M:%S") curr_loadAvg5 less then load_reference, gap=$gap"
#    elif [ ${curr_loadAvg5%.*} -lt $((${CPU_CORES})) ]; then
#      gap=$((${CPU_CORES}-${curr_loadAvg1%.*}))
#      echo "$(date "+%Y-%m-%d %H:%M:%S") curr_loadAvg5 less then CPU_CORE, gap=$gap"
#    elif [ ${curr_loadAvg15%.*} -lt $((${CPU_CORES})) ]; then
#      gap=$((${CPU_CORES}-${curr_loadAvg15%.*}))
#      echo "$(date "+%Y-%m-%d %H:%M:%S") curr_loadAvg15 less then CPU_CORE, gap=$gap"
    else
      gap=1
      echo "$(date "+%Y-%m-%d %H:%M:%S") curr_loadAvg bigger then CPU_CORE, gap=1"
    fi

    echo "$(date "+%Y-%m-%d %H:%M:%S") + + CPU STRESS inject BEGIN + + , cpu load gap: $gap "
    nohup stress --cpu $gap --timeout 999999 1>/dev/null 2>&1 &
    #nohup memtester 4G 100 &
        echo "$(date "+%Y-%m-%d %H:%M:%S") + + CPU STRESS inject FINISHED + + , cpu load gap: $gap "
   }

DATE=$(date "+%Y-%m-%d")

echo "## Begin CPU_BUSY jection: $(date "+%Y-%m-%d %H:%M:%S") - - - - - - - - - - - - - - - - - -"

## 异常构造
cpu_busy_simulate $load_percent

echo "$(date "+%Y-%m-%d %H:%M:%S") > > > > > End simulate exception, current time: $(date "+%Y-%m-%d %H:%M:%S")"

echo "## End CPU_BUSY jection: $(date "+%Y-%m-%d %H:%M:%S") - - - - - - - - - - - - - - - - - - -"



# ##########
# #! /bin/bash
# ## 故障注入依赖 stress工具;
# set -e
# if [ $# -ne 1 ];then
#         echo "USEAGE: $0 <load_percent> "
#         exit 1
# fi

# load_percent=$1

# log_print(){
#     LOG_VAL=$1
#     echo "$(date "+%Y-%m-%d %H:%M:%S") "${LOG_VAL}
# }

# ## 以loadavg_5为参考，增加cpu load；
# cpu_busy_simulate() {
#     CPU_CORES=`/bin/cat /proc/cpuinfo |grep processor | wc -l`
#     curr_loadAvg5=`/bin/cat /proc/loadavg | awk '{print $2}'`
#     current_time=$(date "+%Y-%m-%d %H:%M:%S")

#     log_print "current loadavg5: ${curr_loadAvg1}"

#     percent=$1
#     load_reference=`echo "${CPU_CORES} * $percent" | bc`
#     load_reference=${load_reference%.*}

#     if [ ${curr_loadAvg5%.*} -lt ${load_reference} ]; then
#       gap=$((${load_reference}-${curr_loadAvg5%.*}))
#       log_print "urr_loadAvg5 less then load_reference, gap=$gap"
#     else
#       gap=1
#       log_print "curr_loadAvg bigger then CPU_CORE, gap=1"
#     fi

#     log_print "CPU STRESS inject BEGIN, cpu load gap:$gap"
#     nohup stress --cpu $gap --timeout 999999 1>/dev/null 2>&1 &
#     log_print "CPU STRESS inject FINISHED"
#    }

# ## 异常构造
# cpu_busy_simulate $load_percent