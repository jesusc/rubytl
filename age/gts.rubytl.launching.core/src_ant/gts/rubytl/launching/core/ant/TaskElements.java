package gts.rubytl.launching.core.ant;

import java.io.File;

public class TaskElements {

	public static class PathElement {
		private String path;

		public void setPath(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
	}

	public static class FilePathElement {
		private File path;

		public void setPath(File path) {
			this.path = path;
		}
		
		public File getPath() {
			return path;
		}
	}
	public static class NameElement {
		private String name;

		public void setName(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}

	public static class NamespaceElement {
		private String namespace;
		private String metamodel;
		
		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
		
		/** Synonym for: setNamespace */
		public void setName(String namespace) { setNamespace(namespace); }

		public String getNamespace() {
			return namespace;
		}
		
		public void setMetamodel(String metamodel) {
			this.metamodel = metamodel;
		}
		
		public String getMetamodel() {
			return metamodel;
		}
	}
	
	public static class ModelMapping {
		private String uri;
		private String file;
		
		public void setUri(String uri) {
			this.uri = uri;
		}
		
		public String getUri() {
			return uri;
		}
		
		public void setFile(String file) {
			this.file = file;
		}
		
		public String getFile() {
			return file;
		}
	}
	
}
