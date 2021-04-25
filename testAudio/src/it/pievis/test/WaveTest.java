package it.pievis.test;

import it.pievis.GUI.WaveformParallelFrame;

public class WaveTest extends Thread {
	WaveformParallelFrame waveFrame;
	SimulateAudio audio;
	int cycleCount = 0;
	
	@Override
	public void run() {
		while(true)
		//while(cycleCount < 4) //pochi cicli per jpf
		{
			try {
				audio.updateWave();
				byte[] d = audio.getPcmData();
				waveFrame.updateWave(d);
				sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cycleCount++;
		}
	}
	
	public WaveTest(WaveformParallelFrame ff, SimulateAudio a) {
		audio = a;
		waveFrame = ff;
	}
	

	public static void main(String[] args)
	{
		WaveformParallelFrame wff = new WaveformParallelFrame();
		WaveTest ft = new WaveTest(wff, new SimulateAudio(1));
		ft.start();
		wff.setVisible(true);
		
	}
	
}
