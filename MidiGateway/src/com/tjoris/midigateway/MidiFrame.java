package com.tjoris.midigateway;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice.Info;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.tjoris.util.Queue;

class MidiFrame extends JFrame implements Receiver
{
	private static final long serialVersionUID = -4390339159944077019L;
	
	private static final String kPROPERTY_MIDI_IN_DEVICE = "MidiInDevice";
	private static final String kPROPERTY_FRAME_X = "FrameX";
	private static final String kPROPERTY_FRAME_Y = "FrameY";
	private static final String kPROPERTY_FRAME_WIDTH = "FrameWidth";
	private static final String kPROPERTY_FRAME_HEIGHT = "FrameHeight";
	
	private final Configuration fConfiguration;
	private final MidiWorkerThread fThread;
	private final MidiModel fModel;
	private final JToggleButton fToggleButton;
	private final JTable fTable;
	private MidiDevice fMidiDevice;
	private Transmitter fTransmitter = null;
	private final Queue fEvents;
	

	public MidiFrame(final IMidiAction[] actions, final File propertyFile) throws IOException, MidiUnavailableException
	{
		setTitle("Midi Gateway");
		final URL resource = MidiFrame.class.getResource("/icons/headphones.png");
		setIconImage(Toolkit.getDefaultToolkit().createImage(resource));
		
		fEvents = new Queue();
		fThread = new MidiWorkerThread(fEvents);
		fThread.start();
		
		fConfiguration = new Configuration(propertyFile);
		
		readProperties();
		
		fModel = new MidiModel(actions, fConfiguration);
		
		fConfiguration.clear();
		
		fToggleButton = new JToggleButton("Learn");
		fTable = new JTable(fModel);

		final JMenu menu = new JMenu("Audio");
		final ButtonGroup buttonGroup = new ButtonGroup();
		final Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < midiDeviceInfo.length; ++i)
		{
			final Info info = midiDeviceInfo[i];
			final MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
			if (midiDevice.getMaxTransmitters() != 0)
			{
				final JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(info.getName());
				buttonGroup.add(radioButtonMenuItem);
				if ((fMidiDevice != null) && (fMidiDevice.getDeviceInfo().getName().equals(info.getName())))
				{
					radioButtonMenuItem.setSelected(true);
				}
				radioButtonMenuItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(final ActionEvent e) {
						try
						{
							updateMidiDevice(midiDevice);
						}
						catch (final MidiUnavailableException exception)
						{
							exception.printStackTrace();
						}
					}
				});
				menu.add(radioButtonMenuItem);
			}
		}
		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		setJMenuBar(menuBar);

		final JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(fToggleButton, BorderLayout.NORTH);
		panel.add(new JScrollPane(fTable), BorderLayout.CENTER);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.CENTER);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(final WindowEvent e)
			{
				try
				{
					shutDown();
				}
				catch (final Exception exception)
				{
					exception.printStackTrace();
				}

				System.exit(0);
			}
		});
	}
	
	public void close()
	{
	}

	public void send(final MidiMessage message, final long timeStamp)
	{
		final int note;
		final byte[] data = message.getMessage();
		if ((data[0] >= (byte) 0x90) && (data[0] <= (byte ) 0x9F) && (data[2] != (byte) 0x00))
		{
			// note on event with a non-null velocity
			note = data[1];
		}
		else if ((data[0] >= (byte) 0xB0) && (data[0] <= (byte) 0xBF))
		{
			// control change
			note = 128 + data[1];
		}
		else
		{
			note = -1;
		}
		if (note != -1)
		{
			if (fToggleButton.isSelected())
			{
				final int selectedRow = fTable.getSelectedRow();
				if (selectedRow != -1)
				{
					final int oldRowIndex = fModel.getRowForNote(note);
					if ((oldRowIndex != -1) && (selectedRow != oldRowIndex))
					{
						fModel.setNote(oldRowIndex, -1);
					}
					fModel.setNote(selectedRow, note);
				}
			}
			else
			{
				final IMidiAction action = fModel.getAction(note);
				if (action != null)
				{
					fEvents.add(new MidiEvent(action, data[2]));
					SwingUtilities.invokeLater(new Runnable ()
					{
						public void run()
						{
							final int row = fModel.getRowForNote(note);
							fTable.getSelectionModel().setSelectionInterval(row, row);
						}
					});
				}
			}
		}
	}

	private void updateMidiDevice(final MidiDevice midiDevice) throws MidiUnavailableException
	{
		if (fMidiDevice != null)
		{
			fMidiDevice.close();
			fMidiDevice = null;
		}
		if (fTransmitter != null)
		{
			fTransmitter.close();
			fTransmitter = null;
		}
		fMidiDevice = midiDevice;
		if (fMidiDevice != null)
		{
			if (!fMidiDevice.isOpen())
			{
				fMidiDevice.open();
			}
			fTransmitter = fMidiDevice.getTransmitter();
			fTransmitter.setReceiver(this);
		}
	}
	
	private void readProperties() throws MidiUnavailableException
	{
		updateMidiDevice(getMidiDevice(fConfiguration.getSystemProperty(kPROPERTY_MIDI_IN_DEVICE)));
		final Rectangle bounds = new Rectangle(100, 100, 200, 300);
		final String propertyFrameX = fConfiguration.getSystemProperty(kPROPERTY_FRAME_X);
		if (propertyFrameX != null)
		{
			bounds.x = Integer.parseInt(propertyFrameX);
		}
		final String propertyFrameY = fConfiguration.getSystemProperty(kPROPERTY_FRAME_Y);
		if (propertyFrameY != null)
		{
			bounds.y = Integer.parseInt(propertyFrameY);
		}
		final String propertyFrameWidth = fConfiguration.getSystemProperty(kPROPERTY_FRAME_WIDTH);
		if (propertyFrameWidth != null)
		{
			bounds.width = Integer.parseInt(propertyFrameWidth);
		}
		final String propertyFrameHeight = fConfiguration.getSystemProperty(kPROPERTY_FRAME_HEIGHT);
		if (propertyFrameHeight != null)
		{
			bounds.height = Integer.parseInt(propertyFrameHeight);
		}
		setBounds(bounds);
	}
	
	private void shutDown() throws IOException
	{
		fThread.interrupt();
		fConfiguration.setSystemProperty(kPROPERTY_FRAME_X, Integer.toString(getX()));
		fConfiguration.setSystemProperty(kPROPERTY_FRAME_Y, Integer.toString(getY()));
		fConfiguration.setSystemProperty(kPROPERTY_FRAME_WIDTH, Integer.toString(getWidth()));
		fConfiguration.setSystemProperty(kPROPERTY_FRAME_HEIGHT, Integer.toString(getHeight()));
		fModel.storeProperties(fConfiguration);
		if (fMidiDevice != null)
		{
			fConfiguration.setSystemProperty(kPROPERTY_MIDI_IN_DEVICE, fMidiDevice.getDeviceInfo().getName());
		}
		fConfiguration.saveProperties();
		
		if (fMidiDevice != null)
		{
			fMidiDevice.close();
			fMidiDevice = null;
		}
		if (fTransmitter != null)
		{
			fTransmitter.close();
			fTransmitter = null;
		}
	}
	
	private static MidiDevice getMidiDevice(final String name) throws MidiUnavailableException
	{
		if (name == null)
		{
			return null;
		}
		final Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < midiDeviceInfo.length; ++i)
		{
			if (name.equals(midiDeviceInfo[i].getName()))
			{
				return MidiSystem.getMidiDevice(midiDeviceInfo[i]);
			}
		}
		return null;
	}
}
