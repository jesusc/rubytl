#!/bin/sh
if [ "$1" == "" ]; then
  echo "call with release or integration build target in build-RDT.xml" ;
  exit 1
fi
#echo "Make sure xvfb is running - otherwise the tests won't run. Start with"
Xvfb -nolisten tcp :1 &
export DISPLAY=localhost:1
cmd="ant -f build-RDT.xml $1"
logfile=build.$1.log
echo Starting build: $cmd, see $logfile
$cmd > $logfile

