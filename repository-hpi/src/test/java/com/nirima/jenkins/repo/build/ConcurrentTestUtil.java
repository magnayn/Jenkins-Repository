package com.nirima.jenkins.repo.build;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
class ConcurrentTestUtil {
    public static void executeConcurrent(final String message, final Iterable<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
        final Stopwatch stopWatch = new Stopwatch();
        stopWatch.start();
        final int numThreads = Iterables.size(runnables);
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(new TestRunnable(exceptions, allExecutorThreadsReady, afterInitBlocker, allDone, submittedTestRunnable));
            }
            // wait until all threads are ready
            assertTrue("Timeout submitting Runnables to the ExecutorService",//
                allExecutorThreadsReady.await((numThreads * 10) + 200, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue(message + " timeout! More than " + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
            stopWatch.stop();
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue(message + "failed with exception(s) " + exceptions, exceptions.isEmpty());
        LoggerFactory.getLogger(ConcurrentTestUtil.class).info(message + " took " + stopWatch.elapsedTime(TimeUnit.MILLISECONDS) + "ms for " + numThreads);
    }

    private static final class TestRunnable implements Runnable {
        private final List<Throwable> exceptions;
        private final CountDownLatch allExecutorThreadsReady;
        private final CountDownLatch afterInitBlocker;
        private final CountDownLatch allDone;
        private final Runnable submittedTestRunnable;

        private TestRunnable(final List<Throwable> exceptions, final CountDownLatch allExecutorThreadsReady, final CountDownLatch afterInitBlocker, final CountDownLatch allDone,
                final Runnable submittedTestRunnable) {
            this.exceptions = exceptions;
            this.allExecutorThreadsReady = allExecutorThreadsReady;
            this.afterInitBlocker = afterInitBlocker;
            this.allDone = allDone;
            this.submittedTestRunnable = submittedTestRunnable;
        }

        public void run() {
            allExecutorThreadsReady.countDown();
            try {
                afterInitBlocker.await();
                submittedTestRunnable.run();
            } catch (final Throwable e) {
                exceptions.add(e);
            } finally {
                allDone.countDown();
            }
        }
    }
}
