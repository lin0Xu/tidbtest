#! /bin/bash

## disk capcacity fill tools: dd
set -e
if [ $# -ne 3 ];then
        echo "USEAGE: $0 <fill_dir> <block_size> <block_count>"
        exit 1
fi

fill_dir=$1
block_size=$2
block_count=$3

if [ $block_count -le 0 ];then
	echo "## $(date "+%Y-%m-%d %H:%M:%S"), block_count illegal."
	exit 1;
fi

nohup dd if=/dev/zero of=${fill_dir} bs=${block_size} count=${block_count} 1>/dev/null 2>&1 &

sleep 0.8s

# if [[ -f ${rnd_tmp_file} ]]; then
if [[ -f ${fill_dir} ]]; then
    echo -n ${fill_dir}
	exit 0;
else
	exit 1;
fi
