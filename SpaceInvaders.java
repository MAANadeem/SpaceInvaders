/**
 * SpaceInvaders.java
 * Muhammad Nadeem
 * Space Invaders Game - Assignment 4
 */

import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SpaceInvaders extends JFrame {
    private static final long serialVersionUID = 1L;
    GPanel game;

    public SpaceInvaders () {
        super("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        game = new GPanel();
        add(game);
        pack();

        setVisible(true);
        //setResizable(false);
    }


    public static void main(String[] args){
        SpaceInvaders frame = new SpaceInvaders();
    }

}

class GPanel extends JPanel implements ActionListener, KeyListener{
    private static final long serialVersionUID = 1L;

    private String screen;

    private Timer time;
    private Font retroType;                             //an arcade like font
    private boolean[] keys;                             //keys on the keyboard
    public static int WIDTH = 750, HEIGHT = 800;        //dimensions of the frame
    public static final int eCOLUMNS = 11, eROWS = 6;   //# of enemy columns and rows 

    private Ship ship;                                  //ship object
    private Bullet shipBullet;                          //the ship's bullet
    private ArrayList<Enemy> horde = new ArrayList<Enemy>();                    //all the enemies on the field
    private ArrayList<Bullet> enemyBullets = new ArrayList<Bullet>();           //enemies' bullets
    private ArrayList<Barrier> defenses = new ArrayList<Barrier>();             //shields in field
    private Alien alien;                                                        //alien ufo object
    private static int score;                                                   //score counter, increases as enemies are killed

    private Sound shipShotSound, enemyDeathSound;                               //sounds made when ship shoots and when enemy dies
    private Sound[] music = new Sound[4];                                       //background music

    private boolean gameOver = false;                                           //game over checker
    
    public GPanel() {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        addKeyListener(this);

        screen = "Intro";                                                       //sets beginning screen as the intro screen

        keys = new boolean[KeyEvent.KEY_LAST+1];
        int[] playerKeys = {KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_SPACE}; //all buttons player can use (left, right, spacebar)

        ship = new Ship(WIDTH/2-28, HEIGHT-110, playerKeys);
        shipBullet = new Bullet(ship.getX()+18,0);
        alien = new Alien(-60, 50);

        //adds enemies in a grid; depending on row, a different enemy is used; their bullets are initially set to null
        for (int i = 0; i < (eCOLUMNS*eROWS); i++) {
            horde.add(new Enemy(75+50*(i%eCOLUMNS), 120+50*(i/eCOLUMNS), Enemy.POS[i/(2*eCOLUMNS)], null));
        }

        //enemy bullets are put into an arraylist (maximum 11); enemies on the front line are given access to each bullet
        for (int i = 0; i < eCOLUMNS; i++) {
            enemyBullets.add(new Bullet((int)horde.get((eCOLUMNS*eROWS-1)-i).getX(),HEIGHT));
            horde.get((eCOLUMNS*eROWS-1)-i).setBullet(enemyBullets.get(i));
        }

        //each barrier is initialized
        for (int i = 0; i < 4; i++) {
            defenses.add(new Barrier(75 + 160*i, HEIGHT-250));
        }

        //the arcade-like font is initialized
        try {
            retroType = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/PressStart2P-Regular.ttf")).deriveFont(25f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(retroType);
        } catch (IOException | FontFormatException e) {}

        //each sound is initialized
        try {
            shipShotSound = new Sound("sounds/shoot.wav");
            enemyDeathSound = new Sound("sounds/invaderkilled.wav");
            for (int i = 0; i < 4; i++) {
                music[i] = new Sound("sounds/fastinvader"+(i+1)+".wav");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        //score is 0 at first
        score = 0;

        time = new Timer(20, this);
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void paint(Graphics g) {
        
        //makes background black
        super.paintComponent(g);
        setBackground(Color.BLACK);
        
        //intro screen
        if (screen == "Intro") {
            Image introScreen = new ImageIcon("images/space_invaders_introscreen.png").getImage();
            Image introScreenCommand = new ImageIcon("images/space_invaders_introscreen_command.png").getImage();
            g.drawImage(introScreen, 0,0, null);
            g.drawImage(introScreenCommand, 90,600, null);
        }

        //game over screen
        if (screen == "Game Over") {
            Image gameOverScreen = new ImageIcon("images/space_invaders_gameoverscreen.png").getImage();
            g.drawImage(gameOverScreen, 225,325, null);
        }

        //game screen
        if (screen == "Game") {
            //all things are drawn, if things are in an arraylist, for loops individually draw them
            //something != null is for when something doesn't exist in an arraylist, avoids nullpointerexception
            ship.draw(g);
            shipBullet.draw(g, ship);
            alien.draw(g);
            for (Barrier shield : defenses) {
                shield.draw(g);
            }
            
            for (Enemy en : horde) {
              if (en!=null) {
                en.draw(g);
              }
            }
            for (Bullet b : enemyBullets) {
              if (b!=null) {
                b.draw(g, enemyBullets);
              }
            }

            //shows score and lives
            g.setColor(Color.WHITE);
            g.setFont(retroType);
            g.drawString("SCORE:"+score, 0, HEIGHT);
            g.drawString("LIVES:"+ship.getLives(), WIDTH-175,HEIGHT);
        }
    }

    //moves each object as they need to
    public void move() {
        //plays music
        Sound.playMusic(music);
        //speeds up the music
        Sound.musicSpeedUp(horde);
        ship.move(keys);
        shipBullet.move(ship, keys, shipShotSound);
        alien.move(ship, ship.getShotsBeforeAlien());
        
        //if any of the enemies hit a wall, their direction changes, and they move down 
        boolean hitWall = false;
        for (Enemy en : horde) {
            if (en!=null) {
                Enemy.speedUp(horde);
                if (en.move()) {
                    hitWall = true;
                }
            }
        }
        if (hitWall) {
            Enemy.dx *= -1;
            Enemy.moveDown(horde);
        }

        //moves the enemy bullets
        for (Enemy en : horde) {
            if (en!=null && en.getBullet() != null) {
                en.getBullet().move(en);
            }
        }
    }

    //checks all possible collisions and performs an action accordingly
    public void collisionCheck() {
        //bullet - bullet collision
        for(Enemy en : horde) {
            if (en==null) {}
            else if (en.getBullet() != null && shipBullet.getRect(ship).intersects(en.getBullet().getRect(en))) {
                //sends bullets back to their original positions (off screen)
                en.getBullet().hit(en);
                shipBullet.hit(ship);
            }
        }

        //enemy - bullet collision
        for(Enemy en : horde) {
            if (en==null) {}
            else if (shipBullet.getRect(ship).intersects(en.getCollideRect())) {
                score += en.getPoints(); //add the points of the enemy killed to the score
                shipBullet.hit(ship);
                shift(en);
            }
        }

        //alien - bullet collision
        if (shipBullet.getRect(ship).intersects(alien.getRect())) {
            score += alien.getPoints();
            alien.alienSounds[1].play();
            //if the alien was going right and it is hit, it is sent to the far right, and vice versa
            int newAlienX = (alien.getDX() > 0) ? GPanel.WIDTH : -50;
            alien.setX(newAlienX);
            //the alien stops moving until the next 25 shots
            alien.setCanMove(false);
        }
        
        //bullet - ship collision
        for (Enemy en : horde) {
            if (en == null) {}
            else if (en.getBullet() != null && ship.getRect().intersects(en.getBullet().getRect(en))) {
                //decreases life of ship and sends enemy bullet back (off screen)
                ship.setLives(ship.getLives()-1);
                en.getBullet().hit(en);
            }
        }
        
        //ship bullet - barrier collision
        for (Barrier shield : defenses) {
            for (Rectangle r : shield.getAllRects()) {
                if (r == null){}
                else if (shipBullet.getRect(ship).intersects(r)) {
                    //a rectangle is removed wherever the ship's bullet hit
                    shield.getAllRects().set(shield.getAllRects().indexOf(r), null);
                    shipBullet.hit(ship);
                }
            }
        }

        //enemy bullet - barrier collision
        for (Barrier shield : defenses) {
            for (Enemy en : horde) {
                for (Rectangle r : shield.getAllRects()) {
                    if (en == null || en.getBullet() == null || r == null) {}
                    else if (en.getBullet().getRect(en).intersects(r)) {
                        shield.getAllRects().set(shield.getAllRects().indexOf(r), null);
                        en.getBullet().hit(en);
                    }
                }
            }
        }

        //enemy - barrier collision
        for (Barrier shield : defenses) {
            for (Enemy en : horde) {
                for (Rectangle r : shield.getAllRects()) {
                    if (en == null || en.getBullet() == null || r == null) {}
                    else if (en.getCollideRect().intersects(r)) {
                        //a barrier breaks down as an enemy passes through it
                        shield.getAllRects().set(shield.getAllRects().indexOf(r), null);
                    }
                }
            }
        }

        //enemy - ship collision
        for(Enemy en : horde) {
            if (en==null) {}
            else if (ship.getRect().intersects(en.getCollideRect())) {
                //if an enemy touches the ship, game over
                gameOver = true;
                //the game over screen is shown
                screen = "Game Over";
            }
        }
    }

    //kills an enemy and gives the access of their bullet to the enemy behind them
    //if there isn't anyone left, the bullet is removed
    public void shift (Enemy en) {
        if (horde.indexOf(en) <eCOLUMNS) {
            enemyBullets.remove(en.getBullet());
        }
        else {
            int target = horde.indexOf(en);
            for (int i = 0; i < 4; i++) {
                if (horde.get(target-eCOLUMNS) == null) {
                    target -= eCOLUMNS;
                }
                else {
                    horde.get(target-eCOLUMNS).setBullet(en.getBullet());
                    break;
                }
            }
        }
        //at the end, the enemy is set to null, and their death sound is played
        horde.set(horde.indexOf(en), null);
        enemyDeathSound.play();
    }

    //if lives runs out, the game is over
    public void isEndGame() {
        if (ship.getLives()==0) {
            gameOver = true;
            //the game over screen is shown
            screen = "Game Over";
        }
    }

    //main game loop
    public void actionPerformed(ActionEvent e) {
        //if the game ends, everything stops, and only the screen (showing "Game Over") is displayed
        if (!gameOver) {
            move();
            collisionCheck();
            isEndGame();
        }
        repaint();
    }

    //--------------------------------

    //if a key is pressed, the game begins and the timer starts
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        screen = "Game";
        time.start();
        keys[e.getKeyCode()] = true;
    }
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
}