package org.rubypeople.rdt.internal.ui.compare;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IResourceProvider;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public class RubyMergeViewer extends TextMergeViewer {
	
	private IPropertyChangeListener fPreferenceChangeListener;
	private IPreferenceStore fPreferenceStore;
	private boolean fUseSystemColors;
	private RubySourceViewerConfiguration fSourceViewerConfiguration;
	private ArrayList fSourceViewer;
	
	public RubyMergeViewer(Composite parent, int styles, CompareConfiguration mp) {
		super(parent, styles | SWT.LEFT_TO_RIGHT, mp);
		
		getPreferenceStore();
		
		fUseSystemColors= fPreferenceStore.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT);
		if (! fUseSystemColors) {
			RGB bg= createColor(fPreferenceStore, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			setBackgroundColor(bg);
			RGB fg= createColor(fPreferenceStore, IRubyColorConstants.RUBY_DEFAULT);
			setForegroundColor(fg);
		}
	}
	
	@Override
	protected IDocumentPartitioner getDocumentPartitioner() {
		return RubyPlugin.getDefault().getRubyTextTools().createDocumentPartitioner();
	}
	
	protected String getDocumentPartitioning() {
		return IRubyPartitions.RUBY_PARTITIONING;
	}

	@Override
	public void setInput(Object input) {
		if (input instanceof ICompareInput) {    		
    		IRubyProject project= getRubyProject((ICompareInput)input);
			if (project != null) {
				setPreferenceStore(createChainedPreferenceStore(project));
				if (fSourceViewer != null) {
					Iterator iterator= fSourceViewer.iterator();
					while (iterator.hasNext()) {
						SourceViewer sourceViewer= (SourceViewer) iterator.next();
						sourceViewer.unconfigure();
						sourceViewer.configure(getSourceViewerConfiguration());
					}
				}
			}
    	}
    		
    	super.setInput(input);
	}
	
	public String getTitle() {
		return CompareMessages.RubyMergeViewer_title; 
	}
	
	public IRubyProject getRubyProject(ICompareInput input) {
		
		if (input == null)
			return null;
		
		IResourceProvider rp= null;
		ITypedElement te= input.getLeft();
		if (te instanceof IResourceProvider)
			rp= (IResourceProvider) te;
		if (rp == null) {
			te= input.getRight();
			if (te instanceof IResourceProvider)
				rp= (IResourceProvider) te;
		}
		if (rp == null) {
			te= input.getAncestor();
			if (te instanceof IResourceProvider)
				rp= (IResourceProvider) te;
		}
		if (rp != null) {
			IResource resource= rp.getResource();
			if (resource != null) {
				IRubyElement element= RubyCore.create(resource);
				if (element != null)
					return element.getRubyProject();
			}
		}
		return null;
	}
		
	private ChainedPreferenceStore createChainedPreferenceStore(IRubyProject project) {
	    	ArrayList stores= new ArrayList(4);
	    	if (project != null)
	    		stores.add(new EclipsePreferencesAdapter(new ProjectScope(project.getProject()), RubyCore.PLUGIN_ID));
			stores.add(RubyPlugin.getDefault().getPreferenceStore());
			stores.add(new PreferencesAdapter(RubyCore.getPlugin().getPluginPreferences()));
			stores.add(EditorsUI.getPreferenceStore());
			return new ChainedPreferenceStore((IPreferenceStore[]) stores.toArray(new IPreferenceStore[stores.size()]));
	}
	
	private RubySourceViewerConfiguration getSourceViewerConfiguration() {
		if (fSourceViewerConfiguration == null)
			getPreferenceStore();
		return fSourceViewerConfiguration;
	}
	
	private IPreferenceStore getPreferenceStore() {
		if (fPreferenceStore == null)
			setPreferenceStore(createChainedPreferenceStore(null));
		return fPreferenceStore;
	}
	
	private void setPreferenceStore(IPreferenceStore ps) {
		if (fPreferenceChangeListener != null) {
			if (fPreferenceStore != null)
				fPreferenceStore.removePropertyChangeListener(fPreferenceChangeListener);
			fPreferenceChangeListener= null;
		}
		fPreferenceStore= ps;
		if (fPreferenceStore != null) {
			RubyTextTools tools= RubyCompareUtilities.getRubyTextTools();
			fSourceViewerConfiguration= new RubySourceViewerConfiguration(tools.getColorManager(), fPreferenceStore, null, getDocumentPartitioning());
			fPreferenceChangeListener= new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					handlePropertyChange(event);
				}
			};
			fPreferenceStore.addPropertyChangeListener(fPreferenceChangeListener);
		}
	}
	
	private void handlePropertyChange(PropertyChangeEvent event) {
		
		String key= event.getProperty();
		
		if (key.equals(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND)) {

			if (!fUseSystemColors) {
				RGB bg= createColor(fPreferenceStore, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
				setBackgroundColor(bg);
			}
						
		} else if (key.equals(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT)) {

			fUseSystemColors= fPreferenceStore.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT);
			if (fUseSystemColors) {
				setBackgroundColor(null);
				setForegroundColor(null);
			} else {
				RGB bg= createColor(fPreferenceStore, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
				setBackgroundColor(bg);
				RGB fg= createColor(fPreferenceStore, IRubyColorConstants.RUBY_DEFAULT);
				setForegroundColor(fg);
			}
		} else if (key.equals(IRubyColorConstants.RUBY_DEFAULT)) {

			if (!fUseSystemColors) {
				RGB fg= createColor(fPreferenceStore, IRubyColorConstants.RUBY_DEFAULT);
				setForegroundColor(fg);
			}
		}
		
		if (fSourceViewerConfiguration != null && fSourceViewerConfiguration.affectsTextPresentation(event)) {
			fSourceViewerConfiguration.handlePropertyChangeEvent(event);
			invalidateTextPresentation();
		}
	}
	
	/**
	 * Creates a color from the information stored in the given preference store.
	 * Returns <code>null</code> if there is no such information available.
	 */
	private static RGB createColor(IPreferenceStore store, String key) {
		if (!store.contains(key))
			return null;
		if (store.isDefault(key))
			return PreferenceConverter.getDefaultColor(store, key);
		return PreferenceConverter.getColor(store, key);
	}
	
	@Override
	protected void configureTextViewer(TextViewer textViewer) {
		if (textViewer instanceof SourceViewer) {
			if (fSourceViewer == null)
				fSourceViewer= new ArrayList();
			fSourceViewer.add(textViewer);
			RubyTextTools tools= RubyCompareUtilities.getRubyTextTools();
			if (tools != null)
				((SourceViewer)textViewer).configure(getSourceViewerConfiguration());
		}
//		if (textViewer instanceof SourceViewer) {
//			
//			SourceViewer sourceViewer = ((SourceViewer) textViewer);
//			
//			RubyTextTools tools = RubyPlugin.getDefault().getRubyTextTools();
//			Document doc = new Document();
//			tools.setupRubyDocumentPartitioner(doc,	IRubyPartitions.RUBY_PARTITIONING);
//
//			IPreferenceStore[] chain = { RubyPlugin.getDefault().getCombinedPreferenceStore() };
//			ChainedPreferenceStore preferenceStore = new ChainedPreferenceStore(chain);
//
//			SimpleRubySourceViewerConfiguration viewerConfiguration = new SimpleRubySourceViewerConfiguration(tools
//					.getColorManager(), preferenceStore, null, IRubyPartitions.RUBY_PARTITIONING, true);
//			sourceViewer.configure(viewerConfiguration);
//			
//   		} else {
//			super.configureTextViewer(textViewer);
//		}
	}
	
	protected void handleDispose(DisposeEvent event) {
		setPreferenceStore(null);
		fSourceViewer= null;
		super.handleDispose(event);
	}
}
