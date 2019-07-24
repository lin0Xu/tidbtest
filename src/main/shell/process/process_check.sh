#! /bin/bash

if [ $# -ne 2 ];then
        echo "USEAGE: $0 <service_flag> <expected_num>"
        exit 1
fi

service_flag=$1
expected_num=$2

ps_num=`ps aux|grep ${service_flag} | grep -v grep | grep -v $0 | wc -l`

if [ ${ps_num} -ne ${expected_num} ];then
    echo "$(date "+%Y-%m-%d %H:%M:%S") process num:${ps_num}, expected process num:${expected_num}, Check: NOT_PASS."
    exit 1;
else
    echo "$(date "+%Y-%m-%d %H:%M:%S") process num:${ps_num}, expected process num:${expected_num}, Check: PASS."
    exit 0;
fi
