package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import static java.lang.Math.abs;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.awt.RenderingHints;
import resources.SettingsProperties;

public class GamePanel extends JPanel {

    public GameEngine ge = new GameEngine();
    public GamePanel.ListenerThread lt;
    public MultiplicationDungeonForm parent;
    Thread th;
    int coolDown = 0;

    public GamePanel() {
        this.setLayout(null);
        this.setFocusable(true);
        this.addKeyListener(ge.keys);
        this.addMouseListener(ge.mouse);
        this.addMouseMotionListener(ge.mouse);
        newThread();
    }

    public void newThread() {
        if (th != null) {
            lt.listening = false;
        }
        lt = new GamePanel.ListenerThread();
        th = new Thread(lt);
        th.start();
    }

    public void reloadEngine() {
        this.setFocusable(true);
        this.addKeyListener(ge.keys);
        this.addMouseListener(ge.mouse);
        this.addMouseMotionListener(ge.mouse);
        newThread();
    }

    @Override
    protected void paintComponent(Graphics g1) {

        if (coolDown > 0) {
            coolDown--;
        }
        if (coolDown < 0) {
            coolDown = 0;
        }

        super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;
        if (SettingsProperties.antiAlisaingGraphics) {
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        if (SettingsProperties.antiAlisaingText) {
            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        ge.font[0] = ge.font[0].deriveFont(26.0f);
        g.setFont(ge.font[0]);

        if (ge.intro) {
            g.drawImage(ge.menuScreen, 0 - ge.ImageScroll, 0, 850, 650, null);
            g.drawImage(ge.menuScreen, 850 - ge.ImageScroll, 0, 850, 650, null);
            g.setColor(new Color(0, 0, 0, 125));

            g.fill3DRect(ge.loadingBarProg, 630, 210, 12, true);
            ge.loadingBarProg++;
            if (ge.loadingBarProg > (2 * (Math.PI / .01))) {
                ge.loadingBarProg = 0;
                ge.intro = false;
                ge.mainMenu = true;
            }
            if (ge.loadTimer <= 10) {
                g.setColor(Color.BLACK);
                g.drawString("Loading.", 365, 325);
                g.setColor(Color.WHITE);
                g.drawString("Loading.", 364, 324);
            }
            if (ge.loadTimer > 10) {
                g.setColor(Color.BLACK);
                g.drawString("Loading..", 365, 325);
                g.setColor(Color.WHITE);
                g.drawString("Loading..", 364, 324);
            }
            if (ge.loadTimer > 20) {
                g.setColor(Color.BLACK);
                g.drawString("Loading...", 365, 325);
                g.setColor(Color.WHITE);
                g.drawString("Loading...", 364, 324);
            }
        }

        if (ge.mainMenu) {
            g.drawImage(ge.menuScreen, 0 - ge.ImageScroll, 0, 850, 650, null);
            g.drawImage(ge.menuScreen, 850 - ge.ImageScroll, 0, 850, 650, null);
            if (ge.mainSettings) {
//                System.out.println(ge.gameScreen);
                for (int i = 0; i < ge.settingsBtns.length; i++) {
                    if (ge.mouse.getX() > ge.menuBtnLMargin && ge.mouse.getX() < (this.getWidth() - 400) + ge.menuBtnLMargin && ge.mouse.getY() > ge.settingsBtns[i].yLoc && ge.mouse.getY() < ge.settingsBtns[i].yLoc + 50) {
                        g.setColor(Color.DARK_GRAY);
                        g.fill3DRect(ge.settingsBtns[i].xLoc, ge.settingsBtns[i].yLoc, this.getWidth() - 400, ge.settingsBtns[i].height, true);
                        ge.settingsBtns[i].isMouseOver = true;
                        g.setColor(Color.WHITE);
                        g.drawString(ge.settingsBtns[i].label, findStringMid(g, this.getWidth(), ge.settingsBtns[i].label) + ge.settingsBtns[i].xLoc, ge.settingsBtns[i].yLoc + 33);

                        if (i == 1) {
                            g.setColor(Color.LIGHT_GRAY);
                            g.fill3DRect(500, 253, 45, 12, true);
                            g.fill3DRect(545, 253, 45, 12, true);
                            g.fill3DRect(500, 290, 45, 12, true);
                            g.fill3DRect(545, 290, 45, 12, true);
                            g.setColor(Color.BLACK);
                            g.drawString("+", 517, 269);
                            g.drawString("+", 562, 269);
                            g.drawString("-", 517, 306);
                            g.drawString("-", 562, 306);
                        }
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fill3DRect(ge.settingsBtns[i].xLoc, ge.settingsBtns[i].yLoc, this.getWidth() - 400, ge.settingsBtns[i].height, true);
                        ge.settingsBtns[i].isMouseOver = false;
                        g.setColor(Color.BLACK);
                        g.drawString(ge.settingsBtns[i].label, findStringMid(g, this.getWidth(), ge.settingsBtns[i].label) + ge.settingsBtns[i].xLoc, ge.settingsBtns[i].yLoc + 33);
                    }

                    if (ge.mouse.isMousePressed() && ge.settingsBtns[i].isMouseOver && coolDown <= 0) {
                        switch (i) {
                            case 0:
                                if (ge.playerSp > 0) {
                                    ge.playerSp = 0;
                                } else {
                                    ge.playerSp++;
                                }
                                ge.pl = new Player(ge, ge.playerSpritePaths[ge.playerSp], ge.keys);
                                coolDown = 200;
                                ge.updateBtns();
                                break;
                            case 1:
                                if (ge.mouse.getX() > 500 && ge.mouse.getX() < 545 && ge.mouse.getY() > 253 && ge.mouse.getY() < 270 && ge.roomSizeX < ge.roomXUpLimit) {
                                    ge.roomSizeX++;
                                }
                                if (ge.mouse.getX() > 545 && ge.mouse.getX() < 590 && ge.mouse.getY() > 253 && ge.mouse.getY() < 270 && ge.roomSizeY < ge.roomYUpLimit) {
                                    ge.roomSizeY++;
                                }
                                if (ge.mouse.getX() > 500 && ge.mouse.getX() < 545 && ge.mouse.getY() > 290 && ge.mouse.getY() < 302 && ge.roomSizeX > ge.roomXLowLimit) {
                                    ge.roomSizeX--;
                                }
                                if (ge.mouse.getX() > 545 && ge.mouse.getX() < 590 && ge.mouse.getY() > 290 && ge.mouse.getY() < 302 && ge.roomSizeY > ge.roomYLowLimit) {
                                    ge.roomSizeY--;
                                }
                                coolDown = 50;
                                ge.updateBtns();
                                break;
                            case 2:
                                UsefulSnippets.openWebpage("https://github.com/xNovax/Multiplication_Dungeon");
                                coolDown = 200;
                                break;
                            case 3:
                                ge.coolDown = 200;
                                ge.mainSettings = false;
                                ge.mainMenu = true;
                                ge.rooms = new Room[ge.roomSizeX][ge.roomSizeY];
                                ge.loadRooms();
                                break;

                        }
                    }
                }

            } else {
                g.drawImage(ge.menuTitle, 0, 0, null);
                for (int i = 0; i < ge.buttons.size(); i++) {
                    ge.buttons.get(i).draw(g1);
                }
            }

        }
        if (ge.gameScreen) {
            ge.rooms[ge.currentRoomX][ge.currentRoomY].draw(g);

            g.setColor(Color.red);

            ge.mouse.x1 = (int) ge.pl.xLoc + 32;
            ge.mouse.y1 = (int) ge.pl.yLoc + 32;

            if (ge.mouse.isMousePressed() && !ge.transitioning) {
                g.setColor(Color.WHITE);

                int dx = ge.mouse.x2 - ge.mouse.x1;
                int dy = ge.mouse.y2 - ge.mouse.y1;
                g.drawLine(ge.mouse.x1, ge.mouse.y1, ge.mouse.x2, ge.mouse.y2);

                if (dx > 0) {    //R
                    if (dy > 0) {
                        if (abs(dx) < abs(dy)) {
                            if (SettingsProperties.debugModeG == true) {
//                                g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                                g.drawString("quad_3_D", 50, 50);
                            }
                            ge.pl.distToMove = abs(dy);
                            ge.pl.orientation = 2;

                        } else {
                            if (SettingsProperties.debugModeG == true) {
//                                g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                                g.drawString("quad_3_R", 50, 50);
                            }
                            ge.pl.distToMove = abs(dx);
                            ge.pl.orientation = 1;

                        }

                    } else {
                        if (abs(dx) < abs(dy)) {
                            if (SettingsProperties.debugModeG == true) {
//                                g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                                g.drawString("quad_0_U", 50, 50);
                            }
                            ge.pl.distToMove = abs(dy);
                            ge.pl.orientation = 0;

                        } else {
                            if (SettingsProperties.debugModeG == true) {
//                                g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                                g.drawString("quad_0_R", 50, 50);
                            }
                            ge.pl.distToMove = abs(dx);
                            ge.pl.orientation = 1;

                        }

                    }
                } else //L
                if (dy > 0) {
                    if (abs(dx) < abs(dy)) {
                        if (SettingsProperties.debugModeG == true) {
//                            g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                            g.drawString("quad_2_D", 50, 50);
                        }
                        ge.pl.distToMove = abs(dy);
                        ge.pl.orientation = 2;

                    } else {
                        if (SettingsProperties.debugModeG == true) {
//                            g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                            g.drawString("quad_2_L", 50, 50);
                        }
                        ge.pl.distToMove = abs(dx);
                        ge.pl.orientation = 3;

                    }

                } else {
                    if (abs(dx) < abs(dy)) {
                        if (SettingsProperties.debugModeG == true) {
//                            g.drawLine(mouse.x1, mouse.y1, mouse.x1, mouse.y1 + dy);
                            g.drawString("quad_1_U", 50, 50);
                        }
                        ge.pl.distToMove = abs(dy);
                        ge.pl.orientation = 0;

                    } else {
                        if (SettingsProperties.debugModeG == true) {
//                            g.drawLine(mouse.x1, mouse.y1, mouse.x1 + dx, mouse.y1);
                            g.drawString("quad_1_L", 50, 50);
                        }
                        ge.pl.distToMove = abs(dx);
                        ge.pl.orientation = 3;

                    }
                }
            } else {
                ge.pl.distToMove = 0;
            }

            for (int i = 0; i < ge.rooms[ge.currentRoomX][ge.currentRoomY].en_arry.size(); i++) {
                ge.rooms[ge.currentRoomX][ge.currentRoomY].en_arry.get(i).draw(g);
                g.setColor(Color.GREEN);
                if (SettingsProperties.debugModeG == true) {
                    g.drawLine((int) ge.rooms[ge.currentRoomX][ge.currentRoomY].en_arry.get(i).xLoc + 32, (int) ge.rooms[ge.currentRoomX][ge.currentRoomY].en_arry.get(i).yLoc + 32,
                            (int) ge.pl.xLoc + 32, (int) ge.pl.yLoc + 32);
                }
            }
            ge.pl.draw(g);
            for (int a = 0; a < ge.rooms[ge.currentRoomX][ge.currentRoomY].en_arry.size(); a++) {
                if ((ge.pl.getBounds().intersects(ge.rooms[ge.currentRoomX][ge.currentRoomY].en_arry.get(a).getBounds())) && (ge.pl.graceTimer < 1)) {
                    ge.en_index = a;
                    ge.switchTo("battle");
                    ge.frozen = true;
                }
            }
            ge.hud.draw(g);
        }
        if (ge.battle) {
            ge.qt.draw(g);
            ge.pl.graceTimer = 1000;
        }

        if (ge.paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            for (int i = 0; i < ge.pauseBtns.length; i++) {
                if (ge.mouse.getX() > ge.menuBtnLMargin && ge.mouse.getX() < (this.getWidth() - 400) + ge.menuBtnLMargin && ge.mouse.getY() > ge.pauseBtns[i].yLoc && ge.mouse.getY() < ge.pauseBtns[i].yLoc + 50) {
                    g.setColor(Color.DARK_GRAY);
                    g.fill3DRect(ge.pauseBtns[i].xLoc, ge.pauseBtns[i].yLoc, this.getWidth() - 400, ge.pauseBtns[i].height, true);
                    ge.pauseBtns[i].isMouseOver = true;
                    g.setColor(Color.WHITE);
                    g.drawString(ge.pauseBtns[i].label, findStringMid(g, this.getWidth(), ge.pauseBtns[i].label) + ge.pauseBtns[i].xLoc, ge.pauseBtns[i].yLoc + 33);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fill3DRect(ge.pauseBtns[i].xLoc, ge.pauseBtns[i].yLoc, this.getWidth() - 400, ge.pauseBtns[i].height, true);
                    ge.pauseBtns[i].isMouseOver = false;
                    g.setColor(Color.BLACK);
                    g.drawString(ge.pauseBtns[i].label, findStringMid(g, this.getWidth(), ge.pauseBtns[i].label) + ge.pauseBtns[i].xLoc, ge.pauseBtns[i].yLoc + 33);
                }

                if (ge.mouse.isMousePressed() && ge.pauseBtns[i].isMouseOver && coolDown <= 0) {
                    switch (i) {
                        case 0:
                            this.parent.saveState();
                            ge.paused = false;
                            break;
                        case 1:
                            this.parent.loadState();
                            ge.paused = false;
                            break;
                        case 2:
                            ge.paused = false;
                            break;
                        case 3:
                            ge = new GameEngine();
                            this.reloadEngine();
                            break;

                    }
                }
            }
        }

        if (ge.transitioning) {
            if (ge.transitionDir == 4) {
                g.setColor(Color.BLACK);
                ge.transitionProg = ge.transitionProg + 4;
                ge.drawTransition(ge.transitionDir, g);
            } else {
                g.setColor(Color.BLACK);
                ge.transitionProg = ge.transitionProg + 10;
                ge.drawTransition(ge.transitionDir, g);
            }

        }

        if (ge.gameOver) {
            String[] endMsg = new String[3];
            endMsg[0] = "Game Over";
            endMsg[1] = "You got " + ge.pl.score + " points!";
            endMsg[2] = "Completed in " + (long) ((ge.endMillis - ge.startMillis) / 1000) / 60 + " minutes and " + (int) ((ge.endMillis - ge.startMillis) / 1000) % 60 + " seconds";
            g.setColor(Color.darkGray);
            for (int i = 0; i < endMsg.length; i++) {
                g.drawString(endMsg[i], findStringMid(g, this.getWidth(), endMsg[i]) + 200, 200 + 50 * i);
            }

            if (ge.mouse.getX() > ge.menuBtnLMargin && ge.mouse.getX() < (this.getWidth() - 400) + ge.menuBtnLMargin && ge.mouse.getY() > 500 && ge.mouse.getY() < 550) {
                if (ge.mouse.isMousePressed()) {
                    ge = new GameEngine();
                    this.reloadEngine();
                }
                g.setColor(Color.DARK_GRAY);
                g.fill3DRect(ge.menuBtnLMargin, 500, this.getWidth() - 400, 50, true);
                g.setColor(Color.WHITE);
                g.drawString("Exit", findStringMid(g, this.getWidth(), "Exit") + ge.menuBtnLMargin, 500 + 33);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fill3DRect(ge.menuBtnLMargin, 500, this.getWidth() - 400, 50, true);
                g.setColor(Color.BLACK);
                g.drawString("Exit", findStringMid(g, this.getWidth(), "Exit") + ge.menuBtnLMargin, 500 + 33);
            }
        }

        g.setColor(Color.red);

//        g.drawString(ge.mouse.getX() + " - " + ge.mouse.getY(), 100, 100);
//        g.drawString(ge.pl.getX() + " - " + ge.pl.getY(), 100, 100);
    }

    public void setParent(MultiplicationDungeonForm p) {
        parent = p;
    }

    public int findStringMid(Graphics g, int width, String s) {
        int stringLen = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        int start = (width - 400) / 2 - stringLen / 2;
        return start;
    }

    public class ListenerThread implements Runnable {

        public boolean listening = true;   //listener is always listening

        @Override
        public void run() {
            while (listening) {
                ge.tick();
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
