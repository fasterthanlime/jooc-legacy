package org.ooc.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.nodes.types.TypeDef;

/**
 * A structure holding everything we're interested in from a C header file.
 * 
 * @author Amos Wenger
 */
public class CHeader {

	/** Type definitions */
	private final String path;
	private final Map<String, TypeDef> typeDefs;
	private final List<CHeader> dependencies;
	
	/**
	 * Default constructor
	 */
	public CHeader(String path) {
		
		this.path = path;
		typeDefs = new HashMap<String, TypeDef>();
		dependencies = new ArrayList<CHeader>();
		
	}

	/**
	 * @param name
	 * @return true if this header has a typedef with this name
	 */
	public boolean hasTypeDef(String name) {

		return getTypeDef(name) != null;
		
	}

	/**
	 * @param name
	 * @return the typeDef with this name, or null if not found in this header
	 */
	public TypeDef getTypeDef(String name) {
		
		List<CHeader> done = new ArrayList<CHeader>();
		return getTypeDef(name, done);
		
	}
	
	/**
	 * @return all typeDefs in this header
	 */
	public List<TypeDef> getTypeDefs() {
		
		List<TypeDef> list = new ArrayList<TypeDef>();
		getTypeDefs(list, new ArrayList<CHeader>());
		return list;
		
	}

	private void getTypeDefs(List<TypeDef> list, List<CHeader> done) {
		
		if(done.contains(this)) {
			return;
		}
		
		done.add(this);
		
		list.addAll(typeDefs.values());
		
		for(CHeader dep: dependencies) {			
			if(!done.contains(dep)) {
				dep.getTypeDefs(list, done);
			}
		}
		
	}

	private TypeDef getTypeDef(String name, List<CHeader> done) {
		
		if(done.contains(this)) {
			return null;
		}
		
		done.add(this);
		
		for(TypeDef def: typeDefs.values()) {
			if(def.getName().equals(name)) {
				return def;
			}
		}
		
		for(CHeader dep: dependencies) {
			if(!done.contains(dep)) {
				TypeDef def = dep.getTypeDef(name, done);
				if(def != null) {
					return def;
				}
			}
		}
		
		return null;
		
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the number of typeDefs in this header
	 */
	public int numTypeDefs() {

		return typeDefs.size();
		
	}

	/**
	 * Add a typedef to this header (only in memory, it won't be written <>)
	 * @param name
	 * @param typeDef
	 */
	public void addTypeDef(String name, TypeDef typeDef) {

		typeDefs.put(name, typeDef);
		
	}

	/**
	 * Add a dependency to this header (ie. caused by an #include)
	 * @param dependency
	 */
	public void addDependency(CHeader dependency) {

		dependencies.add(dependency);
		
	}
	
}
