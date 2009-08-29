package org.ooc.frontend.pkgconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Information about a package managed by pkg-config
 * 
 * @author Amos Wenger
 */
public class PkgInfo {

	/** The name of the package, e.g. gtk+-2.0, or imlib2 */
	public final String name;
	
	/** The output of `pkg-config --libs name` */
	public final String libsString;
	
	/** The output of `pkg-config --cflags name` */
	public final String cflagsString;
	
	/** The C flags (include the include paths) */
	public final List<String> cflags;
	
	/** A list of all libraries needed */
	public final List<String> libraries;
	
	/** A list of all include paths */
	public final List<String> includePaths;
	
	/**
	 * Create a new Package info
	 */
	public PkgInfo(String name, String libsString, String cflagsString) {
		
		this.name = name;
		this.libsString = libsString;
		this.cflagsString = cflagsString;

		cflags = new ArrayList<String>();
		libraries = new ArrayList<String>();
		includePaths = new ArrayList<String>();
		
		extractTokens("-l", libsString, libraries);
		extractTokens("-I", cflagsString, includePaths);
		extractTokens("", cflagsString, cflags);
		
	}

	protected void extractTokens(String prefix, String string, List<String> list) {
		
		int prefixLength = prefix.length();
		
		StringTokenizer st = new StringTokenizer(string);
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if(token.startsWith(prefix)) {
				list.add(token.substring(prefixLength).trim());
			}
		}
		
	}
	
}
