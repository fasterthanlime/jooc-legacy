all:
	ooc mylib.ooc ${OOC_FLAGS} -entrypoint=mylib_init -v -gc=off -noclean -c -driver=sequence -outlib=mylib.a -outpath=objs/
	gcc myapp.c mylib.a -Iobjs/ -o myapp

clean:
	rm -rf objs/ *.a
