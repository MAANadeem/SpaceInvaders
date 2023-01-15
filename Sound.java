/**
 * Sound.java
 * Muhammad Nadeem
 * Quality of life for sounds
 * Allows for quick initialization of sound objects 
 * Also plays music and speeds it up if needed
 */

import java.io.*;
import java.util.ArrayList;
import java.applet.*;

class Sound {

    @Deprecated
    private AudioClip soundClip;                //the sound clip itself
    private static int clip;                    //like frame, the current audioclip
    private static double increment, time;      //emulates a delay like in the Enemy class

    //makes a sound by passing in just the file name
    public Sound(String fileName) {
        try {
            soundClip = Applet.newAudioClip(new File(fileName).toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        clip = 0;
        increment = 0.5;
        time = 20;
    }

    //plays the sound
    public void play() {
        soundClip.play();
    }
    
    //plays the music in the background as 4 audioclips on repeat with a delay between them
    public static void playMusic(Sound[] music) {
        if (increment < time) {
            increment += 0.5;
        }
        else {
            music[clip].play();
            clip = (clip + 1) % music.length;       //changes the audioclip
            increment = 0.5;
        }
    }

    //as fewer enemies are left on the field, the music gets faster
    public static void musicSpeedUp(ArrayList<Enemy> ens) {
        if (Enemy.enemiesRemaining(ens) == 1) {time = 1;}
        else if (Enemy.enemiesRemaining(ens) <= 10) {time = 5;}
        else if (Enemy.enemiesRemaining(ens) <= 25) {time = 10;}
        else if (Enemy.enemiesRemaining(ens) <= 40) {time = 15;}
        else {time = 20;}
    }
}
