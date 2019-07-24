#! /bin/bash
set -e
if [[ $# -ne 1 ]]; then
	echo "Usage: $0 <service_flag>"
fi

flag=$1
pids=`ps aux|grep $flag | grep -v grep | grep -v $0 | awk '{print $2}'`
print $pids