package org.rubypeople.rdt.internal.ui.text.hyperlinks;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.OpenActionUtil;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.text.hyperlinks.IHyperlinkProvider;

public class RubyElementsHyperlinkProvider implements IHyperlinkProvider {

	public RubyElementsHyperlinkProvider() {}

	class RubyElementsHyperlink implements IHyperlink {
		private IRegion fRegion;
		private final IRubyElement[] fElements;

		public RubyElementsHyperlink(IRegion region, IRubyElement[] elements) {
			fRegion = region;
			this.fElements = elements;
		}

		public IRegion getHyperlinkRegion() {
			return fRegion;
		}

		public String getHyperlinkText() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getTypeLabel() {
			// TODO Auto-generated method stub
			return null;
		}

		public void open() {
			try {
				// FIXME Check for first element which is an instanceof of
				// IMember, don't just try to access the first element!
				if (fElements != null && fElements.length > 0) {					
					OpenActionUtil.open(fElements[0], true);
				}
			} catch (PartInitException e) {
				RubyPlugin.log(e);
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
	}

	public IHyperlink getHyperlink(IEditorInput input, ITextViewer textViewer, Node node, IRegion region, boolean canShowMultipleHyperlinks) {
		if (!inCode(textViewer, region)) return null;
		IRegion newRegion = RubyWordFinder.findWord(textViewer.getDocument(), region.getOffset());
		try {
			IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
			IRubyScript script = manager.getWorkingCopy(input);
			IRubyElement[] elements = SelectionConverter.codeResolve(script, newRegion.getOffset(), newRegion.getLength());
			if (elements == null || elements.length == 0) {
				return null;
			}
			return new RubyElementsHyperlink(newRegion, elements);
		} catch (Exception e) {
			RubyPlugin.log(e);
		}
		return null;
	}

	private boolean inCode(ITextViewer textViewer, IRegion region) {
		try {
			ITypedRegion[] regions= TextUtilities.computePartitioning(textViewer.getDocument(), IRubyPartitions.RUBY_PARTITIONING, region.getOffset(), region.getLength(), false);
			if (regions == null) return false;
			for (int i = 0; i < regions.length; i++) {
				String type = regions[i].getType();
				if (type.equals(IDocument.DEFAULT_CONTENT_TYPE)) {
					return true;
				}
			}
		} catch (BadLocationException e1) {
			// ignore
		}
		return false;
	}
}