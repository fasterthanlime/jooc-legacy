#!/bin/sh

#if [[ $OOC_DIST == "" ]]; then
#	echo "ERROR: You need to set the OOC_DIST environment variable to where you have installed ooc. (example: export OOC_DIST=/opt/ooc)"
#	exit 1
#fi
#if [[ ! -e $OOC_DIST/dist/ooc.jar ]]; then
#	echo "ERROR: '$OOC_DIST' is not the path where ooc has been installed: the file dist/ooc.jar can't be found.
#Please set the OOC_DIST environment variable to where you have installed ooc. (example: export OOC_DIST=/opt/ooc)"
#	exit 1
#fi
#java -jar $OOC_DIST/dist/ooc.jar $*
java -jar /blue/Dev/ooc/dist/ooc.jar
