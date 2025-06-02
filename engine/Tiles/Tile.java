// package declarations and imports here;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile implements Cloneable {
    // variables
    private String tileType;
    private boolean isSolid;
    private boolean isBreakable;
    private int dangerLevel; // 0 = no danger, 1 = low danger, 2 = medium danger, 3 = high danger

    // constructors
    public Tile() { // basic tile, aka air
        tileType = "Tile";
        isSolid = false;
        isBreakable = false;
        dangerLevel = 0; // default danger level
    }
    public Tile(String tileType, boolean isSolid, boolean isBreakable) { // any other tile
        this.tileType = tileType;
        this.isSolid = isSolid;
        this.isBreakable = isBreakable;
        dangerLevel = 0; // default danger level
    }
    //Clone
    @Override
    public Tile clone() {
        try {
            return (Tile) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should not happen if Tile implements Cloneable
            throw new AssertionError();
        }
    }


    // accessors
    public boolean getBreakable() {
        return isBreakable;
    }
    public boolean getSolid() {
        return isSolid;
    }
    public String getTileType() {
        return tileType;
    }

    // methods
    public void breakTile() {} // to be overridden by subclasses if needed
}
