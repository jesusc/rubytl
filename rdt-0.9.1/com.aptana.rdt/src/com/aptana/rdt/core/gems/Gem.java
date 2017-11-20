package com.aptana.rdt.core.gems;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Gem implements Comparable {
	
	private String name;
	private String version;
	private String description;
	private String platform;
	public static final String RUBY_PLATFORM = "ruby";
	public static final String MSWIN32_PLATFORM = "mswin32";

	public Gem(String name, String version, String description) {
		this(name, version, description, "ruby");
	}
	
	public Gem(String name, String version, String description, String platform) {
		if (name == null) throw new IllegalArgumentException("A Gem's name must not be null");
		if (version == null) throw new IllegalArgumentException("A Gem's version must not be null");
		if (platform == null) throw new IllegalArgumentException("A Gem's platform must not be null");
		this.name = name;
		this.version = version;
		this.description = description;
		this.platform = platform;
	}


	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}
	
	public String getPlatform() {
		return platform;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Gem)) return false;
		Gem other = (Gem) arg0;
		return getName().equals(other.getName()) && getVersion().equals(other.getVersion()) && getPlatform().equals(other.getPlatform());
	}
	
	@Override
	public int hashCode() {
		return (getName().hashCode() * 100) + getVersion().hashCode();
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof Gem)) return -1;
		Gem other = (Gem) arg0;
		return toString().compareTo(other.toString());
	}
	
	@Override
	public String toString() {
		return getName().toLowerCase() + " " + getVersion() + " " + getPlatform();
	}

	public boolean hasMultipleVersions() {
		return version != null && version.indexOf(",") != -1;
	}

	public List<String> versions() {
		List<String> versions = new ArrayList<String>();
		if (version == null) return versions;
		StringTokenizer tokenizer = new StringTokenizer(version, ",");
		while (tokenizer.hasMoreTokens()) {
			versions.add(tokenizer.nextToken().trim());
		}
		return versions;
	}

}
