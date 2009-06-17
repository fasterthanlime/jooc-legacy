package org.ooc.compiler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Contains information about the name and package of a source file.
 * 
 * @author Amos Wenger
 */
public class SourceInfo {
	
	/**
	 * e.g. "my.super.package"
	 */
	public String pakageName;
	
	/**
	 * List of '.'-separated components of {@link SourceInfo#pakageName} 
	 */
	public List<String> pakage;
	
	/**
	 * e.g. "MyClass"
	 */
	public final String simpleName;
	
	/**
	 * e.g. "my.super.package.MyClass"
	 */
	public final String fullName;

	/**
	 * e.g. "my_super_package_MyClass"
	 */
	public final String underName;

	/**
	 * e.g. "src/"
	 */
	public String sourceElementPath;
	
	/**
	 * Default constructor
	 * @param fullSourceName @link {@link SourceInfo#fullName}
	 */
	public SourceInfo(String fullSourceName) {
		
		int index = fullSourceName.lastIndexOf('.');
		pakageName = (index == -1) ? "" : fullSourceName.substring(0, index);
		simpleName = fullSourceName.substring(index + 1);
		pakage = new ArrayList<String>();
		sourceElementPath = "";
		 
		if(pakageName.isEmpty()) {
			fullName = simpleName;
		} else {
			// Step 1: break down the pakageName into tokens, and resolve "super"s
			StringTokenizer sT = new StringTokenizer(pakageName, ".");
			while(sT.hasMoreElements()) {
				String token = sT.nextToken();
				if(token.equals("super") && !pakage.isEmpty()) {
					pakage.remove(pakage.size() - 1);
				} else {
					pakage.add(token);
				}
			}
			
			// Step 2: rebuild the pakageName from the token list.
			StringBuilder builder = new StringBuilder();
			int count = 0;
			for(String token: pakage) {
				builder.append(token);
				if(++count < pakage.size()) {
					builder.append(".");
				}
			}
			pakageName = builder.toString();

			fullName = pakageName+"."+simpleName;
		}
		
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < fullName.length(); i++) {
			int codePoint = fullName.codePointAt(i);
			if(!(Character.isLetterOrDigit(codePoint) || codePoint == '_')) {
				builder.append('_');
			} else {
				builder.appendCodePoint(codePoint);
			}
		}
		this.underName = builder.toString();
		
	}
	
	@Override
	public String toString() {
		return "fullName = "+fullName
		+ "\npakage = "+pakageName
		+ "\nsimpleName = "+simpleName;
	}

	/**
	 * @param info
	 * @return the path of the source represented by this info, relative to another
	 * info, represented by the parameter. E.g. for class "my.super.package.MyClass"
	 * relative to "my.other.superpackage.MyOtherClass", gives "../../super/package/"
	 */
	public String getRelativePath(SourceInfo info) {

		StringBuilder builder = new StringBuilder();
		
    	if(pakageName.equals(info.pakageName)) {
    		
    		// it's okay
    		
    	} else if(pakageName.length() < info.pakageName.length() && info.pakageName.startsWith(pakageName)) {
    		
    		String relative = info.pakageName.substring(pakageName.length()).replace('.', '/');
    		if(relative.charAt(0) == '/') {
    			relative = relative.substring(1);
    		}
			builder.append(relative);
    		builder.append('/');
    		
    	} else {
    		
    		if(info.pakage.size() >= pakage.size()) {
    			for(int i = 0; i < info.pakage.size(); i++) {
	    			String depPart = info.pakage.get(i);
	    			String ourPart = pakage.get(i);
	    			if(!depPart.equals(ourPart)) {
	    				for(int j = i; j < pakage.size(); j++) {
	    					builder.append("../");
	    				}
	    				for(int j = i; j < info.pakage.size(); j++) {
	    					builder.append(info.pakage.get(j));
	    					builder.append("/");
	    				}
	    			}
	    		}
    		} else {
    			for(int i = 0; i < this.pakage.size(); i++) {
    				builder.append("../");
    			}
    			if(!info.pakage.isEmpty()) {
    				builder.append(info.pakageName.replace('.', '/'));
    				builder.append('/');
    			}
    		}
    		
    	}
    	
    	return builder.toString();
		
	}
	
	/**
	 * @return e.g. for class "MyClass" in package "my.super.package", return "my/super/package/MyClass.ooc"
	 */
	public String getPath() {
		
		return fullName.replace('.', File.separatorChar) + ".ooc";
		
	}

	/**
	 * @return e.g. for class "MyClass" in package "my.super.package", return "my.super.package.MyClass"
	 */
	public String getFullName(String simpleName) {
		
		return pakageName.isEmpty() ? simpleName : pakageName + "." + simpleName;
		
	}

	/**
	 * @return e.g. for class "MyClass" in package "my.super.package", return "my/super/package/MyClass"
	 */
	public String getBaseName() {
		
		return pakageName.isEmpty() ? simpleName : pakageName.replace('.', File.separatorChar) + "/" + simpleName;
		
	}
}
