===========================
 Build instructions for ooc
===========================

The source distribution is spread mainly for study, but if you want to
build it yourself, here's what it take:

 - GNU Make
 - Apache Ant
 - a POSIX-ish shell (bash is fine, and so is MSYS for MinGW/Windows)
 - GCC 4.x with java support (gcj front-end) (last builds work very
 well on MinGW/Windows)

If you have everything set up correctly, including paths, etc., just type
$ make

In the main ooc directory, and it should compile fine =) The output is
an executable in the bin/ooc directory, statically linked to gcj.

The provided Makefile is very easy to understand, and works perfectly
under Gentoo and Windows XP+MinGW+MSYS.

If you want to build from .java source (not from .class), you have
to check out 'ubi' as well (ooc depends on it), you can find on GitHub
at http://github.com/amoswenger/ubi

The garbage collector (libgc.a) is the Boehm GC. It's very portable.
It can be downloaded from http://www.hpl.hp.com/personal/Hans_Boehm/gc/

You're welcome if you want to build binaries for 64-bit platforms,
Solaris*, BSD*, etc. I'm reachable at amoswenger@gmail.com for discussion
about this.
