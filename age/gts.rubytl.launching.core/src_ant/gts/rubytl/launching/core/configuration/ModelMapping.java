package gts.rubytl.launching.core.configuration;

public class ModelMapping {

	protected String uri;
	protected String filename;
	
	public ModelMapping(String uri, String filename) {
		this.uri = uri;
		this.filename = filename;
	}
	
	public String getURI() { return uri; }
	public String getFilename() { return filename;  }
}
