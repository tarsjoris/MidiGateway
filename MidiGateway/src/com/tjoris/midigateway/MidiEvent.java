package com.tjoris.midigateway;

public class MidiEvent
{
	private final IMidiAction fAction;
	private final int fValue;
	
	
	public MidiEvent(final IMidiAction action, final int value)
	{
		fAction = action;
		fValue = value;
	}
	
	public void performAction()
	{
		fAction.performAction(fValue);
	}
}
