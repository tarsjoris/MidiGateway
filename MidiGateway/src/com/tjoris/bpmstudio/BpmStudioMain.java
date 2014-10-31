package com.tjoris.bpmstudio;

import java.io.File;

import com.tjoris.midigateway.IMidiAction;
import com.tjoris.midigateway.MidiGatewayMain;

public class BpmStudioMain
{
	public static void main(String[] args)
	{
		try
		{
			MidiGatewayMain.startGateway(new IMidiAction[]
			    {
					new BpmVariableAction("Master", "Master Volume", "SETVOLUME_MASTER", 0, 100),
					new BpmVariableAction("Monitor", "Monitor Volume", "SETVOLUME_MONITOR", 0, 100),
					new BpmConstantAction("APlay", "A Play", "PLAY1"),
					new BpmConstantAction("APause", "A Pause", "PAUSE1"),
					new BpmPlayPauseAction("APlayPause", "A Play/Pause", "1"),
					new BpmConstantAction("AStop", "A Stop", "STOP1"),
					new BpmConstantAction("ACue", "A Cue", "CUE1"),
					new BpmConstantAction("ACup", "A Cup", "CUP1"),
					new BpmConstantAction("APrev", "A Previous", "PREV1"),
					new BpmConstantAction("ANext", "A Next", "NEXT1"),
					new BpmVariableAction("AVolume", "A Volume", "SETVOLUME_PLAYER1", 0, 100),
					new BpmVariableAction("APitch", "A Pitch", "SETSPEED1", -80, 80),
					new BpmConstantAction("BPlay", "B Play", "PLAY2"),
					new BpmConstantAction("BPause", "B Pause", "PAUSE2"),
					new BpmPlayPauseAction("BPlayPause", "B Play/Pause", "2"),
					new BpmConstantAction("BStop", "B Stop", "STOP2"),
					new BpmConstantAction("BCue", "B Cue", "CUE2"),
					new BpmConstantAction("BCup", "B Cup", "CUP2"),
					new BpmConstantAction("BPrev", "B Previous", "PREV2"),
					new BpmConstantAction("BNext", "B Next", "NEXT2"),
					new BpmVariableAction("BVolume", "B Volume", "SETVOLUME_PLAYER2", 0, 100),
					new BpmVariableAction("BPitch", "B Pitch", "SETSPEED2", -80, 80),
					new BpmConstantAction("AutoCrossFade", "Auto Cross Fade", "FADENOW"),
					new BpmVariableAction("CrossFader", "Cross Fader", "SETCROSSFADE", -100, 100),
					new BpmConstantAction("Sample1", "Sample 1", "SMP_PLAY1"),
					new BpmConstantAction("Sample2", "Sample 2", "SMP_PLAY2"),
					new BpmConstantAction("Sample3", "Sample 3", "SMP_PLAY3"),
					new BpmConstantAction("Sample4", "Sample 4", "SMP_PLAY4"),
					new BpmConstantAction("Sample5", "Sample 5", "SMP_PLAY5"),
					new BpmConstantAction("Sample6", "Sample 6", "SMP_PLAY6"),
					new BpmConstantAction("Sample7", "Sample 7", "SMP_PLAY7"),
					new BpmConstantAction("Sample8", "Sample 8", "SMP_PLAY8"),
					new BpmConstantAction("Sample9", "Sample 9", "SMP_PLAY9"),
					}, new File("data/gateway.ini")
			);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
}
