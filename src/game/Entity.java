package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import game.Input.Keyboard;
import java.awt.Rectangle;
import java.io.Serializable;

public class Entity implements Serializable {

    GameEngine ge;

    double xLoc = 0;
    double yLoc = 0;
    double xLocFeet = this.xLoc + 32;
    double yLocFeet = this.yLoc + 64;

    boolean uBlock, rBlock, dBlock, lBlock = false; //Whether or not adjacent block is solid

    int dTu = 99;
    int dTr = 99;
    int dTd = 99;
    int dTl = 99; //Distance from the player's feet to the closest edge of a solid block (Distance to Up etc...)

    int collMinDist = 10;

    int tileLocX;
    int tileLocY;
    int orientation = 2; //0 - North/Up, 1 - East/Right, 2 - South, 3/Down - West/Left
    int[] animSeq = {0, 1, 2, 1};
    double spd = 0;
    boolean isMoving = false;

    int rows = 4;
    int columns = 3;
    int height = 64;
    int width = 64;
    int animCycle = 1;
    transient BufferedImage spriteSheetB;
    transient BufferedImage[][] sprites;
    Random rand = new Random();
    String spritePath;

    Keyboard keys;

    public Entity(GameEngine world, String sp) {
        ge = world;
        spritePath = sp;
        loadResources(spritePath);
        this.xLoc = rand.nextInt(400) + 50;
        this.yLoc = rand.nextInt(300) + 50;
    }

    public void draw(Graphics2D g) {
        g.drawImage(ge.spritesTex[1][0], (int) xLoc + 10, (int) yLoc + 25, null);
        g.drawImage(sprites[orientation][animSeq[animCycle]], (int) xLoc, (int) yLoc, null);

    }

    public void checkCollision() {
//        System.out.println(" " + dTu + " " + dTd + " " + dTr + " " + dTl);
        if ((this.tileLocX != 0) && (this.tileLocY != 0)) {
            uBlock = false;
            rBlock = false;
            dBlock = false;
            lBlock = false;

            dTu = 99;
            dTr = 99;
            dTd = 99;
            dTl = 99;

            if ((ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[(this.tileLocX + 1) + this.tileLocY * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isSolid())
                    || (ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[(this.tileLocX + 1) + this.tileLocY * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isDoor())) {
                this.rBlock = true;
                dTr = ((50 * (this.tileLocX + 1))) - (int) this.xLocFeet;
            }   //Right

            if ((ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[(this.tileLocX - 1) + this.tileLocY * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isSolid())
                    || (ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[(this.tileLocX - 1) + this.tileLocY * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isDoor())) {
                this.lBlock = true;
                dTl = ((int) this.xLocFeet - (50 * (this.tileLocX)));
            }   //Left

            if ((ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[this.tileLocX + (this.tileLocY + 1) * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isSolid())
                    || (ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[this.tileLocX + (this.tileLocY + 1) * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isDoor())) {
                this.dBlock = true;
                dTd = ((50 * (this.tileLocY + 1))) - (int) this.yLocFeet;
            }   //Up

            if ((ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[this.tileLocX + (this.tileLocY - 1) * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isSolid())
                    || (ge.rooms[ge.currentRoomX][ge.currentRoomY].tileArry[this.tileLocX + (this.tileLocY - 1) * ge.rooms[ge.currentRoomX][ge.currentRoomY].width].isDoor())) {
                this.uBlock = true;
                dTu = ((int) this.yLocFeet - (50 * (this.tileLocY)));
            }   //Down
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) this.xLoc + 10, (int) this.yLoc + 10, (int) width - 20, (int) height - 20);
    }

    public void tick() {
        if (!ge.frozen) {
            checkCollision();
        }

        if (isMoving && spd < 3) {
            spd = spd + 0.5;
        }

        if (!isMoving && spd > 0) {
            spd = spd - 0.5;
            animCycle = 1;
        }

        if (animCycle > 2) {
            animCycle = 0;
        }

        switch (orientation) {
            case 0:
                if (!uBlock && dTu > 3) {
                    setLocation(this.getX(), this.getY() - spd);
                }
                break;
            case 1:
                if (!rBlock && dTr > 3) {
                    setLocation(this.getX() + spd, this.getY());
                }
                break;
            case 2:
                if (!dBlock && dTd > 3) {
                    setLocation(this.getX(), this.getY() + spd);
                }
                break;
            case 3:
                if (!lBlock && dTl > 3) {
                    setLocation(this.getX() - spd, this.getY());
                }
                break;

        }
        tileLocX = (int) (xLocFeet) / 50;
        tileLocY = (int) (yLocFeet) / 50;
    }

    public void setLocation(double x, double y) {
        this.xLoc = x;
        this.yLoc = y;
        xLocFeet = this.xLoc + 32;
        yLocFeet = this.yLoc + 64;
        tileLocX = (int) (xLocFeet) / 50;
        tileLocY = (int) (yLocFeet) / 50;
    }

    public double getX() {
        return xLoc;
    }

    public double getY() {
        return yLoc;
    }

    public void loadResources(String sp) {
        sprites = new BufferedImage[rows][columns];
        spriteSheetB = UsefulSnippets.loadImage(spritePath);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sprites[i][j] = spriteSheetB.getSubimage(j * width, i * height, width, height);
            }
        }
    }
}
