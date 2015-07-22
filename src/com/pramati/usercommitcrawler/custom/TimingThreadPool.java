package com.pramati.usercommitcrawler.custom;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.istack.internal.logging.Logger;

public class TimingThreadPool extends ThreadPoolExecutor {

	private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	private final ThreadLocal<Long> startCPUTime = new ThreadLocal<Long>();
	private final AtomicLong totalTime = new AtomicLong(0);
	private final AtomicLong userThreadCPUTime = new AtomicLong(0);
	private final AtomicLong numTask = new AtomicLong(0);
	private final Logger logger = Logger.getLogger(TimingThreadPool.class);
	ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	public TimingThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {

		super.beforeExecute(t, r);
		System.out.println("start time " + System.nanoTime());
		
		startTime.set(System.nanoTime());
		System.out.println("cpu start time" + bean.getCurrentThreadUserTime());
		startCPUTime.set(bean.getCurrentThreadUserTime());

	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {

		try {
			long executionTime = System.nanoTime() - startTime.get();
			long cpuExecutionTime = bean.getCurrentThreadUserTime() - startCPUTime.get();
			System.out.println("end Time " + System.nanoTime());
			System.out.println("end cpu time " + bean.getCurrentThreadUserTime());
			totalTime.addAndGet(executionTime);
			userThreadCPUTime.addAndGet(cpuExecutionTime);
			numTask.incrementAndGet();
		}

		finally {
			super.afterExecute(r, t);
		}

	}

	@Override
	protected void terminated() {
		try {
			
			System.out.println("Average Task Execution Time = " + (totalTime.get() / numTask.get())/1000000);
			System.out.println("Average Task Execution CPU Time = " + (userThreadCPUTime.get() / numTask.get())/1000000);

		} finally {
			super.terminated();
		}
	}

}
