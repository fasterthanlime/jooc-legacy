#!/bin/bash

if [[ $1 == "-gui" ]]; then
	opts="-gui"
	shift;
fi

if [[ $OSTYPE = "msys" ]]; then
	incpath="/mingw/include/"
fi

basepath=`dirname $0`
sourcepath="`pwd`"
cd $basepath

if [[ $OOC_DIR = "" ]]; then
	OOC_DIR="../dist/ooc.jar"
fi

java -client -jar $OOC_DIR -backend="gcc:-clean=yes" -libpath="../libs" -incpath="$incpath" -sourcepath="$sourcepath:../lib" -outpath="$sourcepath" $opts $* && cd "$sourcepath" && strip $*
