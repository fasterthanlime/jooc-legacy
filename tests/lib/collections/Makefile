all:
	ooc collections.ooc -v -c -m32 -nomain -gc=off -driver=sequence -outpath=objects/ -outlib=libcollections.a && make clean

clean:
	rm -rf objects/

mrproper: clean
	rm -rf *.a
