package org.ooc.compiler;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

import org.ooc.backends.BackendFactory;
import org.ooc.backends.ProjectInfo;

/**
 * The compiler daemon sits quietly in the background, and wait for orders.
 * It's darn useful for e.g. remote compilation, plus it caches files, too,
 * so it 
 * 
 * @author Amos Wenger
 */
public class CompilerDaemon {

	private static ServerSocket serverSocket;
	private static boolean isRunning;
	private static Date startDate = new Date();
	
	protected CompilerDaemon(int port) throws Exception {
		
		serverSocket = new ServerSocket(port);
    	System.out.println("Started ooc compiler daemon at port "+port+" "+startDate.toString());
    	
    	isRunning = true;

    	while(isRunning) {
    		try {
				handleSocket(serverSocket.accept());
			} catch (SocketException e) {
				System.out.println(e.getMessage());
			}
    	}
    	
    	Date stopDate = new Date();
    	float runTime = (stopDate.getTime() - startDate.getTime()) / 1000f;
    	System.out.println("Stopped ooc compiler daemon at port "+port+" "+stopDate.toString()+". Ran for "+runTime+" seconds.");
		
	}
	
	void halt() {
		
		isRunning = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void handleSocket(final Socket socket) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Compiler compiler = new Compiler();
				
				BuildProperties props = new BuildProperties();
				// No default outpath, since the working directory of the daemon is irrelevant.
				// In fact, the daemon should probably refuse to compile if no outPath is specified
				props.outPath = null;
				
				boolean timing = false; //Should we output the time the compilation took?
				
				try {
			
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					
					System.out.println("Got connection to ooc compiler daemon from "+socket.getRemoteSocketAddress());
					while(!socket.isClosed()) {
						String command = reader.readLine().trim();
						if(command.equals("halt")) {
							
							halt();
							break;
							
						} else if(command.equals("close")) {
							
							socket.close();
							break;
							
						} else if(command.startsWith("compile")) {
							
							String path = command.substring("compile".length() + 1).trim();
							System.out.println("Got remote command to compile '"+path+"'. SourcePath = "+props.sourcePath);
							int returnCode;
							if(props.sourcePath.isEmpty()) {
								System.err.println("Can't compile: Empty sourcePath ! Doesn't make sense with a compiler daemon. sourcepath-add, sourcepath-remove, or sourcepath-clear");
								returnCode = 0;
							} else if(props.outPath == null) {
								System.err.println("Can't compile: No outpath specified ! Doesn't make sense with a compiler daemon. Use outpath-set <path>.");
								returnCode = 0;
							} else {
								long t1 = System.currentTimeMillis();
								returnCode = compiler.compile(new ProjectInfo(props), path);
								long t2 = System.currentTimeMillis();
								if(timing) {
									System.out.println("Finished compilation in "+(t2-t1)+"ms.");
								}
							}
							writer.write(String.valueOf(returnCode));
							writer.newLine();
							writer.flush();
							
						} else if(command.startsWith("sourcepath-set ")) {
							
							props.sourcePath.clear();
							props.sourcePath.add(command.substring("sourcepath-set ".length()).trim());
							
						} else if(command.startsWith("sourcepath-add ")) {
							
							props.sourcePath.add(command.substring("sourcepath-add ".length()).trim());
							
						} else if(command.startsWith("sourcepath-remove ")) {
							
							props.sourcePath.remove(command.substring("sourcepath-remove ".length()).trim());
							
						} else if(command.equals("sourcepath-clear")) {
							
							props.sourcePath.clear();
							
						} else if(command.equals("clear-cache")) {
							
							compiler.clearCache();
							
						} else if(command.startsWith("outpath-set")) { 
							
							props.outPath = command.substring("outpath-set ".length()).trim();
							
						} else if(command.startsWith("incpath-add")) { 
							
							props.incPath.add(command.substring("incpath-add ".length()).trim());
							
						} else if(command.startsWith("incpath-clear")) { 
							
							props.incPath.clear();
							
						} else if(command.startsWith("libpath-add")) { 
							
							props.libPath.add(command.substring("libpath-add ".length()).trim());
							
						} else if(command.startsWith("libpath-clear")) { 
							
							props.libPath.clear();
							
						} else if(command.startsWith("backend-set ")) {
							
							props.backend = BackendFactory.getBackend(command.substring("backend-set ".length()).trim());
							
						} else if(command.equals("verbose-off")) { 
							
							props.verbose = false;
							
						} else if(command.equals("verbose-on")) { 
							
							props.verbose = true;
							
						} else if(command.equals("timing-off")) { 
							
							timing = false;
							
						} else if(command.equals("timing-on")) { 
							
							timing = true;
							
						} else {
							
							System.out.println("Unknown command '"+command+"', ignoring.");
							
						}
					}
					System.out.println("Connection closed from "+socket.getRemoteSocketAddress());
					
				} catch(Exception e) {
					
					e.printStackTrace();
					System.exit(1);
					
				}
				
			}
			
		}, "daemon").start();
		
	}
	
}
