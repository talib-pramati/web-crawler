/*package com.pramati.usercommitcrawler.custom;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
public class ExecuteTimingPool {
	
	public static void main(String args[]) throws ExecutionException
	{
		TimingThreadPool executor = new TimingThreadPool(10, 10, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		long wallstartTime = System.nanoTime();
		List<Callable<Long>> list = new ArrayList<Callable<Long>>();
		for(int i = 0; i < 10 ; i++)
		{
			Callable<Long> task = new Callable<Long>(){

				@Override
				public Long call() {
					
					Long sum = new Long(0);
					for(long i = 0; i < Integer.MAX_VALUE; i++)
					{
						sum+=i;
					}
					return sum;
				}
				
			};
			
			//executor.execute(task);
			
			list.add(task);
		}
		
		executor.shutdown();
		while(!executor.isTerminated())
		{
			
		}
		
		try {
			List<Future<Long>> invokeAll = executor.invokeAll(list);
			
			long sum = 0;
			for(Future<Long> f : invokeAll)
			{
				sum = sum +(long) f.get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long wallendTime = System.nanoTime();
		
		System.out.println("wallTime = " + (wallendTime - wallstartTime) / 1000000);
		
	}

}
*/