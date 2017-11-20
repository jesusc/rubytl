package gts.rubytl.launching.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Util {
	
	public static boolean isWindowsPlatform(){
		String env = System.getenv("windir");
		return env != null;
	}
	
	public static String changeOSSlashes(String path){
		// TODO: usar Platform.getOS() en vez de getenv() --> no funciona
		return isWindowsPlatform()?path.replaceAll("/", "\\\\"):path.replaceAll("\\\\", "/");
	}
	
	public static String getOSSlash(){
		// TODO: usar Platform.getOS() en vez de getenv()
		return isWindowsPlatform()?"\\":"/";		
	}

	public static interface IMap<T, K> {
		public K map(T t);
	}
	
	public static <T, K> Collection<K> map(Collection<T> collection, IMap<T, K> f) {
		LinkedList<K> result = new LinkedList<K>();
		for (T t : collection) {
			K k = f.map(t);
			result.add(k);
		}
		return result;
	}
	
	public static String join(Collection<String> collection, String separator) {
		String result = "";
		int i = 0;
		for (String string : collection) {
			result += string;
			i += 1;
			if ( i != collection.size() ) {
				result += separator;
			}
		}
		return result;
	}
}
