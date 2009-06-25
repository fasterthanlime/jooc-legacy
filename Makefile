.PHONY: all clean

all:
	ant
	cd utils/ && gcj -static-libgcj -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find build/ -name "*.class"` --main=org.ooc.compiler.CommandLineInterface -o ../bin/ooc
	test -z WINDIR && strip bin/ooc.exe || strip bin/ooc

clean:
	ant clean
