package com.tjoris.bpmstudio;

abstract class AbstractBpmAction implements IBpmAction
{
	private final String fID;
	private final String fName;
	
	
	public AbstractBpmAction(final String id, final String name)
	{
		fID = id;
		fName = name;
	}
	
	public String getName()
	{
		return fName;
	}

	public String getID()
	{
		return fID;
	}
}
