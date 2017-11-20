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
package org.rubypeople.rdt.internal.core.util;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class TC_EclipseJobScheduler extends TestCase {

    public void testSchedule() throws Exception {
        TestJob job = new TestJob();
        EclipseJobScheduler scheduler = new EclipseJobScheduler();
        scheduler.schedule(job);
        job.join();
        job.assertRun();
    }

    public void testExecute() throws Exception {
        TestJob job = new TestJob();
        EclipseJobScheduler scheduler = new EclipseJobScheduler();
        scheduler.execute(job);
        job.assertRun();
    }

    private static class TestJob extends Job {
        public TestJob() {
            super("test Job");
        }

        private boolean ran;
        
        protected IStatus run(IProgressMonitor monitor) {
            ran = true;
            return Status.OK_STATUS;
        }

        public void assertRun() {
            Assert.assertTrue("expected to Run", ran);
        }

    }

}
