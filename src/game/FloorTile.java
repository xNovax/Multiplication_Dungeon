package game;

import java.awt.Color;
import java.io.Serializable;
import java.util.Random;

public class FloorTile implements Serializable {

    int metaElement = 0;
    int metaType = -1;
    int metaDir = -1;
    int metaAnim = 0;
    boolean isSpawn = false;
    //Tile metaElement:
    //0 = none
    //1 = Water
    //2 = Lava

    //Tile metaType:
    //0 = none
    //1 = (1side)
    //2 = (2sidesC)
    //3 = (2sidesO)
    //4 = (3sides)
    //Tile metaDir (rotation/ orientation, relative to spritesheet):
    //0 = up
    //1 = left
    //2 = down
    //3 = right
    int TILE_ID = -1;

    Random rand = new Random();
    Color c;
    //Tile 0 = Test
    //Tile 1 = Wall
    //Tile 2 = Floor
    //Tile 3 = Door
    //Tile 4 = Water
    //Tile 5 = Ice
    //Tile 6 = Lava
    //Tile 7 = Moss
    //Tile 8 = Grass
    //Tile 9 = Rock
    //Tile 10 = ???

    public FloorTile(int id) {
        this.TILE_ID = id;
//        c = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        c = Color.GRAY;
    }

    public void setTile(int i) {
        TILE_ID = i;
    }

    public boolean isSolid() {
        switch (TILE_ID) {
            case 0:
                return false;
            case 1:
                return true;
            case 2:
                return false;
            case 3:
                return false;
            case 4:
                return true;
            case 5:
                return false;
            case 6:
                return true;
            case 7:
                return false;
            case 8:
                return false;
            case 9:
                return true;

            default:
                return false;
        }
    }

    public boolean isDoor() {
        switch (TILE_ID) {
            case 3:
                return true;
            case 10:
                return true;
            default:
                return false;
        }
    }

    public void setMetadata(int i) {
        metaElement = i;
    }

    public Color getColor() {

        switch (TILE_ID) {
            case 0:
                c = Color.MAGENTA;
                break;
            case 1:
                c = new Color(65, 43, 3);
                break;
            case 2:
                c = new Color(130, 86, 6);
                break;
            case 3:
                c = new Color(32, 21, 1);
                break;
            case 4:
                c = new Color(32, 21, 1);
                break;
            case 5:
                c = new Color(255, 21, 1);
                break;
        }
        return c;
    }

}
