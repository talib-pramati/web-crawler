package com.pramati.usercommitcrawler.custom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pramati.usercommitcrawler.constants.UserCommitCrawlerConstants;

public class CustomizedExecutorService {
	
	private static volatile ExecutorService executor = Executors.newFixedThreadPool(UserCommitCrawlerConstants.Maximum_Threads);
	
	private CustomizedExecutorService()
	{
		
	}
	
	public static ExecutorService getExecutor()
	{
		if(executor != null)
			return executor;
		else
		{
			synchronized(executor)
			{
				executor =  Executors.newFixedThreadPool(UserCommitCrawlerConstants.Maximum_Threads);
				return executor;
			}
		}
	}
}
