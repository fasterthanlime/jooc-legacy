.PHONY: all clean

all:
	ant
	cd utils/ && gcj -static-libgcj -O3 -Dooc.version="`cat version.txt`, built on `date -R`" `find build/ -name "*.class"` --main=org.ooc.compiler.CommandLineInterface -o ../bin/ooc
	strip bin/ooc*
	#cd utils/ && gcj `find ../../ubi/build/classes -name "*.class"` `find ../build/classes -name "*.class"` --main=org.ooc.compiler.CommandLineInterface -o ../dist/ooc

clean:
	ant clean
