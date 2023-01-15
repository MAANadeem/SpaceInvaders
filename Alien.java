/**
 * Alien.java
 * Muhammad Nadeem
 * Creates the alien ufo that flies on the top of the screen frequently
 * Plays a sound when it appears and when it dies
*/

import java.awt.*;
import javax.swing.ImageIcon;

class Alien {
    private int points;                             //point value of the alien
    private int alienX, alienY, dx, clip;           //x and y coordinate, and speed in the x direction, along with the current audioclip
    private double increment, time;                 //emulates a delay like in the Enemy class
    private Image alienPic = new ImageIcon("images/space_invaders_alien.png").getImage();
    private boolean canMove;                        //if a certain condition is met, only then can the alien come out and move
    
    //sounds that the alien can make, 1st is when it moves, 2nd is when it is shot
    public final Sound[] alienSounds = {new Sound("sounds/ufo_highpitch.wav"),new Sound("sounds/ufo_lowpitch.wav")};

    public Alien(int x, int y) {
        alienX = x;
        alienY = y;
        increment = 0.5;
        time = 6;
        points = 300;
        dx = 5;
        canMove = false;
    }
    
    public int getPoints() {return points;}
    public int getDX() {return dx;}
    public Rectangle getRect() {return new Rectangle(alienX, alienY, 50, 25);}
    
    public void setCanMove(boolean isCanMove) {canMove = isCanMove;}
    public void setX (int newX) {alienX = newX;}

    public void draw(Graphics g) {
        g.drawImage(alienPic, alienX, alienY, null);
    }

    //if the ship has shot 25 times, the alien appears from one side of the screen
    //then alternates where it shows up every 25 shots 
    //it plays a sound repeatedly while it is moving
    public void move(Ship ship, int shotsBeforeAppearance) {
        if (alienX > GPanel.WIDTH || alienX + 50 < 0) {
            alienX = (alienX + 50 < 0) ? -50 : GPanel.WIDTH;
            //alienX = (alienX > GPanel.WIDTH) ? GPanel.WIDTH : -50;
            ship.setShotsBeforeAlien(0);
            canMove = false;
            points = 300;
            dx *= -1;
        }
        if (shotsBeforeAppearance%25 == 0 && shotsBeforeAppearance!=0) {
            canMove = true;
        }
        if (canMove) {
            alienX += dx;
            if (increment < time) {
                increment += 0.5;
            }
            else {
                alienSounds[clip].play();
                increment = 0.5;
            }
        }
    }
}
