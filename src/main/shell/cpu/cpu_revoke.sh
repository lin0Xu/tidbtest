#! /bin/bash
set -e

if [ $# -ne 1 ];then
        echo "USEAGE: $0 <load_tool>"
        exit 1
fi
load_tool=$1

nohup ps aux|grep "${load_tool}" |grep -v grep |awk '{print $2}' |xargs kill 1>/dev/null 2>&1 &

if [ $? -eq 0 ];then
    echo "$(date "+%Y-%m-%d %H:%M:%S") ## revoke cpu load, kill ${load_tool} succeed."
    exit 0
else
    echo "$(date "+%Y-%m-%d %H:%M:%S") ## revoke cpu load, kill ${load_tool} failed."
    exit 1
fi

# bash cpu_revoke.sh stress