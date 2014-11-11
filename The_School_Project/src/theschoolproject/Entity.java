/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theschoolproject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
//import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import theschoolproject.Input.Keyboard;

/**
 *
 * @author root
 */
public class Entity {

    double xLoc = 0;
    double yLoc = 0;
    int orientation = 2; //0 - North, 1 - East, 2 - South, 3 - West
    int mode = 0; // 0 - random walk, 1 - player targeting
    int[] animSeq = {0, 1, 2, 1};
    double spd = 0;
    boolean isMoving = false;

    int rows = 4;
    int columns = 3;
    int height = 64;
    int width = 64;
    int animCycle = 1;
    BufferedImage spriteSheetB;
    BufferedImage[][] sprites;
    String [] spritePaths = {"/resources/pl_sprite.png", "/resources/en1_sprite.png"};
    Random rand = new Random();
    
    Keyboard keys;

    public Entity(String sp) {
        sprites = new BufferedImage[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                spriteSheetB = UsefulSnippets.loadImage(sp);
                sprites[i][j] = spriteSheetB.getSubimage(j * width, i * height, width, height);
            }
        }
        this.xLoc = rand.nextInt(400)+50;
        this.yLoc = rand.nextInt(300)+50;
    }

    public void draw(Graphics g) {
        g.drawImage(sprites[orientation][animSeq[animCycle]], (int) xLoc, (int) yLoc, null);
    }

    public void tick() {
        

        if (isMoving && spd < 3) {
            spd = spd + 0.5;
        }

        if (!isMoving && spd > 0) {
            spd = spd - 0.5;
            animCycle = 1;
        }
        
        if (animCycle > 2){
            animCycle = 0;
        }
        
        switch (orientation) {
            case 0:
                if (this.yLoc > 50) {
                    setLocation(this.getX(), this.getY() - spd);
                }
                break;
            case 1:
                if (this.xLoc < 750) {
                    setLocation(this.getX() + spd, this.getY());
                }
                break;
            case 2:
                if (this.yLoc < 550) {
                    setLocation(this.getX(), this.getY() + spd);
                }
                break;
            case 3:
                if (this.xLoc > 50) {
                    setLocation(this.getX() - spd, this.getY());
                }
                break;
        }
    }

    public void setLocation(double x, double y) {
        this.xLoc = x;
        this.yLoc = y;
    }

    public double getX() {
        return xLoc;
    }

    public double getY() {
        return yLoc;
    }
}
