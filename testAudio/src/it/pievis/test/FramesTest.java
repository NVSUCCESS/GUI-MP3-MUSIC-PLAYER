package it.pievis.test;

import it.pievis.GUI.FFTParallelFrame;
import it.pievis.GUI.WaveformParallelFrame;

public class FramesTest extends Thread {
	FFTParallelFrame freqsFrame;
	WaveformParallelFrame waveFrame;
	SimulateAudio audio;
	//int cycleCount = 0;
	//Uso per performance
	
	
	@Override
	public void run() {
		while(true)
		//while(cycleCount < 4)
		{
			try {
				audio.updateWave();
				byte[] d = audio.getPcmData();
				freqsFrame.updateWave(d);
				waveFrame.updateWave(d);
				sleep(60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public FramesTest(FFTParallelFrame ff, WaveformParallelFrame wff, SimulateAudio a) {
		audio = a;
		freqsFrame = ff;
		waveFrame = wff;
	}
	

	public static void main(String[] args)
	{
		WaveformParallelFrame wff = new WaveformParallelFrame();
		FFTParallelFrame fdf = new FFTParallelFrame();
		FramesTest ft = new FramesTest(fdf , wff, new SimulateAudio(1));
		//FramesTest ft = new FramesTest(null , wff, new SimulateAudio());
		ft.start();
		wff.setVisible(true);
		fdf.setVisible(true);
		
	}
	
}
