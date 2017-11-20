package org.rubypeople.rdt.internal.core.builder;

import java.util.List;


public class TC_CleanRdtCompiler extends AbstractRdtTestCase {

    AbstractRdtCompiler createCompiler(IMarkerManager markerManager) {
        return new CleanRdtCompiler(project, markerManager);
    }

    protected void assertMarkersRemoved(List expectedFiles) {
        markerManager.assertMarkersRemovedFor(project);
    }
}
