package it.pievis.test;

import it.pievis.GUI.FFTParallelFrame;

public class FreqTest extends Thread {
	FFTParallelFrame freqsFrame;
	SimulateAudio audio;
	int cycleCount = 0;
	
	@Override
	public void run() {
		while(true)
		//while(cycleCount < 2) //pochi cicli per jpf
		{
			try {
				audio.updateWave();
				byte[] d = audio.getPcmData();
				freqsFrame.updateWave(d);
				sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cycleCount++;
		}
	}
	
	public FreqTest(FFTParallelFrame ff, SimulateAudio a) {
		audio = a;
		freqsFrame = ff;
	}
	

	public static void main(String[] args)
	{
		//WaveformFrame wff = new WaveformFrame();
		FFTParallelFrame fdf = new FFTParallelFrame();
		FreqTest ft = new FreqTest(fdf, new SimulateAudio(1));
		ft.start();
		fdf.setVisible(true);
		
	}
	
}
