/**
 * Enemy.java
 * Muhammad Nadeem
 * The enemies that appear on the field
 * Main antagonists of the game, move menacingly closer as time progresses
 * The group speeds up as allies fall
 */

import javax.swing.ImageIcon;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

class Enemy{

    private double enemyX, enemyY, increment;               //x and y coordinate of enemy
                                                            //increment increases until it reaches time, where an action occurs,
    private static double time;                             //emulating a delay in time
                                                            //time is static since it affects every enemy on screen
    public static int dx, dy;                               //speeds in the x and y directions, they are static as they affect all enemies
    public static boolean hitWall = false;                  //checks if any of the enemies on screen have hit the boundaries of the screen
    
    private int pointVal;                                   //each enemy type has a different point value
    private int frame;                                      //current frame of the animation

    public static final int TOP = 0, MID = 1, BOT = 2;
    public static final int[] POS = {TOP, MID, BOT};        //the enemy type depends on the row the enemy is on

    private Bullet laser;                                   //each enemy has a bullet

    private Image frame1, frame2;                           
    private Image[] enemyPics = new Image[2];               //frames for the enemy

    public Enemy(int x, int y, int type, Bullet b) {
        enemyX = x;
        enemyY = y;
        dx = 10;
        increment = 0.5;
        time = 15;
        dy = 35;
        laser = b;
        frame = 0;

        //sets the animation frames and point value of each enemy depending on which row they are on
        switch(type){
            case TOP:
                frame1 = new ImageIcon("images/space_invaders_enemy3_f1.png").getImage();
                frame2 = new ImageIcon("images/space_invaders_enemy3_f2.png").getImage();
                pointVal = 30;
                break;
            case MID:
                frame1 = new ImageIcon("images/space_invaders_enemy2_f1.png").getImage();
                frame2 = new ImageIcon("images/space_invaders_enemy2_f2.png").getImage();
                pointVal = 20;
                break;
            case BOT:
                frame1 = new ImageIcon("images/space_invaders_enemy1_f1.png").getImage();
                frame2 = new ImageIcon("images/space_invaders_enemy1_f2.png").getImage();
                pointVal = 10;
                break;
        }
        enemyPics[0] = frame1;
        enemyPics[1] = frame2;
    }

    public int getPoints() {return pointVal;}
    public double getX() {return enemyX;}
    public double getY() {return enemyY;}
    public void setY(double newY) {enemyY = newY;}

    //returns the rectangle enclosing the current frame
    public Rectangle getCollideRect() {return new Rectangle((int)enemyX,(int)enemyY,enemyPics[frame].getWidth(null),enemyPics[frame].getHeight(null));}
    public Bullet getBullet() {return laser;}
    public void setBullet(Bullet b) {laser = b;}
    
    public void draw(Graphics g) {
        g.drawImage(enemyPics[frame], (int)enemyX, (int)enemyY, null);
    }

    //---------------------------------

    //moves an enemy left and right
    public boolean move() {
        boolean hitWall = false;

        //emulates a delay
        if (increment < time) {
            increment += 0.5;
        }
        else {
            enemyX += dx;
            frame = (frame + 1)% enemyPics.length;          //changes the frame each time
            increment = 0.5;
            if (enemyX > GPanel.WIDTH-40 || enemyX < 0) {   //if an enemy hits one of the sides, hitWall becomes true
                hitWall = true;
            }
        }
        return hitWall;
    }

    //everytime the enemies, hit the wall, the entire group moves down
    public static void moveDown(ArrayList<Enemy> ens) {
        for (Enemy en : ens) {
            if (en != null) {
                en.setY(en.getY()+dy);
            }
        }
    }

    //purely probabilistically, canShoot returns true to allow for an enemy to shoot
    public boolean randomShot(Enemy enemy) {
        Random rand = new Random();
        double elapsedTime = 0;
        int time = rand.nextInt(400);
        boolean canShoot = false;

        if (elapsedTime < time) {
            elapsedTime += 1;
        }
        else {
            canShoot = true;
        }
        return canShoot;
    }

    //returns the number of enemies left on the field
    public static int enemiesRemaining(ArrayList<Enemy> ens) {
        int counter = 0;
        for (int i = 0; i < ens.size(); i ++) {
            if (ens.get(i) != null) {
                counter++;
            }
        } 
        return counter;
    }

    //sets the time for the delay to be lower the fewer enemies there are
    public static void speedUp(ArrayList<Enemy> ens) {
        if (enemiesRemaining(ens) == 1) {time = 1;}
        else if (enemiesRemaining(ens) <= 10) {time = 5;}
        else if (enemiesRemaining(ens) <= 25) {time = 10;}
        else if (enemiesRemaining(ens) <= 40) {time = 15;}
        else {time = 20;}
    }
}
