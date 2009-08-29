package org.ooc.middle;

import java.util.ArrayList;
import java.util.List;

public class UseDef {

	public static class Requirement {
		String name;
		List<Integer> version;
	}
	
	protected String identifier;
	protected String name = "";
	protected String description = "";
	final protected List<Requirement> requirements;
	final protected List<String> pkgs;
	final protected List<String> libs;
	final protected List<String> includes;
	
	public UseDef(String identifier) {
		this.identifier = identifier;
		this.requirements = new ArrayList<Requirement>();
		this.pkgs = new ArrayList<String>();
		this.libs = new ArrayList<String>();
		this.includes = new ArrayList<String>();
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
	
	@Override
	public String toString() {
		return "Name: "+name+", Description: "+description+", Pkgs: "+pkgs+", Libs: "+libs;
	}
	
}
