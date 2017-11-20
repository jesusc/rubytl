package org.rubypeople.rdt.internal.core.parser;

import org.eclipse.core.resources.IFile;
import org.jruby.common.IRubyWarnings;

public interface IRdtWarnings extends IRubyWarnings {
    public void setFile(IFile file);
}
