// package declarations and imports here;
public class Tile
{
    // variables
    private String tileType;
    private boolean isSolid;
    private boolean isBreakable;

    // constructors
    public Tile() { // basic tile, aka air
        tileType = "Tile";
        isSolid = false;
        isBreakable = false;
    }
    public Tile(String tileType, boolean isSolid, boolean isBreakable) { // any other tile
        this.tileType = tileType;
        this.isSolid = isSolid;
        this.isBreakable = isBreakable;
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
    
}
