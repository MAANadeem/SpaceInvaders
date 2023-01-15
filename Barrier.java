/**
 * Barrier.java
 * Muhammad Nadeem
 * The 4 barriers at the front of the ship protecting it from the enemy fire
 * Disintegrate from bullets shot by both the enemies and the ship
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

class Barrier {

    private int barrierX, barrierY;

    //the barrierd are made up of multiple rectangles (exactly 18 - 5x4 minus 2)
    private ArrayList<Rectangle> barrierRects = new ArrayList<Rectangle>();

    public Barrier (int x, int y) {
        barrierX = x;
        barrierY = y;
        //multiple rectangles are arranged as a grid, forming a large rectangle
        for (int i = 0; i < 20; i++) {
            barrierRects.add(new Rectangle(x + 16*(i%5), y + 16*(i/5), 16,16));
        }

        //2 rectangles in the corners are removed for style
        barrierRects.set(0,null);
        barrierRects.set(4,null);
    }

    public int getX() {return barrierX;}
    public int getY() {return barrierY;}
    public ArrayList<Rectangle> getAllRects() {return barrierRects;}

    public void draw(Graphics g) {
        for (Rectangle r : barrierRects) {
            if (r != null) {
                g.setColor(Color.GREEN);
                g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
            }
        }
    }
}
