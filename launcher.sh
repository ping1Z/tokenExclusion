#!/bin/bash
# Change this to your netid
netid=yxz164731

#
# Root directory of your project
PROJDIR=$HOME/cs6378/p2

#
# This assumes your config file is named "config.txt"
# and is located in your project directory
#
CONFIG=$PROJDIR/config.txt

#
# Directory your java classes are in
#
BINDIR=$PROJDIR/bin

#
# Your main project class
#
PROG=MutualExclusionApp

#
# Your main project class
#
LOG=$PROJDIR/logs

n=-1


cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    while read line 
    do
        if [ "$n" -gt -1 ];then
            host=$( echo $line | awk '{ print $2 }' )
            ssh $netid@$host java -cp $BINDIR $PROG $CONFIG $n $LOG &
           
        fi
        n=$(( n + 1 ))
    done
   
)


