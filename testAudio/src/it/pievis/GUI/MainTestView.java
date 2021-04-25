package it.pievis.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class MainTestView extends JFrame{
	
	BasicPlayer player = new BasicPlayer();
	String audioPath = "";
	float audioDurationInSeconds;
	int audioFrameSize;
	float audioFrameRate;
	
	//Components
	JButton playbtn = new JButton();
	JButton stopbtn = new JButton();
	JSlider seekbar = new JSlider();
	VisualizerTestPanel visPan = new VisualizerTestPanel();
	
	//Other
	boolean audioOpened = false;
	File audioFile;
	boolean isAnimating = true;
	private int lastSeekVal = 0;
	
	public MainTestView()
	{
		//GUI
		init();
		audioFile = new File(audioPath);
		//player
		player.addBasicPlayerListener(new BasicPlayerListener() {
			
			@Override
			public void stateUpdated(BasicPlayerEvent event) {
				// TODO Auto-generated method stub
				if(event.getCode() == BasicPlayerEvent.EOM)
				{
					//reset player state
					System.out.println("reset player stat");
					 audioOpened = false;
					 playbtn.setText("play");
					 lastSeekVal = 0;
				}
			}
			
			@Override
			public void setController(BasicController controller) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
				if(isAnimating){
					updateSeekBar(microseconds);
					updateVisualizer(pcmdata);
					}
			}
			
			@Override
			public void opened(Object stream, Map properties) {
				System.out.println("Event catched - Opened");
				File file = new File(audioPath);
			    long audioFileLength = file.length();
			    int frameSize = (int) properties.get("mp3.framesize.bytes");
			    float frameRate = (float) properties.get("mp3.framerate.fps");
			    audioFrameSize = frameSize;
			    audioFrameRate = frameRate;
			    audioDurationInSeconds = (audioFileLength / (frameSize * frameRate));
			    System.out.println("\tframesize " + frameSize + " framerate " + frameRate);
			    System.out.println("\tAudio File lenght in seconds is: " + audioDurationInSeconds);
			}
		});
	}
	
	private void printMapProperties(Map properties)
	{
		for(Object obj : properties.keySet())
			System.out.println("\t" + obj.toString());
		for(Object obj : properties.values())
			System.out.println("\t" + obj.toString());
	}
	
	private void updateVisualizer(byte[] pcmdata){
		visPan.updateWireframe(pcmdata);
	}
	
	private void updateSeekBar(long progress)
	{
		int lp = (int) (progress / 1000); //progress comes in microseconds
		int seekLenght = seekbar.getMaximum() - seekbar.getMinimum();
		int n = (int) ((lp/(audioDurationInSeconds*1000))*seekLenght); 
		seekbar.setValue(lastSeekVal + n); //lastSeekVal is necessary because progress feedback is resetted every time we seek
		//System.out.println("Seek val : " + lastSeekVal+"+"+n + " " + lp +" t:" + audioDurationInSeconds + " sl:" + seekLenght);
	}
	
	public void init()
	{
		//View
		setTitle("Test Music Player - Java - 0.1");
		setSize(300,200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//setResizable(false);
		seekbar.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent ev) {
				
				
				System.out.println("Mouse Event Released " + isAnimating);
			}
			
			@Override
			public void mousePressed(MouseEvent ev) {
				isAnimating = false;
				System.out.println("Mouse Event Pressed");
				//isAnimating = false;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		//panel
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(null);
		
		//playbutton
		playbtn.setText("play");
		playbtn.setMnemonic(KeyEvent.VK_SPACE);
		playbtn.setBounds(10, 10, 80, 30);
		playbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tooglePlay();
			}
		});
		panel.add(playbtn);
		
		//stopbutton
		stopbtn.setText("stop");
		stopbtn.setBounds(100, 10, 80, 30);
		stopbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(player.getStatus() == BasicPlayer.PLAYING)
					try {
						player.stop();
						audioOpened = false; //next time reset audio stream
						playbtn.setText("play");
						seekbar.setValue(0);
						lastSeekVal = 0;
					} catch (BasicPlayerException e1) {
						e1.printStackTrace();
					}
			}
		});
		panel.add(stopbtn);
		
		//seekbar
		seekbar.setMaximum(1000);
		seekbar.setBounds(20, 50, 250, 20);
		seekbar.setOrientation(JSlider.HORIZONTAL);
		seekbar.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent ev) {
				if(isAnimating)
					return;
				JSlider source = (JSlider) ev.getSource();
				if(!source.getValueIsAdjusting())
				{
					System.out.println("Seek stateChanged detected");
					if(player.getStatus() == BasicPlayer.PLAYING)
						lastSeekVal = source.getValue();
					float relpos = (float) source.getValue()/seekbar.getMaximum();
					long songPosition = (long) (relpos*audioDurationInSeconds);
					try {
						long seekValue = (long) (((float)songPosition*audioFrameRate)*audioFrameSize);
						player.seek(seekValue);
						System.out.println("seek done " + seekValue + " s:" + songPosition + " " + relpos);
					} catch (BasicPlayerException e) {
						e.printStackTrace();
					}
					finally{
						isAnimating = true;
					}
				}
				
			}
		});
		panel.add(seekbar);
		
		//Viauslizer Panel
		visPan.setBounds(0, 80, getWidth(), 150);
		panel.add(visPan);
	
	}
	
	private void tooglePlay(){
		if(!(player.getStatus() == BasicPlayer.PLAYING))
			try {
				if(!audioOpened){
					player.open(new File(audioPath));
					audioOpened = true;
					player.play();
					System.out.println("Player started with sonog: " + audioPath);
					}
				else
					player.resume();
				playbtn.setText("pause");
			} catch (BasicPlayerException e) {
				e.printStackTrace();
			}
		else
			try {
				player.pause();
				playbtn.setText("play");
			} catch (BasicPlayerException e) {
				e.printStackTrace();
			}
	}
	
	//Main for tests
	public static void main(String[] args){
		
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run()
			{
				MainTestView mtv = new MainTestView();
				mtv.audioPath = "C:\\Users\\Pierluigi\\workspace\\testAudio\\res\\free2.mp3";
				mtv.setVisible(true);
			}
		});
	}
}
