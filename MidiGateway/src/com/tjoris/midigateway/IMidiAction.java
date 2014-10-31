package com.tjoris.midigateway;

public interface IMidiAction
{
	public String getName();
	
	public String getID();
	
	public void performAction(int value);
}
