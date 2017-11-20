package gts.eclipse.emf.tasks;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EMOFResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

public class MetamodelConverter {
		
	public String convertToEcore(File file) {
		String sourceFile = file.getAbsolutePath();
		String targetFile = new Path(sourceFile).removeFileExtension().toOSString() + ".ecore";
		convertTo(sourceFile, targetFile);
		return targetFile;
	}

	public void convertToEMOF(File file) {
		String sourceFile = file.getAbsolutePath();
		String targetFile = new Path(sourceFile).removeFileExtension().toOSString() + ".emof";
		
		convertTo(sourceFile, targetFile);
	}
	
	private void convertTo(String sourceFile, String targetFile) {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emof", new EMOFResourceFactoryImpl());
		
		// Lectura del modelo origen
		ResourceSet rsSource = new ResourceSetImpl();		
		Resource resSource = rsSource.getResource(URI.createFileURI(sourceFile), true);
		
		// Lectura del modelo destino
		ResourceSet rsTarget = new ResourceSetImpl();
		Resource resTarget = rsTarget.createResource(URI.createFileURI(targetFile));

		// Escritura a disco
		try {
			resTarget.getContents().add(resSource.getContents().get(0));
			resTarget.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}				
	}
	
}
