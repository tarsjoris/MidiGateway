package com.tjoris.bpmstudio;

import java.net.MalformedURLException;
import java.net.URL;

class BpmConstantAction extends AbstractBpmAction
{
	private final URL fCommand;
	
	public BpmConstantAction(final String id, final String name, final String command) throws MalformedURLException
	{
		super(id, name);
		fCommand = new URL(kBASE_URL + command);
	}

	public void performAction(final int value)
	{
		try
		{
			fCommand.getContent();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}
