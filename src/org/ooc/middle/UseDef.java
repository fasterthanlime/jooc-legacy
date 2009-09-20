package org.ooc.middle;

import java.util.ArrayList;
import java.util.List;

public class UseDef {

	public static class Requirement {
		String name;
		int[] version;
		UseDef useDef = null;
		
		public Requirement(String name, int[] version) {
			this.name = name;
			this.version = version;
		}
		
		@Override
		public String toString() {
			return name+" "+version[0];
		}
		
		public String getName() {
			return name;
		}
		
		public int[] getVersion() {
			return version;
		}
		
		public UseDef getUseDef() {
			return useDef;
		}
		
		public void setDef(UseDef def) {
			this.useDef = def;
		}
	}
	
	protected String identifier;
	protected String name = "";
	protected String description = "";
	final protected List<Requirement> requirements;
	final protected List<String> pkgs;
	final protected List<String> libs;
	final protected List<String> includes;
	final protected List<String> libPaths;
	final protected List<String> includePaths;
	
	public UseDef(String identifier) {
		this.identifier = identifier;
		this.requirements = new ArrayList<Requirement>();
		this.pkgs = new ArrayList<String>();
		this.libs = new ArrayList<String>();
		this.includes = new ArrayList<String>();
		this.libPaths = new ArrayList<String>();
		this.includePaths = new ArrayList<String>();
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Requirement> getRequirements() {
		return requirements;
	}
	
	public List<String> getPkgs() {
		return pkgs;
	}
	
	public List<String> getLibs() {
		return libs;
	}
	
	public List<String> getIncludes() {
		return includes;
	}
	
	public List<String> getLibPaths() {
		return libPaths;
	}
	
	public List<String> getIncludePaths() {
		return includePaths;
	}
	
	@Override
	public String toString() {
		return "Name: "+name+", Description: "+description+", Pkgs: "+pkgs+", Libs: "+libs;
	}
	
}
