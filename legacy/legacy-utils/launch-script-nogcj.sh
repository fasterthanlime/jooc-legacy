#!/bin/sh

OOC_DIST=/blue/Dev/ooc
UBI_DIST=$OOC_DIST/../ubi
java -classpath $OOC_DIST/build/classes:$UBI_DIST/build/classes org.ooc.compiler.CommandLineInterface $*
