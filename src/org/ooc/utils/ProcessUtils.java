package org.ooc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Utilities about relaying I/O between processes launched from Java
 * and streams, writers, loggers, etc.
 * 
 * @author Amos Wenger
 */
public class ProcessUtils {
	
	/** The size, in bytes or chars, of a buffer used by a relay */
	public static int BUFFER_SIZE = 4096;
	
	/**
	 * A stream relay pipes data from an input stream to an output stream
	 * @author Amos Wenger
	 */
	public static class StreamRelay {
		
		protected final InputStream inStream;
		protected final OutputStream outStream;
		
		/**
		 * Create a new relay between an input and an output stream.
		 * @param inStream
		 * @param outStream
		 */
		public StreamRelay(InputStream inStream, OutputStream outStream) {
			
			this.inStream = inStream;
			this.outStream = outStream;
			
		}

		/**
		 * Update the relay
		 * @return
		 * @throws IOException 
		 */
		public boolean update() throws IOException {
			
				byte[] buffer = new byte[BUFFER_SIZE];
				int numRead;
				if((numRead = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, numRead);
					return true;
				}
				return false;
			
		}
		
	}
	
	/**
	 * A stream relay pipes data from an input stream to an output stream
	 * @author Amos Wenger
	 */
	public static class CharRelay {
		
		protected final Reader reader;
		protected final Writer writer;
		
		/**
		 * Create a new relay between an input stream and a writer.
		 * @param inStream
		 * @param outStream
		 */
		public CharRelay(InputStream inStream, Writer writer) {
			
			this.reader = new InputStreamReader(inStream);
			this.writer = writer;
			
		}

		/**
		 * Start the char relay
		 */
		public void start() {
			
			try {
				
				char[] buffer = new char[BUFFER_SIZE];
				int numRead;
				while((numRead = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, numRead);
				}
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	/**
	 * Bind standard output/error stream to a process's output/error stream
	 * @param process
	 * @throws IOException 
	 */
	public static void redirectIO(final Process process) throws IOException {
		
		StreamRelay relay1 = new StreamRelay(process.getInputStream(), System.out);
		StreamRelay relay2 = new StreamRelay(process.getErrorStream(), System.err);
		
		while(relay1.update() || relay2.update()) {
			// enjoy =)
		}
		
	}
	
	/**
	 * Bind a writer to a process's output stream
	 * @param process
	 * @param writer a Writer to which output the specified process input/error stream.
	 * If null, the standard output/error streams are used
	 */
	public static void redirectIO(final Process process, final Writer writer) {
	
		new CharRelay(process.getInputStream(), writer).start();
		
	}

}
