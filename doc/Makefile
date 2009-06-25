.PHONY: clean langref

DOCRES=/usr/share/sgml/stylesheets/dsssl/docbook/html

all: langref
	@echo "All done!"

langref: langref.xml clean
	cd langref && jade -t xml -d ${DOCRES}/docbook.dsl ../res/xml.dcl ../langref.xml
	rm -f langref.zip
	zip langref.zip -r langref/

clean:
	rm -f langref/*
