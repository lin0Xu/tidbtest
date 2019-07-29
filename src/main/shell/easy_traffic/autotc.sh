#!/bin/bash

DEFAULTDEV="`/sbin/ifconfig  -s |grep -v 'Iface' | awk '{print $1}'`"   #default interfaces
DEFAULTDELAY="0ms"  #default delay time
DEFAULTLOSS="0%"   #default package loss rate
DEFAULTBANDWIDTH="200mbps"   #default rate and ceil

MESSAGE1="usage:\n
[-hv]\n 
[ -i interfaces, input eth1 or \"eth1 eth2\", default:$DEFAULTDEV ] \n
[ -d delay time(s,ms,us), default:$DEFAULTDELAY ]\n
[ -l package loss rate, default:$DEFAULTLOSS ]\n
[ -b ceil bindwidth(bps,kbps,mbps), default:$DEFAULTBANDWIDTH ]\n
[ filter expression, key1 value1 [conjunction key2 value2...]. legal key:src|dst|sport|dport. legal conjunction:and|or, default and]"


########### handle input options ##############
while getopts i:d:l:b:hv option
do
    case $option in 
    i)
        dev=$OPTARG;;
    d)
        delay=$OPTARG;;
    l)
        loss=$OPTARG;;
    b)  
        bindwidth=$OPTARG;;
    h | v)
        echo -e $MESSAGE1
        exit 0;;
    *)
        echo "Error option: $option"
	echo -e $MESSAGE1 
	exit 1
    esac
done

if [ -z "$dev" ]; then
    dev=$DEFAULTDEV
fi
if [ -z "$delay" ]; then
    delay=$DEFAULTDELAY
fi
if [ -z "$loss" ]; then
    loss=$DEFAULTLOSS
fi
if [ -z "$bindwidth" ]; then
    bindwidth=$DEFAULTBANDWIDTH
fi



########### handle filter expression #############
shift $[ OPTIND - 1 ]
count=1
if [ $# -eq 0 ];then
    echo "Error: miss filter expression"
    echo -e $MESSAGE1
    exit 1
fi


function checkip {
    regex_ip="^(2[0-4][0-9]|25[0-5]|1[0-9][0-9]|[1-9]?[0-9])(\.(2[0-4][0-9]|25[0-5]|1[0-9][0-9]|[1-9]?[0-9])){3}$";
    if [[ $1 =~ $regex_ip ]];then
        echo 0
    else
        echo 1
    fi
}
function checkport {
    if [[ $1 =~ ^[0-9]+$ ]] && [ $1 -ge 0 ] && [ $1 -le 65536 ];then
        echo 0
    else
        echo 1
    fi
}

while [ $# -gt 0 ]
do
    case $1 in
    src | dst)
        if [ `checkip $2` -eq 0 ];then
            condition[$count]="${condition[$count]} match ip $1 $2/32"
            shift 2
        else
            echo "Error,illegal host: $2"
            exit 1
        fi;;
    sport | dport)
        if [ `checkport $2` -eq 0 ];then
            condition[$count]="${condition[$count]} match ip $1 $2 0xffff"
            shift 2
        else
            echo "Error,illegal port: $2"
            exit 1
        fi;;
    and)
        shift 1;;
    or)
        count=$[$count + 1]
        shift 1;;
    *)
        echo "Error filter expression: $@"
        echo -e $MESSAGE1
        exit 1;;
    esac
done

echo interface: $dev
echo delay: $delay
echo loss: $loss
echo bindwidth: $bindwidth
for ((i = 1; i <= count; i++))
do  
    if [ -n "${condition[$i]}" ];then
        echo filter: ${condition[$i]}
    fi
done

############ set tc qdisc  ################
ERRTRAP()
{
    echo "Error occurred when running tc: `sed -n "$1"p $0`"
    echo "EXIT CODE:$?, LINE:$1"
    exit 1
}
trap 'ERRTRAP $LINENO' ERR


for DEV in $dev
do
    echo "--------tc set in $DEV--------"
    ##### delete root qdisc(including subclasses and leaf qdiscs)
    #command="sudo tc qdisc del dev $DEV root" 
    #echo $command 
    #$command 1>tc.log 2>&1  

    ##### add root qdisc：htb
    command="sudo tc qdisc add dev $DEV root handle 1: htb default 11"
    echo $command 
    $command

    ##### add first class(default class) on root qdisc 
    command="sudo tc class add dev $DEV parent 1:0 classid 1:11 htb rate $DEFAULTBANDWIDTH ceil $DEFAULTBANDWIDTH"
    echo $command 
    $command

    ##### add second class on root qdisc
    command="sudo tc class add dev $DEV parent 1:0 classid 1:12 htb rate $bindwidth ceil $bindwidth"
    echo $command 
    $command

    ##### add qdisc(delay|loss) on second class 	
    command="sudo tc qdisc add dev $DEV parent 1:12 handle 10: netem delay $delay loss $loss  "
    echo $command
    $command

    ##### filter package at root
    for ((i = 1; i <= count; i++))
    do
        if [ -n "${condition[$i]}" ];then 
            command="sudo tc filter add dev $DEV protocol ip parent 1:0  prio 1 u32 ${condition[$i]} flowid 1:12"
            echo "$command"
            $command
        fi 
    done
done

echo -e "\nsuccess!  test it by telnet!\n"
exit 0
