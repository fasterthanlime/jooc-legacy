package org.ooc.backends;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.ooc.backends.compilers.AbstractCompiler;
import org.ooc.backends.compilers.Gcc4;
import org.ooc.backends.compilers.Icc;
import org.ooc.compiler.BuildProperties;
import org.ooc.compiler.libraries.Target;
import org.ooc.compiler.pkgconfig.PkgInfo;
import org.ooc.errors.SourceContext;
import org.ooc.outputting.CachedFileOutputter;
import org.ooc.outputting.FileOutputter;
import org.ooc.outputting.FileUtils;

/**
 * Copies all generated C source and headers into a directory, and generates
 * a Makefile, for later build. The perfect example of an "indirect" backend.
 * 
 * Backend options:
 * <ul>
 * 	<li>-link=libdummy.a : Adds libdummy.a to executable builds.
 * Multiple '-link'</li>
 * </ul>
 * 
 * @author Amos Wenger
 */
public class MakeBackend extends Backend {

	private String userCflags;
	private List<String> userStaticLibs;
	private FileOutputter outputter;
	
	private AbstractCompiler cc;

	protected MakeBackend(String params) {
		
		super(params);
		
		userCflags = "";
		userStaticLibs = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(params, ",");
		
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if(token.startsWith("-link=")) {
				userStaticLibs.add(token.substring("-link=".length()));
			} else if(token.startsWith("-cc=")) {
				String compiler = token.substring("-cc=".length());
				if(compiler.startsWith("gcc")) {
					cc = new Gcc4();
				} else if(compiler.equals("icl")) {
					cc = new Gcc4(); // FIXME implement Icl
				} else if(compiler.equals("icc")) {
					cc = new Icc();
				}
			} else {
				userCflags += token + " ";
			}
		}
		
		if(cc == null) {
			cc = new Gcc4();
		}
		
		outputter = new CachedFileOutputter();
	}

	@Override
	public int compile(ProjectInfo info, BuildProperties props) throws IOException {
		
		/* Writes all .c and .h from .ooc */
		for(SourceContext executable: info.executables.values()) {
			outputter.output(props, info, executable);
		}
		
		List<String> staticLibsRealPath = new ArrayList<String>();
		
		/* Copy static libraries to output directory */
		for(String staticLib: userStaticLibs) {
			staticLibsRealPath.add(copyStaticLib(props, staticLib));
		}
		for(String staticLib: info.staticLibraries) {
			staticLibsRealPath.add(copyStaticLib(props, staticLib));
		}
		
		/* Collect all sources */
		List<SourceContext> sources = new ArrayList<SourceContext>();
		sources.addAll(info.modules.values());
		sources.addAll(info.executables.values());

		/* Collect C++ modules to link in */
		List<String> wittyPaths = getCppModulesPaths(info, props);
		
		try {
		
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(props.getPrefixedOutPath()
							+ File.separator + "Makefile")));
			writer.append("# Generated on "+new Date().toString()+" by ooc's make backend. (by Amos Wenger, 2009)\n\n");
			
			/* ?= means define if undefined. It allows the user to override our default settings */
			writer.append("CC=gcc\n");
			
			/* Dynamic libraries */
			writer.append("DYNAMIC_LIBS+=");
			for(String library: info.dynamicLibraries) {
				writer.append("-l");
				writer.append(library.trim());
				writer.append(' ');
			}
			if(!props.pkgInfos.isEmpty()) {
				writer.append("`pkg-config --libs ");
				for(PkgInfo pkginfo: props.pkgInfos) {
					writer.append(pkginfo.name+" ");
				}
				writer.append("` ");
			}
			writer.append('\n');
			
			/* Static libraries */
			writer.append("STATIC_LIBS+=");
			for(String library: staticLibsRealPath) {
				writer.append(library.trim());
				writer.append(' ');
			}
			writer.append(' ');
			writer.append('\n');
			
			/* C flags */
			writer.append("CFLAGS+=");
			writer.append(cc.getC99());
			writer.append(" ");
			writer.append(userCflags);
			writer.append(' ');
			for(String path: props.libPath) {
				writer.append(cc.getLibraryPath(path));
				writer.append(" ");
			}
			for(String path: props.incPath) {
				writer.append(cc.getIncludePath(path));
				writer.append(path);
				writer.append(" ");
			}
			if(!props.pkgInfos.isEmpty()) {
				writer.append("$(pkg-config --cflags ");
				for(PkgInfo pkginfo: props.pkgInfos) {
					writer.append(pkginfo.name+" ");
				}
				writer.append(") ");
			}
			writer.append("\n\n");
			
			writer.append(".PHONY: all clean");
			writer.append("\n\n");
			
			/* all (default target) */
			writer.append("all: ");
			for(String module: info.modules.keySet()) {
				writer.append(info.getRelativePath(module, ".o"));
				writer.append(' ');
			}
			for(String wittyPath: wittyPaths) {
				writer.append(wittyPath + ".o");
				writer.append(' ');
			}
			writer.append("\n");
			
			for(String executable: info.executables.keySet()) {
				writer.append("\t${CC} ");
				writer.append(info.getRelativePath(executable, ".c"));
				writer.append(' ');
				
				/* add C++ modules */
				for(String wittyPath: wittyPaths) {
					writer.append(wittyPath + ".cpp");
					writer.append(' ');
				}
					
				writer.append(" ${CFLAGS} ${DYNAMIC_LIBS} ");
				
				for(String module: info.modules.keySet()) {
					writer.append(info.getRelativePath(module, ".o"));
					writer.append(' ');
				}
				
				writer.append(" -o ");
				writer.append(info.executables.get(executable).source.getInfo().simpleName);
				
				writer.append(" ${STATIC_LIBS} "); // for some reason, gcc wants them at the end.
				
				writer.append("\n\n");
			}
			
			/* clean */
			writer.append("clean:\n\trm -rf ");
			for(String module: info.modules.keySet()) {
				writer.append(info.getRelativePath(module, ".o"));
				writer.append(' ');
			}
			writer.append("\n\n");
			
			/* ooc modules */
			for(String module: info.modules.keySet()) {
				writer.append(info.getRelativePath(module, ".o"));
				writer.append(": ");
				writer.append(info.getRelativePath(module, ".c"));
				writer.append(" ");
				writer.append(info.getRelativePath(module, ".h"));
				writer.append("\n\t${CC} ${CFLAGS} -c ");
				writer.append(info.getRelativePath(module, ".c"));
				writer.append(" -o ");
				writer.append(info.getRelativePath(module, ".o"));
				writer.append("\n\n");
			}
			
			writer.close();
			
		} catch(IOException e) {
			
			e.printStackTrace();
			
		}
		
		return 0;
		
	}

	private String copyStaticLib(BuildProperties props, String staticLib)
			throws IOException {

		File libFile = new File(staticLib);
		
		File src = new File(staticLib);
		File dst = new File(new File(props.getPrefixedOutPath()), "libs" + File.separator
				+ Target.guessHost().toString() + File.separator + libFile.getName());
		dst = FileUtils.resolveRedundancies(dst);
		
		File toTrim = new File(props.getPrefixedOutPath());
		toTrim = FileUtils.resolveRedundancies(toTrim);
		
		if(!dst.getParentFile().mkdirs()) {
			System.err.println("Couldn't create directory '"+dst.getParent()+"', necessary for exporting static libs.");
		}
		
		//System.out.println("Copying static lib.. from '"+src.getCanonicalPath()+"' to '"+dst.getCanonicalPath()+"'");
		//System.out.println("(relative paths from '"+src.getPath()+"' to '"+dst.getPath()+"')");
		//System.out.println("(outpath = '"+props.outPath+"', prefix = '"+props.prefix+"'");
		//System.out.println("(to trim = '"+toTrim.getPath()+"'");
		FileUtils.copy(src, dst);
		
		String relPath = dst.getPath().substring(toTrim.getPath().length());
		if(relPath.startsWith(File.separator)) {
			relPath = relPath.substring(1);
		}
		//System.out.println("(relPath = '"+relPath+"'");
		
		return relPath;
		
	}

	private List<String> getCppModulesPaths(ProjectInfo info, BuildProperties props)
			throws IOException {
		
		List<String> wittyPaths = new ArrayList<String>();
		String fuzzyPath = new File(props.getPrefixedOutPath()).getCanonicalPath();
		//System.out.println("Fuzzy path: "+fuzzyPath);
		//System.out.println("CPP modules: "+info.cppModules);

		for(File cppModule: info.cppModules) {
			
			String tweenyPath = cppModule.getCanonicalPath();
			//System.out.println("Tweeny path: "+tweenyPath);
			String wittyPath = tweenyPath.substring(fuzzyPath.length());
			if(wittyPath.startsWith(File.separator)) {
				wittyPath = wittyPath.substring(1);
			}
			if(wittyPath.endsWith(".cpp")) {
				wittyPath = wittyPath.substring(0, wittyPath.length() - ".cpp".length());
			}
			//System.out.println("Witty path: "+wittyPath);
			wittyPaths.add(wittyPath);
			
		}
		return wittyPaths;
		
	}

}
