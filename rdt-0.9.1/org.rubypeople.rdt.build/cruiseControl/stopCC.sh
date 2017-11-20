#!/bin/sh
pgid=`ps -ef | grep [s]tartCC | awk '{print $2}'`
kill `ps -eo pgid,pid | grep $pgid | awk '{print $2}'`
