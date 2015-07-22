package com.pramati.usercommitcrawler.utils;

import java.io.IOException;

public class Test {	
	
	public static String str = "abc";

	public static void main(String[] args) throws IOException
	{
		
		new A().start();
	}
	
	
}
class A extends Thread
{
	public void run()
	{
		System.out.println("Hello I am in A...");
	}
	
}

class B extends A implements Runnable{
	public void run()
	{
		System.out.println("I am in B");
	}
}

