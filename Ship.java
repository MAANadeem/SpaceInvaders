/**
 * Ship.java
 * Muhammad Nadeem
 * The ship object - the protagonist of the game - can move left and right,
 * and can shoot a bullet to kill enemies
 */

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

class Ship{

    private int lives = 3;              //lives of ship
    private int shipX;                  //x coordinate of ship
    private int shipY;                  //y coordinate of ship
    private int dx;                     //speed and direction of ship
    private int right, left;            //right and left button
    private int shoot;                  //spacebar (launches bullet)
    private int shotsBeforeAlien;       //number of shots before the alien ufo appears

    //ship image
    private final Image spaceShip = new ImageIcon("images/space_invaders_spaceship.png").getImage();

    public Ship (int x, int y, int[] keys) {
        shipX = x;
        shipY = y;
        dx = 5;
        right = keys[0];
        left = keys[1];
        shoot = keys[2];
    }

    public int getLives() {return lives;}
    public int getX() {return shipX;}
    public int getY() {return shipY;}
    public int getShotsBeforeAlien() {return shotsBeforeAlien;}
    public void setShotsBeforeAlien(int newShots) {shotsBeforeAlien = newShots;}
    public void setLives(int lives) {this.lives = lives;}

    //returns the rectangle that encloses the ship
    public Rectangle getRect() {return new Rectangle(shipX, shipY, spaceShip.getWidth(null), spaceShip.getHeight(null));}

    public void draw(Graphics g) {
        g.drawImage(spaceShip, shipX,shipY, null);
    }

    //moves the ship left or right if the left or right button is pressed, respectively
    //sets left and right boundaries
    public void move(boolean[] keys) {
        if (keys[right] && shipX <= GPanel.WIDTH-52) {
            shipX += dx;
        }
        if (keys[left] && shipX >= 10) {
            shipX -= dx;
        }
    }

    //checks if the spacebar is pressed, thus shooting a bullet
    public boolean shotPressed(boolean[] keys) {
        return keys[shoot];
    }
}