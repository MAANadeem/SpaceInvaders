/**
 * Bullet.java
 * Muhammad Nadeem
 * Bullets fly across the screen from both enemies and the ship
 * As they hit objects, collisions occur (SpaceInvaders class)
 * Get sent back to their initial positions
 */

import java.awt.*;
import javax.swing.ImageIcon;
import java.util.ArrayList;

class Bullet {
    private int bulletX,bulletY, width,height, dy;      //x and y coordinate, width and height, and the speed of the bullet
    private Image[] enemyBullet = new Image[2];         //if an enemy is shooting, an animation of 2 frames is shown
    private int frame;                                  //the current frame of the animation

    public Bullet(int x, int y) {
        bulletX = x;
        bulletY = y;
        width = 3;
        height = 10;
        dy = -3;
        enemyBullet[0] = new ImageIcon("images/space_invaders_enemybullet_f1.png").getImage();
        enemyBullet[1] = new ImageIcon("images/space_invaders_enemybullet_f2.png").getImage();
        frame = 0;
    }

    //returns rectangle enclosing the ship's bullet
    public Rectangle getRect(Ship ship) {return new Rectangle(bulletX,bulletY,width,height);}
    //returns a rectangle that encloses the enemy bullet larger than the bullet itself
    public Rectangle getRect(Enemy enemy) {return new Rectangle(bulletX-2, bulletY-2,enemyBullet[frame].getWidth(null)+4,enemyBullet[frame].getWidth(null)+4);}
    public int getX() {return bulletX;}
    public int getY() {return bulletY;}
    
    public void draw(Graphics g, Ship ship) {
        g.setColor(Color.white);
        g.fillRect(bulletX,bulletY,width,height);
    }

    //animates the enemy bullet
    public void draw(Graphics g, ArrayList<Bullet> bullets) {
        g.drawImage(enemyBullet[(int)frame], bulletX,bulletY, null);
        frame = (frame + 1) % enemyBullet.length;
    }

    //ship bullet is always moving, but when the spacebar is called and the bullet is off screen,
    //the bullet returns to the ship, emulating a bullet firing
    //the sound of the bullet firing is also played
    public void move(Ship ship, boolean[] keys, Sound sound) {
        bulletY += (dy-17);
        if ((ship.shotPressed(keys) && bulletY<0)) {
            sound.play();                                       
            ship.setShotsBeforeAlien(ship.getShotsBeforeAlien()+1); //every shot increases the counter before the alien ufo appears
            bulletX = ship.getX()+18;
            bulletY = ship.getY();
        }
    }

    //same as above with enemy bullets
    //randomShot determines if an enemy should or should not shoot depending on random chance
    public void move(Enemy enemy) {
        bulletY -= dy;
        if ((bulletY>GPanel.HEIGHT && enemy.randomShot(enemy))) {
            bulletX = (int)enemy.getX()+18;
            bulletY = (int)enemy.getY();
        }
    }

    //if the ship's bullet hits something, it is sent off screen
    public void hit(Ship ship) {
        bulletX = ship.getX()+18;
        bulletY = 0-height;
    }
    
    //if an enemy's bullet hits something, it is sent off screen
    public void hit(Enemy enemy) {
        bulletX = (int)enemy.getX()+18;
        bulletY = GPanel.HEIGHT;
    }
}
