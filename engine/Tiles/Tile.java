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
    private int rowIndex;
    private int colIndex;

    // constructors
    public Tile() { // basic tile, aka air
        tileType = "Tile";
        isSolid = false;
        isBreakable = false;
        dangerLevel = 0; // default danger level
    }
    public Tile(int row, int col) { // basic tile, aka air
        tileType = "Tile";
        isSolid = false;
        isBreakable = false;
        dangerLevel = 0; // default danger level
        this.rowIndex = row;
        this.colIndex = col;
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
    public int getRowIndex() {
    return rowIndex;
    }
    public int getColIndex() {
        return colIndex;
    }
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    // methods
    public void update() {} // to be overridden by subclasses if needed
}
