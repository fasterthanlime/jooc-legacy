package org.ooc.frontend;

/**
 * Contain the online (rather inline) help of the ooc compiler
 * 
 * @author Amos Wenger
 */
public class Help {

	/**
	 * Print a helpful help message that helps.
	 */
	public static void printHelp() {

		System.out.println("Usage: ooc [options] files\n");
		System.out.println(
"-v, -verbose                    verbose\n" +
"-g, -debug                      compile with debug information\n" +
"-noclean                        don't delete .c/.h files produced by\n" +
"                                the backend\n" +
"-gcc,-tcc,-icc,-clang,-onlygen  choose the compiler backend (default=gcc)" +
"								 onlygen doesn't launch any C compiler, and implies -noclean\n" +
"-gc=[dynamic,static,off]        link dynamically, link statically, or doesn't\n" +
"                                link with the Boehm GC at all.\n" +
"-driver=[combine,sequence]      choose the driver to use. combine does all in one,\n" +
"                                sequence does all the .c one after the other.\n" +
"-sourcepath=output/path/        location of your source files\n" +
"-outpath                        where to output the .c/.h files\n" +
"-Ipath, -incpath=path           where to find C headers\n" +
"-Lpath, -libpath=path           where to find libraries to link with\n" +
"-lmylib                         link with library 'mylib'\n" +
"-timing                         print how much time it took to compile\n" +
"-r, -run                        runs the executable after compilation\n" +
"\nFor help about the backend options, run 'ooc -help-backends'"
		);
		
	}

	/**
	 * Print a helpful help message that helps about backends.
	 */
	public static void printHelpBackends() {
		System.out.println(
"The available backends are: [none,gcc,make] and the default is gcc.\n" +
"none             just outputs the .c/.h files (be sure to have a main func)\n" +
"gcc              call the GNU C compiler with appropriate options\n" +
"make             generate a Makefile in the default output directory (ooc_tmp)\n" +
"\nFor help about a specific backend, run 'ooc -help-gcc' for example"
		);
	}
	
	/**
	 * Print a helpful help message that helps about gcc.
	 */
	public static void printHelpGcc() {
		System.out.println(
"gcc backend options:\n" +
"-clean=[yes,no]        delete (or not) temporary files. default: yes.\n" +
"                       overriden by the global option -noclean\n" +
"-verbose=[yes,no]      print the gcc command lines called from the backend.\n" +
"                       overriden by the global options -v, -verbose\n" +
"-shout=[yes,no], -s    prints a big fat [ OK ] at the end of the compilation\n" +
"                       if it was successful (in green, on Linux platforms)\n" +
"any other option       passed to gcc\n"
		);
	}
	
	/**
	 * Print a helpful help message that helps about make.
	 */
	public static void printHelpMake() {
		System.out.println(
"make backend options:\n" +
"-cc=[gcc,icl]        write a Makefile to be compatible with the said compiler\n" +
"-link=libname.a      link with the static library libname.a\n" +
"any other option     passed to the compiler\n"
		);
	}
	
	/**
	 * Print a helpful help message that helps about none.
	 */
	public static void printHelpNone() {
		System.out.println(
"Be sure to have a main function! No .c/.h file will be outputted otherwise.\n\033[0;32;"
+String.valueOf((int) (Math.random() * 6) + 31)+"m\n" +
"                 |                                  |\n" +
"  _ \\  _` |  __| __|  _ \\  __|    _ \\  _` |  _` |   |\n" +
"  __/ (   |\\__ \\ |    __/ |       __/ (   | (   |  _|\n" +
"\\___|\\__,_|____/\\__|\\___|_|     \\___|\\__, |\\__, |  _)\n" +
"                                     |___/ |___/     \n\033[m" +
"\nOh, one last thing: the cake is a lie. Too bad, eh.\n"
		);
	}
	
}
