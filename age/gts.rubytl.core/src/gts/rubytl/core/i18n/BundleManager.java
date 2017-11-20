package gts.rubytl.core.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class BundleManager {
	private ResourceBundle bundle;
	
	public BundleManager(String resource, Class klass) {
		bundle = ResourceBundle.getBundle(resource, Locale.getDefault(), klass.getClassLoader());
		
		// System.out.println("No puedo crear bundle");
		// bundle = ResourceBundle.getBundle(resource, Locale.getDefault(), klass.getClassLoader());
	}
	
	public String getString(String key) {
		try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
	}
	
}
