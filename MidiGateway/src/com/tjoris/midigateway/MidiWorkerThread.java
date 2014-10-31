package com.tjoris.midigateway;

import com.tjoris.util.Queue;

class MidiWorkerThread extends Thread
{
	private final Queue fEvents;
	
	
	public MidiWorkerThread(final Queue events)
	{
		super("MidiWorkerThread");
		fEvents = events;
	}
	
	public void run()
	{
		try {
			for (;;)
			{
				((MidiEvent) fEvents.removeFirst()).performAction();
			}
		}
		catch (final InterruptedException e)
		{
			// stop
		}
	}
}
