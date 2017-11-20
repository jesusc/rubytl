package gts.rubytl.launching.core.launcher;

import gts.rubytl.launching.core.configuration.IModelConfiguration;
import gts.rubytl.launching.core.configuration.ITaskConfiguration;
import gts.rubytl.launching.core.configuration.ModelMapping;
import gts.rubytl.launching.core.configuration.data.ModelConfigurationData;
import gts.rubytl.launching.core.configuration.data.RakefileConfigurationData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;


/**
 * Launcher for RubyTL transformations. 
 * 
 * @author jesus
 */
public class RtlLauncher extends AbstractRubyLauncher<ITaskConfiguration> {

	private IModelConfiguration modelConfiguration;

	public RtlLauncher(ITaskConfiguration configuration, IModelConfiguration modelConfiguration) {
		super(configuration);
		this.modelConfiguration = modelConfiguration;
		if ( this.modelConfiguration == null ) {
			this.modelConfiguration = new ModelConfigurationData();
		}
	}
	
	public void execute(String taskName) {		
		File tempfile = null;
		try {
			tempfile = File.createTempFile("rubytl_launcher", ".temp.rakefile");
			String task = configuration.getTaskWriter().writeConfiguration(this.configuration, taskName);				
			String rakefile = writeModelConfiguration() + task; 
					
	        BufferedWriter out = new BufferedWriter(new FileWriter(tempfile));
	        out.write(rakefile);
	        out.close();		
	        
	        RakefileConfigurationData conf = new RakefileConfigurationData(
	        		configuration.getRubyPath(), configuration.getRtlPath(),
	        		configuration.getProjectPath(), tempfile.getAbsolutePath() );
	        RakefileLauncher launcher = new RakefileLauncher(conf);
	        launcher.addProcessListener(new DeleteTempFileOnFinish(tempfile));
	        launcher.execute(taskName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String writeModelConfiguration() {
		String result = "select_rmof_handler :new_handler" + "\n";
		
		Collection<ModelMapping> mappings = this.modelConfiguration.getMappings();
		if ( mappings.size() > 0 ) {
			result += "model_mappings_definition do" + "\n";
			for (ModelMapping modelMapping : mappings) {
				result += "map '" + modelMapping.getURI() + "' => '" + modelMapping.getFilename() + "'" + "\n"; 
			}
			result += "end" + "\n";
		}
		
		return result;
	}

	private class DeleteTempFileOnFinish implements IProcessListener {
		private File tempFile;
		
		public DeleteTempFileOnFinish(File tempFile) {
			this.tempFile = tempFile;
		}
		
		public void processFinished() {
			tempFile.delete();
		}
	}
}
