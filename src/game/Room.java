package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import resources.SettingsProperties;

public class Room implements Serializable {

    int width = 17;
    int height = 13;
    int xNum;
    int yNum;

    public int numEnemies = UsefulSnippets.generateRandomNumber(3) + 2;
    public ArrayList<Entity> en_arry = new ArrayList();

    int[] tiles = new int[width * height];
    int[] spawnTiles = new int[width * height];

    int [] trapDoorX = {7, 9, 1, 15, 1, 15, 7, 9};
    int [] trapDoorY = {1, 1, 5, 5, 7, 7, 11, 11};
    int tdx;
    int tdy;

    ArrayList<Integer> spawnCoordsX = new ArrayList();
    ArrayList<Integer> spawnCoordsY = new ArrayList();
    FloorTile[] tileArry = new FloorTile[width * height];
    int[] liquidTiles = {4, 6};  //Tiles that are special that need the connecting textures, i.e. water, lava
    int[] groundTiles = {2, 5};
    GameEngine world;
    SettingsProperties props = new SettingsProperties();

    transient BufferedImage lvl;
    transient BufferedImage spawnMap;
    String lvlPath;
    String spawnMapPath;
    int drawCycle = 0;

    Random gen = new Random();

    public Room(GameEngine ge, String levelImage, String spawnImage, int x, int y) {
        world = ge;
        for (int i = 0; i < tileArry.length; i++) {
            tileArry[i] = new FloorTile(1);
        }
        lvl = UsefulSnippets.loadImage(levelImage);
        spawnMap = UsefulSnippets.loadImage(spawnImage);
        lvlPath = levelImage;
        spawnMapPath = spawnImage;
        this.xNum = x;
        this.yNum = y;
        this.numEnemies = gen.nextInt(3) + 2;
        loadLevel();
        for (int l = 0; l < numEnemies; l++) {
            int spr = UsefulSnippets.generateRandomNumber(3);
            int spawnInd = UsefulSnippets.generateRandomNumber(spawnCoordsX.size());
            en_arry.add(new Enemy(world, world.spritePaths[spr], spawnCoordsX.get(spawnInd), spawnCoordsY.get(spawnInd)));
        }
    }

    public final void loadLevel() {
        lvl.getRGB(0, 0, width, height, tiles, 0, width);
        spawnMap.getRGB(0, 0, width, height, spawnTiles, 0, width);
        for (int i = 0; i < lvl.getWidth(); i++) {
            for (int j = 0; j < lvl.getHeight(); j++) {

                if (tiles[i + j * width] == 0xFFFFFFFF) {
                    tileArry[i + j * width].setTile(1); //Wall
                }
                if (tiles[i + j * width] == 0xFF000000) {
                    tileArry[i + j * width].setTile(2); //Floor
                    tileArry[i + j * width].metaElement = UsefulSnippets.generateRandomNumber(3);
                }
                if (tiles[i + j * width] == 0xFF532F00) {
                    tileArry[i + j * width].setTile(3); //Door
                }
                if (tiles[i + j * width] == 0xFF0000ff) {
                    tileArry[i + j * width].setTile(4); //Water
                }
                if (tiles[i + j * width] == 0xFF03a5ff) {
                    tileArry[i + j * width].setTile(5); //Ice
                }
                if (tiles[i + j * width] == 0xFFfc1604) {
                    tileArry[i + j * width].setTile(6); //Lava
                }
                if (tiles[i + j * width] == 0xFF073a08) {
                    tileArry[i + j * width].setTile(7); //Moss
                }
                if (tiles[i + j * width] == 0xFF007f00) {
                    tileArry[i + j * width].setTile(8); //Grass
                }
                if (tiles[i + j * width] == 0xFF6b3d00) {
                    tileArry[i + j * width].setTile(9); //Rock
                }
                if (spawnTiles[i + j * width] == 0xFFff0096) {
                    tileArry[i + j * width].isSpawn = true; //Spawn
                    spawnCoordsX.add(i * 50);
                    spawnCoordsY.add(j * 50);
                }
            }
        }

        if (xNum == world.rooms.length - 1 && yNum == world.rooms[0].length - 1) {
            int rng = UsefulSnippets.generateRandomNumber(8);
            tdx = trapDoorX[rng];
            tdy = trapDoorY[rng];
            tileArry[trapDoorX[rng] + trapDoorY[rng] * width].setTile(10);
        }
//
//        System.out.println(tileArry[5 + 5 * width].TILE_ID);
        for (int v = 0; v < groundTiles.length; v++) {
            for (int i = 1; i < lvl.getWidth() - 1; i++) {
                for (int j = 1; j < lvl.getHeight() - 1; j++) {
                    if (tileArry[i + j * width].TILE_ID == groundTiles[v]) {
                        for (int f = 0; f < liquidTiles.length; f++) {
                            if (tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f]) {  //up
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 1;
                                tileArry[i + j * width].metaDir = 0;
                            }
                            if (tileArry[(i + 1) + j * width].TILE_ID == liquidTiles[f]) {  //right
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 1;
                                tileArry[i + j * width].metaDir = 1;
                            }
                            if (tileArry[i + (j + 1) * width].TILE_ID == liquidTiles[f]) {  //down
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 1;
                                tileArry[i + j * width].metaDir = 2;
                            }
                            if (tileArry[(i - 1) + j * width].TILE_ID == liquidTiles[f]) {  //left
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 1;
                                tileArry[i + j * width].metaDir = 3;
                            }
                            //Corners
                            if (tileArry[(i - 1) + j * width].TILE_ID == liquidTiles[f] && tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f]) {  //up + left
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 2;
                                tileArry[i + j * width].metaDir = 0;
                            }
                            if (tileArry[(i + 1) + j * width].TILE_ID == liquidTiles[f] && tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f]) {  //up + right
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 2;
                                tileArry[i + j * width].metaDir = 1;
                            }
                            if (tileArry[(i + 1) + j * width].TILE_ID == liquidTiles[f] && tileArry[i + (j + 1) * width].TILE_ID == liquidTiles[f]) {  //down + right
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 2;
                                tileArry[i + j * width].metaDir = 2;
                            }
                            if (tileArry[(i - 1) + j * width].TILE_ID == liquidTiles[f] && tileArry[i + (j + 1) * width].TILE_ID == liquidTiles[f]) {  //down + left
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 2;
                                tileArry[i + j * width].metaDir = 3;
                            }

                            //Opposites
                            if (tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f] && tileArry[i + (j + 1) * width].TILE_ID == liquidTiles[f]) {  //up/down
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 3;
                                tileArry[i + j * width].metaDir = 1;
                            }
                            if (tileArry[(i - 1) + j * width].TILE_ID == liquidTiles[f] && tileArry[(i + 1) + j * width].TILE_ID == liquidTiles[f]) {  //left/right
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 3;
                                tileArry[i + j * width].metaDir = 0;
                            }

                            //3 Sides (dead end)
                            if (tileArry[(i - 1) + j * width].TILE_ID == liquidTiles[f] && tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f] && tileArry[(i + 1) + j * width].TILE_ID == liquidTiles[f]) {  //n
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 4;
                                tileArry[i + j * width].metaDir = 0;
                            }
                            if (tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f] && tileArry[i + (j + 1) * width].TILE_ID == liquidTiles[f] && tileArry[(i + 1) + j * width].TILE_ID == liquidTiles[f]) {  //backwards "C"
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 4;
                                tileArry[i + j * width].metaDir = 1;
                            }
                            if (tileArry[(i - 1) + j * width].TILE_ID == liquidTiles[f] && tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f] && tileArry[(i + 1) + j * width].TILE_ID == liquidTiles[f]) {  //U
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 4;
                                tileArry[i + j * width].metaDir = 2;
                            }
                            if (tileArry[i + (j - 1) * width].TILE_ID == liquidTiles[f] && tileArry[i + (j + 1) * width].TILE_ID == liquidTiles[f] && tileArry[(i - 1) + j * width].TILE_ID == liquidTiles[f]) {  //C
                                tileArry[i + j * width].metaElement = 1 + f;
                                tileArry[i + j * width].metaType = 4;
                                tileArry[i + j * width].metaDir = 3;
                            }
                        }
                    }
                }
            }
        }

        if (this.yNum == 0) {
            tileArry[7].TILE_ID = 1;
            tileArry[8].TILE_ID = 1;
            tileArry[9].TILE_ID = 1;
        }
        if (this.xNum == 0) {
            tileArry[85].TILE_ID = 1;
            tileArry[102].TILE_ID = 1;
            tileArry[119].TILE_ID = 1;
        }
        if (this.xNum == world.rooms[0].length - 1) {
            tileArry[101].TILE_ID = 1;
            tileArry[118].TILE_ID = 1;
            tileArry[135].TILE_ID = 1;
        }
        if (this.yNum == world.rooms.length - 1) {
            tileArry[211].TILE_ID = 1;
            tileArry[212].TILE_ID = 1;
            tileArry[213].TILE_ID = 1;
        }
    }

    public void draw(Graphics g) {
        //You are entering switch hell
        for (int i = 0; i < lvl.getWidth(); i++) {
            for (int j = 0; j < lvl.getHeight(); j++) {
//                g.setColor(tileArry[i + j * width].getColor());
//                g.fill3DRect(i * 50, j * 50, 50, 50, true);
                switch (tileArry[i + j * width].TILE_ID) {
                    case 0:
                        g.drawImage(world.spritesTex[0][0], i * 50, j * 50, null); //Test tile
                        break;
                    case 1:
                        g.drawImage(world.spritesTex[tileArry[i + j * width].metaElement][1], i * 50, j * 50, null);  //Wall tile
                        break;
                    case 2:
                        if (tileArry[i + j * width].metaDir == -1) {
                            g.drawImage(world.spritesTex[tileArry[i + j * width].metaElement][2], i * 50, j * 50, null);
                            drawEdgeShadows(g, i, j, 2);
                        } else {
                            switch (tileArry[i + j * width].metaElement) {
                                case 1:
                                    g.drawImage(world.spritesTex[0][4], i * 50, j * 50, null);
                                    break;
                                case 2:
                                    g.drawImage(world.spritesTex[0][6], i * 50, j * 50, null);
                                    break;
                            }
                            drawEdgedTiles(g, i, j, tileArry[i + j * width].metaType, tileArry[i + j * width].metaDir, 2);
                            drawEdgeShadows(g, i, j, 2);
                        }
                        break;
                    case 3:
                        g.drawImage(world.spritesTex[tileArry[i + j * width].metaElement][3], i * 50, j * 50, null);  //Door tile
                        break;
                    case 4:
                        drawCycle++;
                        if (drawCycle > 30) {
                            tileArry[i + j * width].metaAnim = UsefulSnippets.generateRandomNumber(3);
                            drawCycle = 0;
                            g.drawImage(world.spritesTex[tileArry[i + j * width].metaAnim][4], i * 50, j * 50, null);
                        } else {
                            g.drawImage(world.spritesTex[tileArry[i + j * width].metaAnim][4], i * 50, j * 50, null);
                        }
                        break;
                    case 5:
                        if (tileArry[i + j * width].metaDir == -1) {
                            g.drawImage(world.spritesTex[tileArry[i + j * width].metaElement][5], i * 50, j * 50, null);
                            drawEdgeShadows(g, i, j, 5);
                        } else {
                            switch (tileArry[i + j * width].metaElement) {
                                case 1:
                                    g.drawImage(world.spritesTex[0][4], i * 50, j * 50, null);
                                    break;
                                case 2:
                                    g.drawImage(world.spritesTex[0][6], i * 50, j * 50, null);
                                    break;
                            }
                            drawEdgedTiles(g, i, j, tileArry[i + j * width].metaType, tileArry[i + j * width].metaDir, 5);
                            drawEdgeShadows(g, i, j, 5);
                        }
                        break;
                    case 6:
                        drawCycle++;
                        if (drawCycle > 30) {
                            tileArry[i + j * width].metaAnim = UsefulSnippets.generateRandomNumber(3);
                            drawCycle = 0;
                            g.drawImage(world.spritesTex[tileArry[i + j * width].metaAnim][6], i * 50, j * 50, null);
                        } else {
                            g.drawImage(world.spritesTex[tileArry[i + j * width].metaAnim][6], i * 50, j * 50, null);
                        }
                        break;
                    case 7:
                        g.drawImage(world.spritesTex[tileArry[i + j * width].metaElement][7], i * 50, j * 50, null);
                        break;
                    case 8:
                        g.drawImage(world.spritesTex[tileArry[i + j * width].metaElement][8], i * 50, j * 50, null);
                        break;
                    case 9:
                        switch (world.stratum) {
                            case 1:
                                g.drawImage(world.spritesTex[1][2], i * 50, j * 50, null);
                                break;
                            case 2:
                                g.drawImage(world.spritesTex[0][5], i * 50, j * 50, null);
                                break;
                            case 3:
                                g.drawImage(world.spritesTex[1][2], i * 50, j * 50, null);
                                break;
                        }
                        g.drawImage(world.spritesTex[2][0], i * 50, j * 50, null);
                        g.drawImage(world.spritesTex[0][9], i * 50, j * 50, null);
                        break;
                    case 10:
                        g.drawImage(world.spritesTex[tileArry[(i + j * width) + 1].metaElement][tileArry[(i + j * width) + 1].TILE_ID], i * 50, j * 50, null);
                        g.drawImage(world.spritesTex[0][11], i * 50, j * 50, null);
                        break;
                }

                if (tileArry[i + j * width].isSpawn) {
                    g.drawImage(world.spritesTex[0][10], i * 50, j * 50, null);
                }

//                if (SettingsProperties.debugModeG == true) {
//                    g.setColor(Color.yellow);
//                    g.fill3DRect((world.pl.tileLocX) * 50, (world.pl.tileLocY) * 50, 50, 50, true);
//                    g.setColor(new Color(0, 255, 0, 7));
//                    g.fill3DRect((world.pl.tileLocX + 1) * 50, (world.pl.tileLocY) * 50, 50, 50, true);
//                    g.fill3DRect((world.pl.tileLocX) * 50, (world.pl.tileLocY + 1) * 50, 50, 50, true);
//                    g.fill3DRect((world.pl.tileLocX - 1) * 50, (world.pl.tileLocY) * 50, 50, 50, true);
//                    g.fill3DRect((world.pl.tileLocX) * 50, (world.pl.tileLocY - 1) * 50, 50, 50, true);
//                    
//                    g.setColor(new Color(255, 0, 0, 7));
//                    if (tileArry[(world.pl.tileLocX + 1) + (world.pl.tileLocY) * width].isSolid()) {
//                        g.fill3DRect((world.pl.tileLocX + 1) * 50, (world.pl.tileLocY) * 50, 50, 50, true);
//                    }
//                    if (tileArry[(world.pl.tileLocX) + (world.pl.tileLocY + 1) * width].isSolid()) {
//                        g.fill3DRect((world.pl.tileLocX) * 50, (world.pl.tileLocY + 1) * 50, 50, 50, true);
//                    }
//                    if (tileArry[(world.pl.tileLocX - 1) + (world.pl.tileLocY) * width].isSolid()) {
//                        g.fill3DRect((world.pl.tileLocX - 1) * 50, (world.pl.tileLocY) * 50, 50, 50, true);
//                    }
//                    if (tileArry[(world.pl.tileLocX) + (world.pl.tileLocY - 1) * width].isSolid()) {
//                        g.fill3DRect((world.pl.tileLocX) * 50, (world.pl.tileLocY - 1) * 50, 50, 50, true);
//                    }
//                    g.setColor(Color.white);
//                }
            }
        }
    }

    public void drawEdgedTiles(Graphics g, int x, int y, int metaType, int metaDir, int material) {
        switch (metaType) {
            case 1:
                switch (metaDir) {
                    case 0:
                        g.drawImage(world.spritesTex[6][material], x * 50, y * 50, null);
                        break;
                    case 1:
                        g.drawImage(world.spritesTex[8][material], x * 50, y * 50, null);
                        break;
                    case 2:
                        g.drawImage(world.spritesTex[9][material], x * 50, y * 50, null);
                        break;
                    case 3:
                        g.drawImage(world.spritesTex[7][material], x * 50, y * 50, null);
                        break;
                }
                break;
            case 2:
                switch (metaDir) {
                    case 0:
                        g.drawImage(world.spritesTex[10][material], x * 50, y * 50, null);
                        break;
                    case 1:
                        g.drawImage(world.spritesTex[11][material], x * 50, y * 50, null);
                        break;
                    case 2:
                        g.drawImage(world.spritesTex[12][material], x * 50, y * 50, null);
                        break;
                    case 3:
                        g.drawImage(world.spritesTex[3][material], x * 50, y * 50, null);
                        break;
                }
                break;
            case 3:
                g.drawImage(world.spritesTex[5 - metaDir][material], x * 50, y * 50, null);
                break;
            case 4:
                g.drawImage(world.spritesTex[13 + metaDir][material], x * 50, y * 50, null);
                break;
        }
    }

    public void drawEdgeShadows(Graphics g, int x, int y, int material) {
        if (tileArry[x + (y - 1) * width].TILE_ID != material && tileArry[x + (y - 1) * width].TILE_ID != 4 && tileArry[x + (y - 1) * width].TILE_ID != 6) {
            g.drawImage(world.spritesTex[3][0], x * 50, y * 50, null);
        }
        if (tileArry[(x + 1) + y * width].TILE_ID != material && tileArry[(x + 1) + y * width].TILE_ID != 4 && tileArry[(x + 1) + y * width].TILE_ID != 6) {
            g.drawImage(world.spritesTex[4][0], x * 50, y * 50, null);
        }
        if (tileArry[x + (y + 1) * width].TILE_ID != material && tileArry[x + (y + 1) * width].TILE_ID != 4 && tileArry[x + (y + 1) * width].TILE_ID != 6) {
            g.drawImage(world.spritesTex[5][0], x * 50, y * 50, null);
        }
        if (tileArry[(x - 1) + y * width].TILE_ID != material && tileArry[(x - 1) + y * width].TILE_ID != 4 && tileArry[(x - 1) + y * width].TILE_ID != 6) {
            g.drawImage(world.spritesTex[6][0], x * 50, y * 50, null);
        }
    }

    public void loadResources() {
        lvl = UsefulSnippets.loadImage(lvlPath);
        spawnMap = UsefulSnippets.loadImage(spawnMapPath);
    }
}
