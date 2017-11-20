#!/bin/bash
Xvfb -nolisten tcp :1 &
export DISPLAY=:1
~/cruisecontrol/cruisecontrol.sh -webport 8080 -jmxport 8000 $@