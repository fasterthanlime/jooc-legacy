#!/bin/bash

if [[ $1 != "" ]]; then
if [[ ("$1" == "--help") || ("$1" == "-h") ]]; then
	echo "Usage: ./$0 [OPTIONS]

OPTIONS
	--clean: removes all .o/.c/.h which have the same base name as .ooc files
	(e.g. blah.ooc will yield the removal of blah.o, blah.c, blah.h, if they
	exist), and do not compile anything
	
	--help: this message
	"
	exit 0
fi
fi

rm -f "tests-log.txt"

basePath=`pwd`/`dirname $0`
echo "basePath = $basePath"
if [[ $OOC_DIST == "" ]]; then
	echo "OOC_DIST environment variable not set. Set it to the location of
	your ooc distribution (e.g. the directory with libs/, sdk/, dist/ooc.jar, etc."
	exit 1
fi

libpath="$OOC_DIST/sdk"

total=0
compiled=0
passed=0
notCompiledList=""
failedList=""
passedList=""

if [[ $1 == "--clean" ]]; then
	mode="clean"
else
	mode="compile"
fi

dirList="."
for i in `ls -R`; do
        if [[ -d $i ]]; then
                end=${i:${#i}-6}
                if [[ $end == "-tests" ]]; then
			dirList="$dirList $i"
                fi
        fi
done

if [[ $mode == "compile" ]]; then

tries=0
# launch the daemon
java -jar $OOC_DIST/dist/ooc.jar -daemon:14269 &> "build-log.txt" &
sleep 0.3

# connect to the compiler daemon
while ! exec 3<> /dev/tcp/127.0.0.1/14269; do
	tries=$(( tries + 1 ))
	if [[ "$tries" -eq 42 ]]; then
		echo "All connection tries failed, abandoning..."
		exit 1
	fi
	echo "Connecting... try $tries"
	sleep 0.1
done

echo "timing-on" 1>&3
echo "backend-set gcc" 1>&3
echo "verbose-on" 1>&3
echo "libpath-add $OOC_DIST/libs" 1>&3

fi

for dir in $dirList; do
if [[ $mode == "clean" ]]; then
	echo "Cleaning up tests in $dir"
fi
cd $basePath/$dir
rm -f "test-log.txt"

if [[ $mode != "clean" ]]; then
	#echo "clear-cache" 1>&3
	echo "sourcepath-clear" 1>&3
	echo "sourcepath-add `pwd`" 1>&3
	echo "sourcepath-add $libpath" 1>&3
fi

for i in *.ooc; do
	if [[ $i == "*.ooc" ]]; then
		continue
	fi
	test=${i:0:(${#i}-4)}
	if [[ $mode == "clean" ]]; then
		rm -f $basePath/$dir/$test.o
		rm -f $basePath/$dir/$test.c
		rm -f $basePath/$dir/$test.h
		rm -f $basePath/$dir/$test
		continue
	fi
	echo "outpath-set `pwd`" 1>&3
	total=$(( total + 1 ))
	echo "compile $test" 1>&3
	#echo "Compiling $dir/$test"
	tabbedTest="$dir/$test......................................................................................................................."
	tabbedTest="${tabbedTest:0:50}"
	printf "\033[1;33mbuilding \033[m$tabbedTest\033[m"
	end=${test:${#test}-11}
	read 0<&3;
	if [[ "$REPLY" -eq 0 ]]; then
		compiled=$(( compiled + 1 ))
		printf "compile \033[1;32m[ OK ]\033[m..";
		strip $test
		./$test --test >> "$basePath/tests-log.txt"
		code=$?
		if [[ $end == "-shouldfail" ]]; then
			if [[ "$code" -eq 0 ]]; then
				printf "test \033[1;31m[FAIL]\033[m\n";
				failedList="$failedList
$dir/$test"
			else
				printf "test \033[1;32m[ OK ]\033[m\n";
				passed=$(( passed + 1 ))
				passedList="$passedList
$dir/$test"
			
			fi
		else
			if [[ "$code" -eq 0 ]]; then
				printf "test \033[1;32m[ OK ]\033[m\n";
				passed=$(( passed + 1 ))
				passedList="$passedList
$dir/$test"
			else
				printf "test \033[1;31m[FAIL]\033[m\n";
				failedList="$failedList
$dir/$test"
			fi
		fi
	else
		if [[ $end == "-shouldfail" ]]; then
			printf "compile \033[1;32m[ OK ]\033[m..test \033[1;32m[ OK ]\033[m\n";
			compiled=$(( compiled + 1 ))
			passed=$(( passed + 1 ))
			passedList="$passedList
$dir/$test" 
		else
			printf "compile \033[1;31m[FAIL]\033[m\n";
			notCompiledList="$notCompiledList
	$dir/$test"
		fi
	fi
done
done

if [[ $mode == "compiler" ]]; then

echo "halt" 1>&3 # shutdown the compiler daemon
exec 3<&- # disconnect

fi

if [[ $mode == "clean" ]]; then

printf "\033[1;34m======================================================================\033[m\n"
echo "all tests cleaned =)"
printf "\033[1;34m======================================================================\033[m\n"

else

echo
printf "\033[1;34m======================================================================\033[m\n"
echo "all tests finished building"
if [[ $compiled -lt $total ]]; then
	printf "\033[1;31m"
else
	printf "\033[1;32m"
fi
printf "$compiled / $total tests compiled\033[m\n"
if [[ $passed -lt $compiled ]]; then
	printf "\033[1;31m"
else
	printf "\033[1;32m"
fi
printf "$passed / $compiled compiled tests passed\033[m\n"
printf "\033[1;34m----------------------------------------------------------------------\033[m\n"
if [[ $notCompiledList != "" ]]; then
	printf "\033[4mnot compiled:\033[m $notCompiledList\n"
	printf "\033[1;34m----------------------------------------------------------------------\033[m\n"
fi
if [[ $failedList != "" ]]; then
	printf "\033[4mfailed tests:\033[m $failedList\n"
	printf "\033[1;34m----------------------------------------------------------------------\033[m\n"
fi
if [[ $notCompiledList != "" ]]; then
	printf "\033[4mpassed tests:\033[m $passedList\n"
	printf "\033[1;34m----------------------------------------------------------------------\033[m\n"
fi
echo "hope you had a good time =)"
printf "\033[1;34m======================================================================\033[m\n"

fi
