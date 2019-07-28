#! /bin/bash

set -e
if [ $# -ne 1 ];then
        echo "USEAGE: $0 <file_name>"
        exit 1
fi

file_name=$1

if [[ ! -f ${file_name} ]]; then
    echo -n ${file_name}
	exit 0;
fi

rm -rf ${file_name}

if [[ ! -f ${file_name} ]]; then
    echo -n ${file_name}
	exit 0;
else
    exit 1;
fi