package gts.rubytl.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

public class PluginImages {

	private static final String iconUrl = "icons/";
	
	public static final String NEW_PROJECT_WIZARD = iconUrl + "newrprj_wiz_big.gif";
	public static final String NEW_TRANSFORMATION_WIZARD = iconUrl + "newtransformation.gif";	                                                     
	public static final String NEW_SOURCE_WIZARD = iconUrl + "newrubymodel.gif";
	
	public static final ImageDescriptor DESC_NEW_PROJECT_WIZARD = create(NEW_PROJECT_WIZARD);

	public static final ImageDescriptor DESC_NEW_TRANSFORMATION_WIZARD = create(NEW_TRANSFORMATION_WIZARD);

	public static final ImageDescriptor DESC_NEW_SOURCE_WIZARD = create(NEW_SOURCE_WIZARD);

	
    protected static ImageDescriptor create(String name) {
//        try {
    		return ImageDescriptor.createFromFile(PluginImages.class, name);
    	//return ImageDescriptor.createFromFile(PluginImages.class, "../../../" + name);
                // return ImageDescriptor.createFromURL(new URL(name));
//        } catch (MalformedURLException e) {
//                return ImageDescriptor.getMissingImageDescriptor();
//        }
    }

    protected static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
        // if (iconBaseURL == null)
        //        throw new MalformedURLException();

        StringBuffer buffer = new StringBuffer(prefix);
        buffer.append('/');
        buffer.append(name);
        // return new URL(iconBaseURL, buffer.toString());
        return new URL(buffer.toString());
}
	
}
