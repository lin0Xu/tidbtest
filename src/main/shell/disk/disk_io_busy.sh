#! /bin/bash
## disk io busy tools: fio, installion as following:
# METHOD 1:
# apt-get update
# apt-get upgrade
# apt-get install build-install
# apt-get install gcc
# apt-get install make
# wget http://brick.kernel.dk/snaps/fio-2.2.5.tar.gz
# tar zxvf fio-2.2.5.tar.gz & cd fio-2.2.5
# ./configure
# make & make install
###
# METHOD 2:
#sudo apt-get install libaio-dev
#sudo apt-get install libgtk2.0-dev
#git clone git://git.kernel.dk/fio.git
#cd fio
#./configure  --enable-gfio
#make & make install
#####################################################

# type:read/write/rw/randread/randwrite/randrw

set -e
if [ $# -ne 2 ];then
        echo "USEAGE: $0 <device_tmp_file> <type>"
        exit 1
fi

device_tmp_file=$1
type=$2

nohup fio -filename=${device_tmp_file} -direct=1 -iodepth 1 -thread -rw=${type} -ioengine=psync -bs=20k -size=1G -numjobs=999 -group_reporting -name=faultInj 1>/dev/null 2>&1 && rm -rf ${device_tmp_file} &

sleep 0.4s
fio_ps_num=`ps aux|grep fio |grep -v grep | grep ${device_tmp_file} |grep ${type} |grep -v $0 | wc -l`

if [ ${fio_ps_num} -eq 0 ]; then
    echo "$(date "+%Y-%m-%d %H:%M:%S") ## revoke disk io busy FAILED."
    exit 1;
else
    echo "$(date "+%Y-%m-%d %H:%M:%S") ## revoke disk io busy SUCCEED."
    exit 0;
fi
