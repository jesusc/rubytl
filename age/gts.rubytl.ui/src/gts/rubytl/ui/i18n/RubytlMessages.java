package gts.rubytl.ui.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class RubytlMessages {

	private static final String RESOURCE_BUNDLE = RubytlMessages.class.getName();
    private static ResourceBundle resourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

    private RubytlMessages() {
    }

    public static String getString(String key) {
            try {
                    return resourceBundle.getString(key);
            } catch (MissingResourceException e) {
                    return '!' + key + '!';
            }
    }

    public static String getFormattedString(String key, String arg) {
            return getFormattedString(key, new String[] { arg });
    }

    public static String getFormattedString(String key, String[] args) {
            return MessageFormat.format(getString(key), args);
    }

    public static ResourceBundle getResourceBundle() {
            return resourceBundle;
    }
	
	
}
