package org.rubypeople.rdt.internal.core.parser;

import org.eclipse.core.resources.IFile;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.internal.core.builder.IMarkerManager;

public class ImmediateWarnings implements IRdtWarnings {

    private IFile file;
    private IMarkerManager markerManager;

	public ImmediateWarnings(IMarkerManager markerManager) {
        this.markerManager = markerManager;
    }

	public void warning(String message) {
		warn(new RdtPosition(1, 0, 0), message);
	}

	public void warning(ISourcePosition position, String message) {
		warn(position, message);
	}

	public void warn(ISourcePosition position, String message) {
		if (message.startsWith("Useless")) {
			return ;
		}
		Warning warning = new Warning(position, message);
        markerManager.addProblem(file, warning);
	}

	public void warn(String message) {
        warning(message);
	}

	public boolean isVerbose() {
		return true;
	}

    public void setFile(IFile file) {
        this.file = file;
    }
}
