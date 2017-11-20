package gts.rubytl.debug.parameterdsl;

import gts.age.jruby.integration.JRubyEvaluator;
import gts.rubytl.debug.ui.DebugUIPlugin;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This class is in charge of loading the plugin parameters DSL 
 * and to update the Eclipse UI to provide the widgets
 * defined in the DSL.
 * 
 * @author jesus
 */
public class ParameterDslLoader {

	private String uiDefinitionPath;

	/**
	 * Creates a new loader.
	 * @param filename The file path where the parameters definition is stores.
	 */
	public ParameterDslLoader(String filename) {
		this.uiDefinitionPath = filename;
	}

	/**
	 * Evaluates the DSL and the result (one or more widgets) are placed
	 * into the passed composite.
	 * @param c The parent composite for the DSL result.
	 */
	public void evaluate(Composite c) {
		PluginParameterList pluginParameterList = loadUI(this.uiDefinitionPath);
		pluginParameterList.createUI(c);		
	}

	private PluginParameterList loadUI(String filename) {
		PluginParameterList pluginParameterList = new PluginParameterList();
		InputStream stream = null;
		try {
			stream = DebugUIPlugin.getDefault().getBundle().getEntry("//ruby/ui_evaluation.rb").openStream();
			JRubyEvaluator evaluator = new JRubyEvaluator();
			evaluator.loadLibrary(JRubyEvaluator.PARSE_TREE);
			// TODO: GET THE TRANSFORMATION FILENAME
			//String transformationFilename = "/home/jesus/usr/mddTool/workspace/modeling_tests/mt2007_korea/transformations/xml2mvc.rb";
			String transformationFilename = "/home/jesus/phases.rb";
			evaluator.getSharedContext().addShared("uidefinition.filename", filename);
			evaluator.getSharedContext().addShared("transformation.path", transformationFilename);
			evaluator.getSharedContext().addShared("plugin.parameters", pluginParameterList);
			evaluator.getSharedContext().setFactory(new ParameterFactory());
			evaluator.evaluate(stream);
			return pluginParameterList;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally { try {
			stream.close();
		} catch (Throwable e) {
			throw new RuntimeException();
		} }		
	}
}
