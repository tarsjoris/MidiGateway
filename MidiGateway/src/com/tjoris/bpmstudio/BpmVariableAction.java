package com.tjoris.bpmstudio;

import java.net.MalformedURLException;
import java.net.URL;

class BpmVariableAction extends AbstractBpmAction
{
	private final String fCommand;
	private final int fMinimum;
	private final double fRatio;
	
	
	public BpmVariableAction(final String id, final String name, final String command, final int minimum, final int maximum) throws MalformedURLException
	{
		super(id, name);
		fCommand = command;
		fMinimum = minimum;
		fRatio = (maximum - minimum) / 127d;
	}
	
	public void performAction(int value)
	{
		try
		{
			final int parameter = (int) ((value * fRatio) + fMinimum);
			//System.out.println(parameter);
			new URL(kBASE_URL + fCommand + '=' + parameter).getContent();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}
