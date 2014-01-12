package view;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import model.Constants;


public class SoundEffect {
    public static boolean muted = false;

    private Clip clip;

    public SoundEffect(String path) {
        try {
            if(Constants.DEBUG) System.out.println("Reading sound: "+path);
            // Use URL (instead of File) to read from disk and JAR.
            //URL url = this.getClass().getClassLoader().getResource("sounds/test.wav");
            File file = new File(path);
            // Set up an audio input stream piped from the sound file.
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            // Get a clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error Reading sound: "+path);
            System.exit(1);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public void play(){
        if(!SoundEffect.muted){
            if (clip.isRunning())
                clip.stop();   // Stop the player if it is still running
            clip.setFramePosition(0); // rewind to the beginning
            clip.start();     // Start playing
        }
    }
    public void stop(){
        clip.stop();
    }
    public void play(int n){
        if(!SoundEffect.muted){
            if (clip.isRunning())
                clip.stop();   // Stop the player if it is still running
            clip.setFramePosition(0); // rewind to the beginning
            clip.loop(n);     // Start playing
        }
    }
    public void loop(){
        if(!SoundEffect.muted){
            if (clip.isRunning())
                clip.stop();   // Stop the player if it is still running
            clip.setFramePosition(0); // rewind to the beginning
            clip.loop(Clip.LOOP_CONTINUOUSLY);    // Start playing
        }
    }
}
