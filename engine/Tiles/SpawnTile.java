public class SpawnTile extends Tile {
    
    // variables
    public static int spawnCount = 0; // static variable to keep track of spawn tiles
    private int spawnNumber;

    // constructor
    public SpawnTile() {
        super("SpawnTile", false, false); // spawn tiles are not solid or breakable
        spawnCount++; // increment the spawn tile count
        spawnNumber = spawnCount; // assign the current spawn tile count to this tile
    }  
    public int getSpawnNumber() {
        return spawnNumber; // return the spawn number of this tile
    }
}
