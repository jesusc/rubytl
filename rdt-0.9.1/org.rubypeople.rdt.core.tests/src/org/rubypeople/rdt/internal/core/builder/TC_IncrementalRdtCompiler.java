package org.rubypeople.rdt.internal.core.builder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.rubypeople.rdt.internal.core.ShamResourceDelta;
import org.rubypeople.rdt.internal.core.util.ListUtil;

public class TC_IncrementalRdtCompiler extends AbstractRdtTestCase {

    private ShamResourceDelta delta = new ShamResourceDelta();

    public void testDeletedResource() throws Exception {
        delta.addChildren(createDelta(t1, IResourceDelta.REMOVED));
        
        compiler.compile(monitor);
        ListUtil.create(t1); //expected files
       
        monitor.assertTaskBegun("Building test...", 1);
        monitor.assertDone(1);
        List subTasks = ListUtil.create(REMOVING_MARKERS_SUB_TASK);
        monitor.assertSubTasks(subTasks);
        assertMarkersRemoved(ListUtil.create(t1));
    }

    protected void assertMarkersRemoved(List expectedFiles) {
        markerManager.assertMarkersRemovedFor(expectedFiles);
    }
    
    protected void setFiles(List filesForTest) throws Exception {
        setFiles(delta, filesForTest);
    }
    
    private void setFiles(ShamResourceDelta delta, List filesForTest) throws Exception {
        for (Iterator iter = filesForTest.iterator(); iter.hasNext();) {
            IResource resource = (IResource) iter.next();
            if (resource instanceof IFolder) {
                IFolder container = (IFolder) resource;
                ShamResourceDelta folderDelta = createDelta(container, IResourceDelta.CHANGED);
                delta.addChildren(folderDelta);
                setFiles(delta, Arrays.asList(container.members()));
                return;
            }
            IFile file = (IFile) resource;
            ShamResourceDelta childDelta = createDelta(file, IResourceDelta.ADDED);
            delta.addChildren(childDelta);
        }
    }

    private ShamResourceDelta createDelta(IResource file, int kind) {
        ShamResourceDelta childDelta = new ShamResourceDelta();
        childDelta.setResource(file);
        childDelta.setKind(kind);
        childDelta.setFlags(IResourceDelta.CONTENT);
        return childDelta;
    }

    AbstractRdtCompiler createCompiler(IMarkerManager markerManager) {
        delta.setResource(project);
        return new IncrementalRdtCompiler(project, delta, 
                markerManager);
    }

}
