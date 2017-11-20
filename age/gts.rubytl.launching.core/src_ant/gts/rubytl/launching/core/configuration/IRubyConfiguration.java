package gts.rubytl.launching.core.configuration;


/**
 * 
 * Interface to retrieve information about the Ruby interpreter
 * being used, as well as about RubyTL.
 * 
 * @author jesus
 */
public interface IRubyConfiguration {

	/**
	 * The path to the ruby interpreter. For instance,
	 * <code>/usr/local/bin/ruby</code> or <code>c:\ruby\bin\ruby.exe</code>. 
	 * 
	 * @return the path to the ruby interpreter.
	 */
	public String getRubyPath();
	
	/**
	 * The path to the RubyTL core directory. For instance,
	 * <code>/home/jesus/mdd/rubytl</code>.
	 * 
	 * @return the path to the RubyTL core directory.
	 */
	public String getRtlPath();
	
	/**
	 * The path of the directory where the project files are stored.
	 * @return the path of a RubyTL project.
	 */
	public String getProjectPath();
}
