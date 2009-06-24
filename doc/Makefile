.PHONY: clean langref

DOCRES=/usr/share/sgml/stylesheets/dsssl/docbook/html

all: langref
	@echo "All done!"

langref: langref.xml
	cd langref && jade -t xml -d ${DOCRES}/docbook.dsl ../res/xml.dcl ../langref.xml

clean:
	rm -f langref/*
