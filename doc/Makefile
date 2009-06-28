.PHONY: clean langref

DOCRES=/usr/share/sgml/stylesheets/dsssl/docbook/html

all: langref compman
	@echo "All done!"

langref: langref.xml clean
	cd langref && jade -t xml -d ${DOCRES}/docbook.dsl ../res/xml.dcl ../langref.xml
	rm -f langref.zip
	zip langref.zip -r langref/

compman: compman.xml clean
	cd compman && jade -t xml -d ${DOCRES}/docbook.xsl ../res/xml.dcl ../compman.xml
	rm -rf compman.zip
	zip compman.zip -r compman/

clean:
	rm -f langref/*
	rm -f compman/*
