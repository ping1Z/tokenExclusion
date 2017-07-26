#!/bin/bash


# Change this to your netid
netid=yxz164731

#
# Root directory of your project
PROJDIR=$HOME/cs6378/p2

LOGDIR=$PROJDIR/logs

EXDIR=$PROJDIR/logs

# Generate Timestamp Evaluation Report
REPORT_HEADER="TIMESTAMP, ID, HOST, PORT"
REPORT_NAME="eport_timestamp_evaluation.txt"
echo $REPORT_HEADER > $REPORT_NAME
grep -E 'ME_REPORT_TIMESTAMP' $LOGDIR/*.log |
(
    READ_NODE=0
    while read line 
    do
	timestamp=$( echo $line | awk '{ print $5 }' )
        id=$( echo $line | awk '{ print $6 }' )
	host=$( echo $line | awk '{ print $7 }' )
	port=$( echo $line | awk '{ print $8 }' )
	
	echo $timestamp, $id, $host, $port >> $REPORT_NAME
    done 
) 

# Generate Throughput Report
REPORT_HEADER="ID, TIME"
REPORT_NAME="report_throughput.txt"
echo $REPORT_HEADER > $REPORT_NAME
grep -E 'ME_REPORT_APP' $LOGDIR/*.log |
(
    READ_NODE=0
    while read line
    do
	id=$( echo $line | awk '{ print $5 }' )
    time=$( echo $line | awk '{ print $6 }' )

	echo $id, $time >> $REPORT_NAME
    done 
)  

# Generate Response Time Report
REPORT_HEADER="ID, REQUEST_NUM, TIME"
REPORT_NAME="report_responseTime.txt"
echo $REPORT_HEADER > $REPORT_NAME
grep -E 'ME_REPORT_RESPONSE' $LOGDIR/*.log |
(
    READ_NODE=0
    while read line 
    do
	id=$( echo $line | awk '{ print $5 }' )
    num=$( echo $line | awk '{ print $6 }' )
    time=$( echo $line | awk '{ print $7 }' )
	
	echo $id, $num, $time >> $REPORT_NAME
    done 
)

# Generate Execution Time Report
REPORT_HEADER="ID, REQUEST_NUM, TIME"
REPORT_NAME="report_executionTime.txt"
echo $REPORT_HEADER > $REPORT_NAME
grep -E 'ME_REPORT_EXECUTION' $LOGDIR/*.log |
(
    READ_NODE=0
    while read line 
    do
	id=$( echo $line | awk '{ print $5 }' )
    num=$( echo $line | awk '{ print $6 }' )
    time=$( echo $line | awk '{ print $7 }' )
	
	echo $id, $num, $time >> $REPORT_NAME
    done 
) 

# Generate Message Complexity Report
REPORT_HEADER="ID, CONTENT"
REPORT_NAME="report_MessageComplexity.txt"
echo $REPORT_HEADER > $REPORT_NAME
grep -E 'ME_REPORT_MSG' $LOGDIR/*.log |
(
    READ_NODE=0
    while read line 
    do
	id=$( echo $line | awk '{ print $5 }' )
    content=$( echo $line | awk '{ print $6 }' )
	
	echo $id, $content >> $REPORT_NAME
    done 
) 