#!/bin/bash

MAXHEAP="8g"
OS_ARGS=""
OTHER_ARGS=""
#OTHER_ARGS="-XX:+UseConcMarkSweepGC"

if [ `uname` == "Darwin" ]; then
    OS_ARGS="-Xdock:icon=toolkits/icons/bulbs/bulb_128.png -Xdock:name=LightSide"
elif [ `uname` == "Linux" ]; then
    OS_ARGS="-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel"
fi

if [[ -z "$1" ]]; then
    echo 'no DISPLAY variable set. Using DISPLAY=:0.0...' 
    export DISPLAY=:0.0
fi

MAIN_CLASS="edu.cmu.side.Workbench"

JAR="LightSide.jar"

ADD_OPENS="java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED"
    
java -cp $JAR $OS_ARGS -Xmx$MAXHEAP $OTHER_ARGS -splash:toolkits/icons/logo.png $MAIN_CLASS $@

