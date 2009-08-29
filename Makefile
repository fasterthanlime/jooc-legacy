.PHONY: all clean jar
MAIN_CLASS="org.ooc.frontend.CommandLine"

all: prepare dynamic

jar:
	ant

static: jar
	cd utils/ && gcj -static-libgcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find build/ -name "*.class"` --main=${MAIN_CLASS} -o ../bin/ooc

dynamic: jar
	cd utils/ && gcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find build/ -name "*.class"` --main=${MAIN_CLASS} -o ../bin/ooc

strip:
	test "${WINDIR}" == "" && strip bin/ooc || strip bin/ooc.exe

prepare:
	test -d bin || mkdir -p bin

clean:
	ant clean
	rm -rf bin

nogcj: prepare
	ant -f build-nogcj.xml
