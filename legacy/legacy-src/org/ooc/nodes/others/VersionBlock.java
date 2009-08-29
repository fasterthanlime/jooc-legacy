package org.ooc.nodes.others;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.control.Scope;
import org.ubi.FileLocation;

/**
 * Allows conditional compilation for cross-platform libraries (such as the
 * standard SDK)
 * Works somehow like an #ifdef .. #endif
 * 
 * @author Amos Wenger
 */
public class VersionBlock extends Scope {

	protected static Map<String, String> map = new HashMap<String, String>();
	
	/**
	 * A version string, corresponding to a #define 
	 * @author Amos Wenger
	 */
	public static class Version {
		
		boolean inverse;
		String name;
		
		/** Default constructor */
		public Version(String name) {
			this(name, false);
		}
		
		/** Default constructor */
		public Version(String name, boolean inverse) {
			this.name = name;
			this.inverse = inverse;
		}
		
	}
	
	static {
		
		// Java's excuse for a Map literal
		map.put("windows", 		"__WIN32"); // FIXME: does that imply that we're not 64 ?
		map.put("linux", 		"__linux__");
		map.put("unix", 		"__unix__");
		map.put("beos", 		"__BEOS__");
		map.put("haiku", 		"__HAIKU__");
		map.put("apple", 		"__APPLE__");
		map.put("gnuc", 		"__GNUC__");
		map.put("i386", 		"__i386__");
		map.put("x86", 			"__X86__");
		map.put("x86_64", 		"__X86_64_");
		map.put("64", 			"__X86_64_");
		
	}
	
	protected List<Version> versions;

	/**
	 * Default constructor
	 * @param location
	 * @param id The id of this version. One of "linux", "windows"
	 */
	public VersionBlock(FileLocation location, List<Version> versions) {
		
		super(location);
		this.versions = versions;

	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		a.append("\n#if ");
		boolean first = true;
		for(Version version: versions) {
			if(!first) {
				a.append("|| ");
			}
			if(version.inverse) {
				a.append("!");
			}
			a.append("defined(");
			a.append(version.name);
			a.append(")");
			first = false;
		}
		super.writeToCSource(a);
		a.append("\n#endif");
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		super.assemble(manager);
		
		for(Version version: versions) {
		
			String match = map.get(version.name.toLowerCase());
			if(match != null) {
				version.name = match;
			} else {
				manager.warn("Unknown version id: '" + version.name
						+ "', compiling anyway (who knows?)", this);
			}
		
		}
		
	}
	
}

