/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

final class ProjectUpgradeListener implements IResourceChangeListener {
    private final RubyPlugin plugin;

    public ProjectUpgradeListener(RubyPlugin plugin) {
        this.plugin = plugin;
    }

    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == ResourceChangeEvent.POST_CHANGE) {
            handlePostChangeEvent(event);
        }
        
    }

    private void handlePostChangeEvent(IResourceChangeEvent event) {
        IResourceDelta delta = event.getDelta();
        //                    System.out.println(delta.getKind()+"; prp=" 
        //                            + delta.getProjectRelativePath()+"; res="
        //                            +delta.getResource() + "; fullpath"+
        //                            delta.getFullPath() );
        //                    IResourceDelta[] children = delta.getAffectedChildren();
        //                    for (int i=0; i < children.length; i++) {
        //                        System.out.println("  "+children[i].getResource());
        //                    }
        if ((delta.getKind() == IResourceDelta.CHANGED)) {
            handleDeltaChange(delta);
        }
    }

    private void handleDeltaChange(IResourceDelta delta) {
        IResourceDelta[] deltas = delta.getAffectedChildren();
        for (int i=0; i<deltas.length; i++) {
            if (isProject(deltas[i])) {
                plugin.upgradeOldProjects();
                return;
            }
        }
    }

    private boolean isProject(IResourceDelta child) {
        Object project = child.getResource().getAdapter(IProject.class);
        return project != null;
    }
}

