#! /bin/bash
set -e

if [ $# -ne 2 ];then
        echo "USEAGE: $0 <user> <recover_cmd> "
        exit 1
fi

user=$1
revover_cmd=$2

nohup su $user -l -s /bin/bash -c '$revover_cmd' 1>/dev/null 2>&1 &



