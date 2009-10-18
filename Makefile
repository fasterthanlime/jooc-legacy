.PHONY: all clean jar
DESTDIR=/usr/local
prefix=$(DESTDIR)
bindir=$(prefix)/bin
sharedir=$(prefix)/share

BINS=bin/ooc
LIBS=sdk
HEADERS=libs/*

MAIN_CLASS="org.ooc.frontend.CommandLine"

test:
	echo $(DESTDIR)

jar:
	ant

gcj-static: prepare jar
	cd utils/ && gcj -static-libgcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find ../build/javac-classes -name "*.class"` --main=${MAIN_CLASS} -o ../bin/ooc

gcj-dynamic: prepare jar
	cd utils/ && gcj -g -O3 -Dooc.version="`cat version.txt`, built on `date +%F\ %R:%S`" `find ../build/javac-classes -name "*.class"` --main=${MAIN_CLASS} -o ../bin/ooc

strip:
	test "${WINDIR}" == "" && strip bin/ooc || strip bin/ooc.exe

prepare:
	test -d bin || mkdir -p bin

clean:
	ant clean
	rm -rf bin

nogcj: prepare
	ant -f build-nogcj.xml

install-bin:
	mkdir -p $(prefix)
	mkdir -p $(bindir)
	mkdir -p $(sharedir)/ooc/sdk
	mkdir -p $(sharedir)/ooc/libs
	cp utils/ooc-bin $(bindir)/ooc
	sed -i 's,sharedir,$(sharedir),' $(bindir)/ooc
	for i in $(BINS); do cp $$i $(sharedir)/ooc/; done
	for i in $(HEADERS);  do cp -r $$i $(sharedir)/ooc/libs; done
	for i in $(LIBS);  do cp -r $$i $(sharedir)/ooc/; done
	
uninstall-bin:
	rm -f $(bindir)/ooc
	rm -rf $(sharedir)/ooc
