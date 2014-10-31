package com.tjoris.util;


import java.io.*;
import java.util.*;


/**
 * Copyright Doug Lea.
 */
public class Queue implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private LinkedList fList;
	private Semaphore fSemaphore;

	public Queue()
	{
		fList = new LinkedList();
		fSemaphore = new Semaphore(0);
	}

	public Queue(Collection c)
	{
		fList = new LinkedList(c);
		fSemaphore = new Semaphore(c.size());
	}

	public boolean add(Object o)
	{
		boolean result = false;
		synchronized (fList)
		{
			result = fList.add(o);
		}
		fSemaphore.release();
		return result;

	}

	public boolean addAll(Collection c)
	{
		boolean result = false;
		synchronized (fList)
		{
			result = fList.addAll(c);
		}
		fSemaphore.release(c.size());
		return result;
	}

	public boolean contains(Object o)
	{
		boolean result = false;
		synchronized (fList)
		{
			result = fList.contains(o);
		}
		return result;
	}

	/**
	 *	Tries to replace the object at the given index.
	 *
	 *	@param obj The new object
	 *	@param index The given index
	 *
	 *	@returns true if it succeeded, false otherwise
	 */
	public boolean replace(Object obj, int index)
	{
		boolean success;
		synchronized (fList)
		{
			if (fList.remove(index) == null)
			{
				success = false;
			}
			else
			{
				fList.add(index, obj);
				success = true;
			}
		}
		return success;
	}


	/**
	 * If there are no elements in the queue it will
	 * block until an object is added to the queue.
	 */
	public Object removeFirst() throws InterruptedException
	{
		fSemaphore.acquire();
		Object o = null;
		synchronized (fList)
		{
			o = fList.remove(0);
		}
		return o;
	}

	public Object removeFirst(long timeOut) throws InterruptedException
	{
		if (fSemaphore.attempt(timeOut))
		{
			Object o = null;
			synchronized (fList)
			{
				o = fList.remove(0);
			}
			return o;
		}
		throw new InterruptedException();
	}

	/**
	 * If there are no elements in the queue it will
	 * block until an object is added to the queue.
	 **/
	public Object removeLast() throws InterruptedException
	{
		fSemaphore.acquire();
		Object o = null;
		synchronized (fList)
		{
			o = fList.remove(fList.size() - 1);
		}
		return o;
	}

	public int size()
	{
		int size = 0;
		synchronized (fList)
		{
			size = fList.size();
		}
		return size;
	}
}
