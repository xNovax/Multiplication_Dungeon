/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theschoolproject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import static java.lang.Math.abs;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import theschoolproject.Input.Keyboard;
import theschoolproject.Input.Mouse;

public class GamePanel extends JPanel {

    Random rand = new Random();
    ListenerThread lt = new ListenerThread();
    Thread th = new Thread(lt);
    FloorTile[][] ft = new FloorTile[17][15];

    //=========================
    //   Game State Variables
    //=========================
    boolean mainMenu = true;
    boolean gameScreen = false;
    boolean battle = false;

    //=========================
    //      Input Variables
    //=========================
    Keyboard keys = new Keyboard();
    Mouse mouse = new Mouse();

    //=========================
    //    Player Variables
    //=========================
    Player pl;
    ArrayList<Entity> en_arry = new ArrayList();
    int numEnemies = 5;

    //=========================
    //    Question Variables
    //=========================
    QuestionTimer qt;

    //=========================
    //      Menu Variables
    //=========================
    BufferedImage menuScreen;
    BufferedImage menuTitle;
    BufferedImage play_NoGlow;
    BufferedImage play_Glow;
    int AnimationTimer = 0;
    int ImageScroll = 0;
    boolean glow = false;
    
    //=========================
    //      Room Variables
    //=========================
    Room[] rooms = new Room[64];
    int CurrentRoom = 0;

    public GamePanel() {
        for (int l = 0; l < numEnemies; l++) {
            en_arry.add(new Enemy(this, "/resources/en1_sprite.png"));
        }
        this.addKeyListener(keys);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        pl = new Player(this, "/resources/pl_sprite.png", keys);
        th.start();
        for (int w = 0; w < 17; w++) {
            for (int h = 0; h < 15; h++) {
                if (w == 0 || w == 16 || h == 0 || h == 12) {
                    ft[w][h] = new FloorTile(1);
                } else {
                    ft[w][h] = new FloorTile(0);
                }
            }
        }
        this.setFocusable(true);
        menuScreen = UsefulSnippets.loadImage("/resources/JustBG.png");
        menuTitle = UsefulSnippets.loadImage("/resources/MenuTitle.png");
        play_NoGlow = UsefulSnippets.loadImage("/resources/Play_NoGlow.png");
        play_Glow = UsefulSnippets.loadImage("/resources/Play_WithGlow.png");
        rooms[0] = new Room("/resources/Levels/Level_01.png");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mainMenu) {
            g.drawImage(menuScreen, 0 - ImageScroll, 0, 850, 650, null);
            g.drawImage(menuScreen, 850 - ImageScroll, 0, 850, 650, null);
            g.drawImage(menuTitle, 0, 0, null);
            if (!glow) {
                g.drawImage(play_NoGlow, 0, 0, null);
            } else {
                g.drawImage(play_Glow, 0, 0, null);
            }
        }
        if (gameScreen) {
            /*
            for (int w = 0; w < 17; w++) {
                for (int h = 0; h < 15; h++) {
                    g.setColor(ft[w][h].getColor());
                    g.fill3DRect(w * 50, h * 50, 50, 50, true);
                }
            }
            */
            rooms[CurrentRoom].draw(g);
            
            for (int i = 0; i < mouse.Xcoords.size() - 1; i++) {
                int x = (int) mouse.Xcoords.get(i);
                int y = (int) mouse.Ycoords.get(i);
                int x1 = (int) mouse.Xcoords.get(i + 1);
                int y1 = (int) mouse.Ycoords.get(i + 1);
                g.drawLine(x, y, x1, y1);
            }
            g.drawLine(mouse.x1, mouse.y1, mouse.x2, mouse.y2);
            int dx = mouse.x2 - mouse.x1;
            int dy = mouse.y2 - mouse.y1;

            g.setColor(Color.red);

            mouse.x1 = (int) pl.xLoc + 32;
            mouse.y1 = (int) pl.yLoc + 32;

            if (dx > 0) {    //R
                if (dy > 0) {
                    if (abs(dx) < abs(dy)) {
                        g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                        g.drawString("quad_3_D", 50, 50);
                    } else {
                        g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                        g.drawString("quad_3_R", 50, 50);
                    }

                } else {
                    if (abs(dx) < abs(dy)) {
                        g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                        g.drawString("quad_0_U", 50, 50);
                    } else {
                        g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                        g.drawString("quad_0_R", 50, 50);
                    }

                }
            } else //L
            if (dy > 0) {
                if (abs(dx) < abs(dy)) {
                    g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                    g.drawString("quad_2_D", 50, 50);
                } else {
                    g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                    g.drawString("quad_2_L", 50, 50);
                }

            } else {
                if (abs(dx) < abs(dy)) {
                    g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                    g.drawString("quad_1_U", 50, 50);
                } else {
                    g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                    g.drawString("quad_1_L", 50, 50);
                }
            }
            g.drawString("pl_pos: " + pl.xLoc + ", " + pl.yLoc, 50, 60);

            for (int i = 0; i < numEnemies; i++) {
                en_arry.get(i).draw(g);
                g.setColor(Color.GREEN);
                g.drawLine((int) en_arry.get(i).xLoc + 32, (int) en_arry.get(i).yLoc + 32,
                        (int) pl.xLoc + 32, (int) pl.yLoc + 32);
            }
            pl.draw(g);
        }
        if (battle) {
            qt.draw(g);
        }
    }

    public void tick() {
        if (mainMenu) {
            if (AnimationTimer > 5) {
                ImageScroll++;
                if (ImageScroll >= 850) {
                    ImageScroll = 0;
                }
                AnimationTimer = 0;
            } else {
                AnimationTimer++;
            }

            if ((mouse.getX() > 350 && mouse.getX() < 505) && (mouse.getY() > 335 && mouse.getY() < 395)) {
                glow = true;
                if (mouse.isMousePressed()) {
                    switchTo("game");
                    mouse.unPress();
                }
            } else {
                glow = false;
            }
        }
        if (gameScreen) {
            pl.tick();
            for (int i = 0; i < en_arry.size(); i++) {
                en_arry.get(i).tick();
            }
        }

        if (battle) {
            qt.tick();
        }
    }

    /*
     Switchs the gamemode to the desired gamemode
     */
    public void switchTo(String mode) {
        if (mode.equals("menu")) {
            this.mainMenu = true;
            this.gameScreen = false;
            this.battle = false;
        }
        if (mode.equals("game")) {
            this.mainMenu = false;
            this.gameScreen = true;
            this.battle = false;
        }
        if (mode.equals("battle")) {
            this.mainMenu = false;
            this.gameScreen = false;
            this.battle = true;
        }
    }

    public GamePanel(LayoutManager layout) {
        super(layout);

    }

    public class ListenerThread implements Runnable {

        boolean listening = true;   //listener is always listening

        @Override
        public void run() {
            while (listening) {
                tick();
                repaint();
                try {
                    sleep(5);   //This is to save resources on repaint
                } catch (InterruptedException ex) {
                    Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
