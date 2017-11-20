package gts.age.jruby.integration;

import gts.age.jruby.Activator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.jruby.RubyObject;
import org.jruby.exceptions.RaiseException;

/**
 * This class is intended to evaluate Ruby scripts. 
 * 
 * @author jesus
 */
public class JRubyEvaluator {
	
	public static final String PARSE_TREE = "jparsetree";
	protected BSFManager manager;
	protected SharedContext sharedContext;
	
	public JRubyEvaluator() {
		this.manager = this.getBSFManager();
		this.sharedContext = new SharedContext(this);
		this.addBean(sharedContext, "sharedContext");
	}
	
	public SharedContext getSharedContext() {
		return sharedContext;
	}
	
	/**
	 * Add a new object that is shared through a name with the
	 * Ruby scripts.
	 * @param obj
	 * @param name
	 * @throws BSFException 
	 */
	public void addBean(Object obj, String name) {
		try {
			this.manager.declareBean(name, obj, obj.getClass());
		} catch (BSFException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads a JRuby library within the evaluation context. Libraries
	 * are just files in the ruby-lib plugin's directory, without '.rb'
	 * @param libraryName The library name.
	 */
	public void loadLibrary(String libraryName) {
		InputStream stream = null;
		try {
			stream = Activator.getDefault().getBundle().getEntry("/ruby-lib/" + libraryName + ".rb").openStream();
			evaluate(stream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally { try { stream.close(); } catch (IOException e) { } }
	}
	
	/**
	 * Read a Ruby script.
	 * @param script
	 * @return
	 */
	public String readRubyScript(String filename) {
		try {
			return readRubyScript(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String readRubyScript(InputStream stream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream)) ;
			StringBuffer buffer = new StringBuffer();
			String line = null;
			
			while ( (line = reader.readLine()) != null ) {
				buffer.append(line + "\n");
			}	
			
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
		
	/**
	 * Evaluates an input stream. 
	 *
	 */
	public void evaluate(InputStream stream) {
		try {	
			String script = readRubyScript(stream);
			String filename = "(eval)"; //probably a parameter		
			manager.exec("ruby", filename, 0, 0, script);		
		} catch (BSFException e) {
//		  } catch (RaiseException e) {
//			    e.getException().printBacktrace(System.err);
			//System.out.println(e.getMessage());
			e.printStackTrace();
			RaiseException k = ((RaiseException) e.getTargetException());
			if ( k != null ) {
				System.err.println(k.getMessage());
				k.getException().printBacktrace(System.err);
			}
			
			//e.printStackTrace();
			//throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Returns a BSF manager that is able to handle Ruby scripts.
	 * @return An instance of BSFManager.
	 */
	private BSFManager getBSFManager() {
		BSFManager.registerScriptingEngine("ruby", "org.jruby.javasupport.bsf.JRubyEngine", 
                						   new String[] { "rb" });
		
		return new BSFManager();		
	}
}
