package it.pievis.audio;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

import java.net.MalformedURLException;
import java.net.URL;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class SimpleAudioPlayer {

	String filePath;
	
	public void play(String filePath)
	{
		this.filePath = filePath;
		File afile = new File(filePath);
		try(AudioInputStream in = getAudioInputStream(afile)){
			AudioFormat outFormat = getOutFormat(in.getFormat());
            Info info = new Info(SourceDataLine.class, outFormat);
            try ( SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                if (line != null) {
                    line.open(outFormat);
                    line.start();
                    stream(getAudioInputStream(outFormat, in), line);
                    line.drain();
                    line.stop();
                }
            }
		}catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }
 
    private void stream(AudioInputStream in, SourceDataLine line) 
        throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
	
	//Main for testing
	public static void main(String[] args) {
		//new SimpleAudioPlayer().play(args[0]);
		BasicPlayer player = new BasicPlayer();
		try
		{
			player.open(new URL("file:///"+args[0]));
			player.play();
		}catch(BasicPlayerException | MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
}

