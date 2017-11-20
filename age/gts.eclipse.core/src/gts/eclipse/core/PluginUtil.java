package gts.eclipse.core;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class PluginUtil {

	public static String getInstalledPath(Bundle bundle) {
		URL url = null;
		try {
			url = Platform.asLocalURL(Platform.find(bundle, new Path("/")));
		} catch ( IOException e ) {
			e.printStackTrace();
			throw new RuntimeException("RubyTL cannot be loaded from the plugins install directory");
		}
		
		return url.getPath();
	}

	
}
