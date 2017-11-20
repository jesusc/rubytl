package gts.rubytl.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.RubySourceLocator;
import org.rubypeople.rdt.internal.debug.ui.console.RubyConsoleTracker;
import org.rubypeople.rdt.internal.debug.ui.console.RubyStackTraceHyperlink;
import org.rubypeople.rdt.internal.debug.ui.console.RubyConsoleTracker.FileExistanceChecker;
import org.rubypeople.rdt.internal.ui.util.StackTraceLine;

public class RubytlConsoleTracker extends RubyConsoleTracker {

	private IConsole fConsole;

	public void init(IConsole pConsole) {
		super.init(pConsole);
		fConsole = pConsole;
	}

	public void lineAppended(IRegion line) {
		try {
			int offset = line.getOffset();
			int length = line.getLength();
			int prefix = 0;
			String text = fConsole.getDocument().get(offset, length);
			while (StackTraceLine.isTraceLine(text)) {
				StackTraceLine stackTraceLine = new StackTraceLine(text);
				if (! existanceChecker.fileExists(stackTraceLine.getFilename())) {
					System.out.println("No existe " + stackTraceLine.getFilename());
					return;
				}
				IHyperlink link = new RubyStackTraceHyperlink(fConsole, stackTraceLine);
				fConsole.addLink(link, line.getOffset() + prefix + stackTraceLine.offset() , stackTraceLine.length());
								
				prefix = stackTraceLine.offset() + stackTraceLine.length();
				text = text.substring(stackTraceLine.offset() + stackTraceLine.length());
				if (text.startsWith(":in `require':")) {
					text = text.substring(14);
					prefix += 14;
				}
			}
		} catch (BadLocationException e) {
		}
	}

}
