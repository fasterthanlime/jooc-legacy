package org.ooc.frontend.pkgconfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.ooc.utils.ShellUtils;
import org.ubi.CompilationFailedError;

/**
 * A frontend to pkgconfig, to retrieve information for packages,
 * like gtk+-2.0, gtkgl-2.0, or imlib2
 * 
 * @author Amos Wenger
 */
public class PkgConfigFrontend {

	protected static Map<String, PkgInfo> cache = new HashMap<String, PkgInfo>();
	
	/**
	 * 
	 * @param pkgName
	 * @return the information concerning a package managed by pkg-manager
	 */
	public static PkgInfo getInfo(String pkgName) {
		
		PkgInfo cached = cache.get(pkgName);
		if(cached != null) {
			return cached;
		}
		
		File path = ShellUtils.findExecutable("pkg-config");
		if(path == null) {
			throw new Error("Error! the 'pkg-config' tool, necessary to resolve package '"
					+pkgName+"' couldn't be find in the $PATH, which is "+System.getenv("PATH"));
		}
		String libs = ShellUtils.getOutput(path.getPath(), "--libs", pkgName);
		String cflags = ShellUtils.getOutput(path.getPath(), "--cflags", pkgName);
		
		if(libs == null) {
			throw new CompilationFailedError(null, "Can't find package '"+pkgName
					+"' in PKG_CONFIG_PATH. Have you configured pkg-config correctly?");
		}
		
		PkgInfo pkgInfo = new PkgInfo(pkgName, libs, cflags);
		cache.put(pkgName, pkgInfo);
		return pkgInfo;
		
	}
	
}
