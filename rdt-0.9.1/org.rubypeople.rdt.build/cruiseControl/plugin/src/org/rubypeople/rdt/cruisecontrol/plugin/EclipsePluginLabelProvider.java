package org.rubypeople.rdt.cruisecontrol.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.cruisecontrol.LabelIncrementer;

import org.jdom.Element;

public class EclipsePluginLabelProvider implements LabelIncrementer
{
	private String defaultLabel = "0.1.0" ;
	private String prefix = "0.8.0" ;
	
	public EclipsePluginLabelProvider() {
		super() ;
	}
	
	public boolean isPreBuildIncrementer() {
		return true ;
	}

	public void setDefaultLabel(String label) {
		defaultLabel = label ;
	}

	public String getDefaultLabel() {
		return defaultLabel ;
	}

	public boolean isValidLabel(String label, Element element) {
		return true ;
	}
	
	 public String incrementLabel(String oldLabel, Element buildLog) {
	 return prefix + "." + this.getFormattedDate() + "NGT" ;
	 }

	public boolean isValidLabel(String label) {
		return true;
	}
	
	private String getFormattedDate() {
		String formattedDate = new SimpleDateFormat("yyMMddHHmm").format(new Date()) ;
		if (formattedDate.startsWith("0")) {
			return formattedDate.substring(1) ;
		}
		else {
			return formattedDate ;
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
