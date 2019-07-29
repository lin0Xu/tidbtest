#!/bin/bash

DEFAULTDEV=`/sbin/ifconfig  -s |grep -v 'Iface' | awk '{print $1}'`

MESSAGE1="usage:\n
[-hv]\n 
[ -i interfaces, input eth1 or \"eth1 eth2\", default:$DEFAULTDEV ]"


########### handle input options ##############
while getopts i: option
do
    case $option in 
    i)
        dev=$OPTARG;;
    h | v)
        echo -e $MESSAGE1
        exit;;
    *)
        echo "Error option: $option"
	echo -e $MESSAGE1 
	exit
    esac
done

if [ -z "$dev" ]; then
    dev=$DEFAULTDEV
fi


for DEV in $dev
do
    echo "--------clean qdisc in $DEV--------"
    ##### delete root qdisc(including subclasses and leaf qdiscs)
    command="sudo tc qdisc del dev $DEV root"
    echo $command
    $command
done


echo 'success!'













