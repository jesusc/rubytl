package gts.rubytl.launching.core.launcher;

import gts.rubytl.launching.core.Util;
import gts.rubytl.launching.core.configuration.IRubyConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;


/**
 * Abstract class for any 
 * 
 * @author jesus
 */
public abstract class AbstractRubyLauncher<T extends IRubyConfiguration> {
	private static final String WIN_CMD = "cmd /c ";
	protected T configuration;
	protected LinkedList<IProcessListener> listeners;

	public AbstractRubyLauncher(T configuration) {
		this.configuration = configuration;
		this.listeners = new LinkedList<IProcessListener>();
	}
	
	public void addProcessListener(IProcessListener listener) {
		this.listeners.add(listener);
	}
	

	protected String computeRubytlCommand(String rubytlPath) {
		File f = new File(rubytlPath);
		if ( ! f.exists() ) throw new RuntimeException(rubytlPath + " not exists");
		if ( f.isDirectory() ) {
			String path = rubytlPath + File.separator + "lib" + File.separator + "rubytl.rb";
			// TODO: Check path?
			return path;
		}
		else if ( rubytlPath.endsWith("rubytl.rb") ) {			
			return rubytlPath;
		}
		
		throw new RuntimeException("Invalid path: " + rubytlPath);
	}
	
	protected void launchCommand(String command) {
		String execution = command;
		if (Util.isWindowsPlatform()) 
			execution += WIN_CMD + " " + command;

		try {
			Process p = Runtime.getRuntime().exec(execution);
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String s;
			while ((s = stdError.readLine()) != null) {
				System.err.println(s);
			}
			BufferedReader stdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdOutput.readLine()) != null) {
				System.out.println(s);
			}
			
			for (IProcessListener listener : this.listeners) {
				listener.processFinished();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// TODO: Allow parametrizing the how to create the process, to allow Eclipse process creation.
	}
	
}
