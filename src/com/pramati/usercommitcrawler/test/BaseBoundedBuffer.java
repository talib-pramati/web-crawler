package com.pramati.usercommitcrawler.test;

public final class BaseBoundedBuffer<V> {
	
	private final V[] buf;
	private int head;
	private int tail;
	private int count;
	
	BaseBoundedBuffer(int capacity)
	{
		this.buf = (V[])new Object[capacity];
	}
	
	protected synchronized final void doPut(V v)
	{
		buf[tail] = v;
		if(++tail == buf.length)
			tail = 0;
		++count;
	}
	
	protected synchronized final V doTake()
	{
		V v = buf[head];
		buf[head] = null;
		if(++head == buf.length)
			head = 0;
		--count;
		return v;
	}
	
	public  synchronized final boolean isFull()
	{
		return count == buf.length;
	}
	
	public synchronized final boolean isEmpty()
	{
		return count == 0;
	}

}
