#!/bin/bash

if type -p java; then
    _java=java  
elif [[ -n $JAVA_HOME ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    _java="$JAVA_HOME/bin/java"
else
    echo "*** No Java JRE found: Install Java 1.8 or greater and then try again ***"
    exit 1
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo Java version "$version"
fi

IFS='.' 
read -ra V <<<"$version"
v0=${V[0]}
v1=${V[1]}
IFS='' 

if [ $v0 = "1" ] && [ $v1 = "8" ]; then\
    . run-java8.sh
elif [ $v0 = "1" ] && [ $v1 < "8" ]; then
    echo "*** LightSide requires Java 1.8 or greater ***"
    exit 1
elif [ $v0 = "1" ] && [ $v1 > "8" ]; then
    . run-java9plus.sh
elif [ $v0 > "1" ]; then
    . run-java9plus.sh
else 
    echo "*** Invalid Java version ***"
    exit 1
fi 
