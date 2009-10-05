package org.ooc.frontend.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class VersionBlock extends Block implements MustBeResolved {

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
		
		public boolean isInverse() {
			return inverse;
		}
		
		public void setInverse(boolean inverse) {
			this.inverse = inverse;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
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
	
	private List<Version> versions;

	/**
	 * Default constructor
	 * @param location
	 * @param id The id of this version. One of "linux", "windows", etc.
	 */
	public VersionBlock(List<Version> versions, Token startToken) {
		super(startToken);
		this.versions = versions;
	}
	
	public List<Version> getVersions() {
		return versions;
	}

	public boolean isResolved() {
		return false;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		for(Version version: versions) {
			String match = map.get(version.name.toLowerCase());
			if(match != null) {
				version.name = match;
			} else {
				System.out.println(new OocCompilationError(this, stack,
						"Unknown version id: '" + version.name
						+ "', compiling anyway (who knows?)").toString());
			}
		}
		return Response.OK;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
