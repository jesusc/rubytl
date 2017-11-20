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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.runtime.jobs.Job;

public class ShamJobScheduler implements IJobScheduler {

    private List scheduledJobs = new ArrayList();

    public void assertScheduled(Job expectedJob) {
        Assert.assertTrue("Expected " + expectedJob + " to be scheduled", scheduledJobs.contains(expectedJob));
    }

    public void schedule(Job job) {
        scheduledJobs.add(job);
    }

    public void execute(Job job) throws InterruptedException {
        schedule(job);
        job.schedule();
        job.join();
        
    }
}
