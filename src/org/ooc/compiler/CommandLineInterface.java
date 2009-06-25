package org.ooc.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import org.ooc.ShellUtils;
import org.ooc.backends.BackendFactory;
import org.ooc.backends.ProjectInfo;
import org.ooc.errors.SourceContext;
import org.ooc.gui.SyntaxTreeWindow;

/**
 *
 * @author Amos Wenger
 */
public class CommandLineInterface {
	
	private static String OOC_DIST = System.getenv("OOC_DIST");
	
    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	
    	findDist();
    	
        if(args.length < 1) {
            System.out.println("ooc: no files.");
            System.exit(1);
        }
        
        List<String> dynamicLibs = new ArrayList<String> ();
        List<String> unitList = new ArrayList<String> ();
        boolean daemon = false;
        boolean run = false;
        boolean timing = false;
        int daemonPort = -1;
        
        boolean gui = false;
        
        BuildProperties props = new BuildProperties();
        
        for(String arg: args) {
        	if(arg.startsWith("-")) {
        		String option = arg.substring(1);
        		if(option.startsWith("locale")) {
        			
        			Locale.setDefault(new Locale(arg.substring(arg.indexOf('=') + 1)));
        		
        		} else if(option.startsWith("gui")) {
        			
        			gui = true;
        			
        		} else if(option.startsWith("backend=")) {
        			
        			props.backend = BackendFactory.getBackend(option.substring("backend=".length()).trim());
        			
        		} else if(option.startsWith("daemon")) {
        			
        			daemon = true;
        			int index = arg.indexOf(':');
        			if(index != -1) {
        				daemonPort = Integer.parseInt(arg.substring(index + 1));
        			}
        			
        		} else if(option.startsWith("sourcepath")) {
        			
        			String sourcePathOption = arg.substring(arg.indexOf('=') + 1);
        			StringTokenizer tokenizer = new StringTokenizer(sourcePathOption, File.pathSeparator);
        			while(tokenizer.hasMoreTokens()) {
        				props.sourcePath.add(tokenizer.nextToken());
        			}
        			
        		} else if(option.startsWith("outpath")) {
        			
        			props.outPath = arg.substring(arg.indexOf('=') + 1);
        			
        		} else if(option.startsWith("incpath")) {
        			
        			props.incPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("I")) {
        			
        			props.incPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("libpath")) {
        			
        			props.libPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("L")) {
        			
        			props.libPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("l")) {
        			
        			dynamicLibs.add(arg.substring(2));
        			
        		} else if(option.equals("noclean")) {
        			
        			props.clean = false;
        			
        		} else if(option.equals("timing") || option.equals("t")) {
        			
        			timing = true;
        			
        		} else if(option.equals("debug") || option.equals("g")) {
        			
        			props.debug = true;
        			
        		} else if(option.equals("verbose") || option.equals("v")) {
        			
        			props.verbose = true;
        			
        		} else if(option.equals("run") || option.equals("r")) {
        			
        			run = true;
        			
        		} else if(option.equals("V") || option.equals("-version") || option.equals("version")) {
        			
        			Version.printVersion();
        			System.exit(0);
        			
        		} else {
        			
        			System.err.println("Unrecognized option: '"+arg+"'");
        			System.exit(1);
        			
        		}
        	} else {
        		unitList.add(arg);
        	}
        }
        
        if(props.verbose) {
        	// Debug info
        	System.out.println("classpath = "+System.getProperty("java.class.path"));
        }
        
        if(daemon) {
        	
        	if(!unitList.isEmpty()) {
        		System.err.println("Shouldn't specify a list of files to compile when starting in daemon mode ('-daemon' option)");
        		System.exit(1);
        	}
        	if(daemonPort == -1) {
        		System.err.println("Should specify a port when starting in daemon mode ('-daemon' option)");
        		System.exit(1);
        	}
        	new CompilerDaemon(daemonPort);
        	
        } else {
        	
        	ProjectInfo projInfo = new ProjectInfo(props);
			projInfo.dynamicLibraries.addAll(dynamicLibs);
        	Compiler compiler = new Compiler();
        	int returnCode = 0;
        	
        	props.sourcePath.add("./");
        	
            if(gui) {
            	
            	SyntaxTreeWindow window = new SyntaxTreeWindow(props.sourcePath, unitList, projInfo);
            	System.out.println("Launching the gui....");
            	window.show();
            	
            } else {
            	
            	long t1 = System.currentTimeMillis();
            	
	        	for(String unitName: unitList) {
	        		
	            	returnCode = compiler.compile(projInfo, unitName); 
	                if(returnCode != 0) {
	                	break;
	                }
	            }
	        	
	        	long t2 = System.currentTimeMillis();
	        	
	        	if(timing) {
	        		System.out.println("Finished compilation in "+(t2-t1)+"ms.");
	        	}

	        	if(run && returnCode == 0) {
	        		ProcessBuilder pb = new ProcessBuilder();
	            	for(SourceContext source: projInfo.executables.values()) {
	            		String execPath = "./" + source.source.getInfo().simpleName;
	            		
	            		if(props.verbose) {
	            			System.out.println("Launching '"+execPath+"'...");
	            		}
	            		
						Process p = pb.command(execPath).start();
						ProcessUtils.redirectIO(p);
						int status = p.waitFor();
						
						if(props.verbose) {
							System.out.println("Exited with status "+status);
						}
						
	            	}
	            }
            	
            }
        	
        	System.exit(returnCode);
        	
        }
        
        System.exit(0);

    }

	private static void findDist() throws IOException {
		
		/** 
		 * First try: assume we're launched with java -jar bin/ooc.jar and
		 * try to find ourselves in the classpath.
		 */
		if(OOC_DIST == null || OOC_DIST.trim().isEmpty()) {
	    	StringTokenizer st = new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator);
	    	while(st.hasMoreElements()) {
	    		String path = st.nextToken();
	    		if(path.contains("ooc.jar")) {
	    			System.out.println("Got path: "+path);
	    			OOC_DIST = new File(path).getCanonicalPath();
	    			System.out.println("Step 1: "+OOC_DIST);
	    			OOC_DIST = new File(OOC_DIST).getParentFile().getParent();
	    			System.out.println("Step 2: "+OOC_DIST);
	    			break;
	    		}
	    	}
    	}
    	
		Properties env = ReadEnv.getEnvVars();
		
		/**
		 * Second try: assume we're launched as GCJ-compiled executable, and
		 * try to get our path from the "_" environment variable, which seems to
		 * be set by bash under Gentoo and by MSYS 1.0.11/whatever under MinGW/WinXP
		 */
    	if(OOC_DIST == null || OOC_DIST.trim().isEmpty()) {
    		Object curr = env.get("_");
    		if(curr != null) {
    			File file = new File(curr.toString().trim());
    			if(file.getPath().endsWith("ooc") || file.getPath().endsWith("ooc.exe")) {
    				String canonicalPath = file.getCanonicalPath();
    				OOC_DIST = new File(canonicalPath).getParentFile().getParent();
    			}
    		}
    	}

		/**
		 * Third try: assume we're launched as GCJ-compiled executable, and
		 * try to get the PATH environment variable, and look us in here.
		 */
    	if(OOC_DIST == null || OOC_DIST.trim().isEmpty()) {
    		
    		File bin = ShellUtils.findExecutable("ooc");
    		if(bin == null) {
    			bin = ShellUtils.findExecutable("ooc.exe");
    		}
    		
    		if(bin != null) {
    			String canonicalPath = bin.getCanonicalPath();
				OOC_DIST = new File(canonicalPath).getParentFile().getParent();
    		}
    		
    	}
    	
    	if(OOC_DIST == null || OOC_DIST.trim().isEmpty()) {
    		// If still null, wasn't found.
    		System.out.println("ERROR: You need to set the OOC_DIST environment " +
    				"variable to where you have installed ooc. (example: export OOC_DIST=/opt/ooc)");
    		System.exit(1);
    	}
	}

    /**
     * @return the emplacement of the ooc distribution directory, as specified
     * either by the OOC_DIST environment variable, or guessed via the classpath
     */
	public static String getOocDist() {

		return OOC_DIST;
		
	}

}
