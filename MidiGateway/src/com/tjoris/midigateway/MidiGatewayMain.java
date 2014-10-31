package com.tjoris.midigateway;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MidiGatewayMain
{
	public static void startGateway(final IMidiAction[] actions, final File propertyFile) throws IOException, MidiUnavailableException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final MidiFrame frame = new MidiFrame(actions, propertyFile);
		frame.setVisible(true);
	}
}
