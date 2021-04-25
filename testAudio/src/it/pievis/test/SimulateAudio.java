package it.pievis.test;

/**
 * This is only for tests, this should simulate a simple wave
 * @author Pierluigi
 */
public class SimulateAudio {
	private static final int FRAME_SIZE_1 = 4608;
	private static final int FRAME_SIZE_0 = 64;
	private byte[] pcmdata = new byte[FRAME_SIZE_0];
	private int intensity = 80;
	private double phaseShift = 0.90;
	private double w = 0.0;
	
	public SimulateAudio(int fz)
	{
		if(fz == 0)
			pcmdata = new byte[FRAME_SIZE_0];
		else
			pcmdata = new byte[FRAME_SIZE_1];
	}
	
	/**
	 * Updates the wave
	 */
	public void updateWave()
	{
		for(int i = 0; i<pcmdata.length; i++)
		{
			pcmdata[i] = (byte) (Math.sin(w) * intensity);
			w+=phaseShift;
		}
	}

	public byte[] getPcmData()
	{
		return pcmdata;
	}
}
