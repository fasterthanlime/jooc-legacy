.PHONY: all clean

all: prepare dynamic

static:
	ant
	cd utils/ && gcj -static-libgcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find build/ -name "*.class"` --main=org.ooc.compiler.CommandLineInterface -o ../bin/ooc

dynamic:
	ant
	cd utils/ && gcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find build/ -name "*.class"` --main=org.ooc.compiler.CommandLineInterface -o ../bin/ooc

strip:
	test "${WINDIR}" == "" && strip bin/ooc || strip bin/ooc.exe

prepare:
	test -d bin || mkdir -p bin

clean:
	ant clean
	rm -rf bin

nogcj: prepare
	ant -f build-nogcj.xml
