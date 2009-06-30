.PHONY: all clean

all:
	ant
	cd utils/ && gcj -static-libgcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find build/ -name "*.class"` --main=org.ooc.compiler.CommandLineInterface -o ../bin/ooc
	#test "${WINDIR}" == "" && strip bin/ooc || strip bin/ooc.exe

nostatic:
	ant
	cd utils/ && gcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find build/ -name "*.class"` --main=org.ooc.compiler.CommandLineInterface -o ../bin/ooc

clean:
	ant clean
