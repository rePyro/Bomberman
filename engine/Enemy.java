import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy extends Player{
    int targetRow; int overallTargetRow;
    int targetCol; int overallTargetCol;
    Map map;
    KeyHandler keyHandler;
    private int actionState = 0; // 0 = idle, 1 = moving, 2 = placing bomb
    private int ticksPerTile;
    private int currentTick = 0;
    private int ticksPerSecond = 60;
    private List<Coordinate> currentPath = new ArrayList<>();
    private int pathIndex = 0;
    private boolean escapingBomb = false;
    private boolean pathingToBomb = false;
    private static final Random rand = new Random();
    private int randomTargetCooldown = 0;
    private static final int RANDOM_TARGET_COOLDOWN_TICKS = 10;

    private double bombFuseMultiplier = 1;
    private int fireFuseAdd = 120;

    private boolean permission;
    
    //Constructors
    public Enemy(Map map, int row, int col) {
        super(map, row, col);
        this.targetRow = row;
        this.targetCol = col;
        this.map = map;
        this.setSpeed(3);
        this.setOverallTarget(-1, -1); // Default overall target to bottom-right corner
        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        this.escapingBomb = false;
        this.ticksPerTile = (int)(map.getTileSize()/getSpeed());
        setSpeedCap(2);
        getEnemyImage();
    }
    public Enemy(Map map, int row, int col, KeyHandler keyHandler) {
        super(map, row, col, keyHandler);
        this.targetRow = row; // Set targetRow to current row
        this.targetCol = col; // Set targetCol to current column
        this.map = map; // Store the map reference
        this.keyHandler = keyHandler; // Initialize keyHandler
    }


    // Weighting methods for actions
    public void setDesire(int desire) {
    }
    public void setTarget(int row, int col) { this.targetRow = row; this.targetCol = col; }
    public void setTarget(Coordinate target) { this.targetRow = target.getRow(); this.targetCol = target.getCol(); }
    public void setOverallTarget(int row, int col) { this.overallTargetRow = row; this.overallTargetCol = col; }
    public void togglePerms() {if (permission) {permission = false;} else if (!permission) {permission = true;}
    System.out.println("Enemy permissions set to: "+permission);}
    //Checking if a tile is safe for all future ticks
    public boolean needToMove(Map map, int ticks, Coordinate target) {
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        int currentTick = ticks;
        cautiousMap.mapUpdate(ticks);
        while (currentTick < 200) {
            cautiousMap.mapUpdate();
            if (danger(cautiousMap, target.getRow(), target.getCol())) {
                //System.out.println("Danger detected at (" + target.getRow() + ", " + target.getCol() + ") at tick " + currentTick);
                return true; // Need to move
            }
            currentTick ++;
        }
        return false;
    }
     public boolean needToMove() {
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        int currentTick = 0;
        while (currentTick < 200) {
            cautiousMap.mapUpdate();
            if (cautiousMap.getBombList().size() == 0) {
                //System.out.println("No bombs on the map at tick " + currentTick);
            }
            if (danger(cautiousMap, this.getRow(), this.getCol())) {
                //System.out.println("Danger detected at (" + this.getRow() + ", " + this.getCol() + ") at tick " + currentTick);
                return true; // Need to move
            }
            currentTick++;
        }
        return false;
    }
    
    public boolean danger(Map map, int row, int col) {
        if (map.getTile(row, col) instanceof BombFire) {
            return true;
        }
        else return false;
    }

    // taking actions
    public void takeAction() {
        if (!isAlive()) return;
        actionState++;
        //System.out.println("Enemy taking action at (" + getRow() + ", " + getCol() + ")");
        if (actionState == ticksPerTile) { actionState = 0;
        if (permission && canAddBomb() && worthPlacingBomb() && couldEscapeBomb() ) {map.addBomb(this);}
        if (needToMove() && !escapingBomb) {Path path = bestEscapePath(map, getRow(), getCol()); if (path != null) {currentPath = path.getPath(); 
            escapingBomb = true; pathIndex = 0; System.out.println("Enemy needs to move, found escape path: " + currentPath);}} 
            else if (!escapingBomb && !pathingToBomb && currentPath.isEmpty()) {
                Path path = bestBombPath(map, getRow(), getCol()); 
                if (path != null) {currentPath = path.getPath(); pathIndex = 0; pathingToBomb = true; System.out.println("Set path to go BOMB WOOHOO");}
            else {currentPath = randomPath(map, getRow(), getCol()).getPath(); pathIndex = 0;
            System.out.println("Enemy is not escaping a bomb, found random path: " + currentPath);}}
            System.out.println("Enemy current path: " + currentPath + " at index " + pathIndex);
    }
        pathFind();
    }


    public void pathFind() {
        //System.out.println("Enemy pathfinding at (" + getRow() + ", " + getCol() + ")");
        int y = getY();
        int x = getX();
        if (currentPath != null && pathIndex < currentPath.size()) {
            Coordinate step = currentPath.get(pathIndex);
            int targetPixelY = 48*step.getRow(); //System.out.println("Target pixel Y: " + targetPixelY+"y = "+y);
            int targetPixelX = 48*step.getCol(); //System.out.println("Target pixel X: " + targetPixelX+"x = "+x);
            // Debug: print if the enemy is on a safe tile
            if (!danger(map, step.getRow(), step.getCol())) {
                //System.out.println("Enemy found safe tile at (" + step.getRow() + ", " + step.getCol() + ")");
            }
            if (y == targetPixelY && x == targetPixelX) {
                pathIndex++;
                if (pathIndex < currentPath.size()) {
                    Coordinate nextStep = currentPath.get(pathIndex);
                    setTarget(nextStep.getRow(), nextStep.getCol());
                } else {
                    randomTargetCooldown = RANDOM_TARGET_COOLDOWN_TICKS;
                    System.out.println("Now at target coordinates: (" + getRow() + ", " + getCol() + ")");
                    System.out.println("Clearing current path, enemy has reached the target.");
                    currentPath.clear(); // <-- Clear the path when finished
                    pathingToBomb = false;
                    escapingBomb = false; // Reset escapingBomb when the path is finished
                }
            }
        }
        int tileSize = map.getTileSize();
        int targetPixelY = rowToY(targetRow);
        int targetPixelX = colToX(targetCol);
        if (Math.abs(y - targetPixelY) <= getSpeed()) y = targetPixelY;
        if (Math.abs(x - targetPixelX) <= getSpeed()) x = targetPixelX;
        setY(y);
        setX(x);
        if (y != targetPixelY) {
            if (x == targetPixelX) {
                if (y < targetPixelY && canMoveDown(map)) setY(y + getSpeed());
                else if (y > targetPixelY && canMoveUp(map)) setY(y - getSpeed());
            } else {
                if (x < targetPixelX && canMoveRight(map)) setX(x + getSpeed());
                else if (x > targetPixelX && canMoveLeft(map)) setX(x - getSpeed());
            }
        } else if (x != targetPixelX) {
            if (y == targetPixelY) {
                if (x < targetPixelX && canMoveRight(map)) setX(x + getSpeed());
                else if (x > targetPixelX && canMoveLeft(map)) setX(x - getSpeed());
            }
        }
    }

    public void setTarget() {
        if (escapingBomb) {
            System.out.println("Enemy is escaping a bomb");
            // Use a prediction map with normal bomb fuses but longer fire fuse for the enemy
        }
    }
    public int rowToY(int row) { return (int)((row)*map.getTileSize()); }
    public int colToX(int col) { return (int)((col)*map.getTileSize()); }

 
    // --- Bomb escape logic using EscapeResult ---
    public boolean couldEscapeBomb() {
        // Use a prediction map with normal bomb fuses but longer fire fuse for the enemy
        if (!this.canAddBomb()){return false;}
        Map cautiousMap = map.copyWithModifiedFuses(1, fireFuseAdd);
        cautiousMap.addBomb(new Enemy(this), false);
        Path best = bestEscapePath(cautiousMap, getRow(), getCol());
        if (best == null) {
            //System.out.println("No valid escape path found for bomb at (" + getRow() + ", " + getCol() + ")");
            return false;
        }
        //System.out.println("Best escape path chosen: " + best.path);
        return true;
    }
    public boolean couldEscapeBombAt(int row, int col) {
        // Use a prediction map with normal bomb fuses but longer fire fuse for the enemy
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        Enemy placer = new Enemy(this); placer.setRow(row); placer.setCol(col);
        cautiousMap.addBomb(placer);
        Path best = bestEscapePath(cautiousMap, row, col);
        if (best == null) {
            //System.out.println("No valid escape path found for bomb at (" + row + ", " + col + ")");
            return false;
        }
        //System.out.println("Best escape path chosen: " + best.path);
        return true;
    }

    public boolean worthPlacingBomb() {
        int power = this.getPower();
        Tile tile = map.getTile(getRow(), getCol());
        if (tile instanceof Bomb) {
            power = ((Bomb) tile).getPower();
        } else if (map.getBombList().size() > 0) {
            power = map.getBombList().get(map.getBombList().size() - 1).getPower();
        }
        int wallsBroken = 0;
        int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] dir : directions) {
            for (int i = 1; i <= power; i++) {
                int r = getRow() + dir[0] * i;
                int c = getCol() + dir[1] * i;
                Tile t = map.getTile(r, c);
                if (t instanceof HardWall) break;
                if (t instanceof SoftWall) {
                    wallsBroken++;
                    break;
                }
            }
        }
        boolean worth = wallsBroken > 0;
        //System.out.println("worthPlacingBomb at (" + getRow() + "," + getCol() + "): " + worth + " (wallsBroken=" + wallsBroken + ")");
        return worth;
    }
    public boolean worthPlacingBombAt(int row, int col) {
        int power = this.getPower();
        Tile tile = map.getTile(row, col);
        if (tile instanceof Bomb) {
            power = ((Bomb) tile).getPower();
        } else if (map.getBombList().size() > 0) {
            power = map.getBombList().get(map.getBombList().size() - 1).getPower();
        }
        int wallsBroken = 0;
        int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] dir : directions) {
            for (int i = 1; i <= power; i++) {
                int r = row + dir[0] * i;
                int c = col + dir[1] * i;
                Tile t = map.getTile(r, c);
                if (t instanceof HardWall) break;
                if (t instanceof SoftWall) {
                    wallsBroken++;
                    break;
                }
            }
        }
        return wallsBroken > 0;
    }
    public class Path {
        public List<Coordinate> path;
        public int length;

        public Path(List<Coordinate> path, int length) {
            this.path = path;
            this.length = length;
        }
        public List<Coordinate> getPath() {
            return path;
        }

        @Override
        public String toString() {
            return "EscapeResult{" +
                    "path=" + path +
                    ", length=" + length +
                    '}';
        }
    }

    public List<Path> findAllEscapePaths(Map map, int startRow, int startCol) {
        Queue<List<Coordinate>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<Coordinate> startPath = new ArrayList<>();
        startPath.add(new Coordinate(startRow, startCol));
        queue.add(startPath);
        visited.add(startRow + "," + startCol);

        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        List<Path> validPaths = new ArrayList<>();

        while (!queue.isEmpty()) {
            List<Coordinate> path = queue.poll();
            Coordinate last = path.get(path.size() - 1);
            int row = last.getRow();
            int col = last.getCol();
            if (!validPaths.isEmpty())
            {if (path.size() > validPaths.get(0).getPath().size()) {break;}}

            // How do I make it so that I only check the destination tile for all future ticks after arrival, this is making it check from the current tick the game is at
            boolean safe = true;
            for (int t = 0; t <= 4*ticksPerSecond; t+=ticksPerTile) {
                Map fm = map.mapUpdatedByTicks(t);
                if (fm == null || danger(fm, row, col)) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                boolean isStart = (row == startRow && col == startCol);
                if (!isStart || (path.size() == 1 && !(map.getTile(row, col) instanceof Bomb))) {
                    if (!validPaths.isEmpty())
            {if (path.size() < validPaths.get(0).getPath().size()) {validPaths.clear();}}
                    validPaths.add(new Path(path, path.size() - 1));
                    //System.out.println("ADDED ESCAPE PATH TO LIST: " + path);
                }
            }

            // Try moving in all directions
            for (int[] dir : dirs) {
                int nrow = row + dir[0];
                int ncol = col + dir[1];
                String key = nrow + "," + ncol;
                if (nrow < 0 || nrow >= map.getHeight() || ncol < 0 || ncol >= map.getWidth()) continue;
                if (visited.contains(key)) continue;
                boolean isStart = (path.size() == 1);
                if (!canMoveTo(map.mapUpdatedByTicks(Math.min(path.size()*ticksPerTile, 7*ticksPerSecond)), nrow, ncol, isStart)) continue;
                if (danger(map.mapUpdatedByTicks(Math.min(path.size()*ticksPerTile, 7*ticksPerSecond)), nrow, ncol)) continue;
                if (danger(map.mapUpdatedByTicks(Math.min(path.size()*ticksPerTile, 7*ticksPerSecond)), nrow, ncol)) continue;
                if (danger(map.mapUpdatedByTicks(Math.min((int)((path.size()-1)*ticksPerTile), 7*ticksPerSecond)), nrow, ncol)) continue;
                if (danger(map.mapUpdatedByTicks(Math.min((int)((path.size()-1)*ticksPerTile), 7*ticksPerSecond)), nrow, ncol)) continue;

                List<Coordinate> newPath = new ArrayList<>(path);
                newPath.add(new Coordinate(nrow, ncol));
                queue.add(newPath);
                visited.add(key);
            }
        }
        return validPaths;
    }
    public Path bestEscapePath(Map map, int startRow, int startCol) {
        //System.out.println("Finding best escape path from (" + startRow + ", " + startCol + ")");
        List<Path> allPaths = findAllEscapePaths(map, startRow, startCol);
        if (allPaths.isEmpty()) return null;

        // Find the minimum path length
        int minLen = Integer.MAX_VALUE;
        for (Path er : allPaths) {
            if (er.path.size() < minLen) minLen = er.path.size();
        }

        // Collect all shortest paths
        List<Path> shortest = new ArrayList<>();
        for (Path er : allPaths) {
            if (er.path.size() == minLen) shortest.add(er);
        }

        // Pick one at random if there are multiple
        return shortest.get(rand.nextInt(shortest.size()));
    }

    private boolean canMoveTo(Map map, int row, int col, boolean allowBomb) {
        if (row < 0 || row >= map.getHeight() || col < 0 || col >= map.getWidth()) return false;
        try {
            Tile t = map.getTile(row, col);
            //if (allowBomb && t instanceof Bomb) return true;
            return !(t instanceof HardWall) && !(t instanceof SoftWall) && !(t instanceof Bomb) && !(t instanceof BombFire);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    private boolean canMoveTo(Map map, int row, int col) {
        return canMoveTo(map, row, col, false);
    }

    public Path randomPath(Map map, int startRow, int startCol) {
        List<Coordinate> path = new ArrayList<>();
        path.add(new Coordinate(startRow, startCol));
        List<Coordinate> options = new ArrayList<>(path);
        int[][] dirs = new int[][] { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] dir : dirs) {
                int nrow = startRow + dir[0];
                int ncol = startCol + dir[1];
                if (nrow < 0 || nrow >= map.getHeight() || ncol < 0 || ncol >= map.getWidth()) continue;
                if (needToMove(map, 0, new Coordinate(nrow, ncol))) continue;
                if (!canMoveTo(map, nrow, ncol)) continue;
                options.add(new Coordinate(nrow, ncol));}
                if (!options.isEmpty()) {path.add(options.get(rand.nextInt(options.size())));}        
        return new Path(path, path.size());
    }
    //More advanced targetSetting
    //Clone method
    
      public List<Path> findBombPaths(Map map, int startRow, int startCol) {
        System.out.println("TAG");
        Queue<List<Coordinate>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<Coordinate> startPath = new ArrayList<>();
        startPath.add(new Coordinate(startRow, startCol));
        queue.add(startPath);
        visited.add(startRow + "," + startCol);

        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        List<Path> validPaths = new ArrayList<>();

        while (!queue.isEmpty()) {
            List<Coordinate> path = queue.poll();
            Coordinate last = path.get(path.size() - 1);
            int row = last.getRow();
            int col = last.getCol();
            if (!validPaths.isEmpty())
            {if (path.size() > validPaths.get(0).getPath().size()) {break;}}

            // How do I make it so that I only check the destination tile for all future ticks after arrival, this is making it check from the current tick the game is at
            
            if (!needToMove(map, 0, new Coordinate(row, col)) && worthPlacingBombAt(row, col) && couldEscapeBombAt(row, col)) {

                boolean isStart = (row == startRow && col == startCol);
                if (!isStart || (path.size() == 1 && !(map.getTile(row, col) instanceof Bomb))) {
                    if (!validPaths.isEmpty())
            {if (path.size() < validPaths.get(0).getPath().size()) {validPaths.clear();}}
                    validPaths.add(new Path(path, path.size() - 1));
                    System.out.println("ADDED BOMB PATH TO LIST: " + path);
                }
            }
            // Try moving in all directions
            for (int[] dir : dirs) {
                int nrow = row + dir[0];
                int ncol = col + dir[1];
                String key = nrow + "," + ncol;
                if (nrow < 0 || nrow >= map.getHeight() || ncol < 0 || ncol >= map.getWidth()) continue;
                if (visited.contains(key)) continue;
                boolean isStart = (path.size() == 1);
                if (!canMoveTo(map.mapUpdatedByTicks(Math.min(path.size()*ticksPerTile, 7*ticksPerSecond)), nrow, ncol, isStart)) continue;
                if (needToMove(map, 0, new Coordinate(nrow, ncol))) continue;

                List<Coordinate> newPath = new ArrayList<>(path);
                newPath.add(new Coordinate(nrow, ncol));
                queue.add(newPath);
                visited.add(key);
            }
        }
        return validPaths;
    }
    public Path bestBombPath(Map map, int startRow, int startCol) {
        //System.out.println("Finding best escape path from (" + startRow + ", " + startCol + ")");
        List<Path> allPaths = findBombPaths(map, startRow, startCol);
        if (allPaths.isEmpty()) return null;

        // Find the minimum path length
        int minLen = Integer.MAX_VALUE;
        for (Path er : allPaths) {
            if (er.path.size() < minLen) minLen = er.path.size();
        }

        // Collect all shortest paths
        List<Path> shortest = new ArrayList<>();
        for (Path er : allPaths) {
            if (er.path.size() == minLen) shortest.add(er);
        }

        // Pick one at random if there are multiple
        return shortest.get(rand.nextInt(shortest.size()));
    }

    
    
    public Enemy(Enemy other) {
        super(new Map(other.map), other.getRow(), other.getCol());
        this.targetRow = other.targetRow;
        this.targetCol = other.targetCol;
        this.overallTargetRow = other.overallTargetRow;
        this.overallTargetCol = other.overallTargetCol;
        this.ticksPerTile = other.ticksPerTile;
        this.currentTick = other.currentTick;
        this.currentPath = new ArrayList<>(other.currentPath);
        this.pathIndex = other.pathIndex;
        this.escapingBomb = other.escapingBomb;
        this.bombFuseMultiplier = other.bombFuseMultiplier;
        this.fireFuseAdd = other.fireFuseAdd;
    }
    //Respawn
    public void respawn(Map map) {
        deathCancel();
    if (isAlive() || isDying()) {System.out.println("DEBUG"); return;}
    setAlive();
      map.setTile(1, 1, new Tile()); // clear the tile
      this.setRow(1);
      this.setCol(1);
      this.setX(48);
      this.setY(48);
  }
    //Supering the upgrades
    public void upgradeCheck(Map map) {
    super.upgradeCheck(map);
    if (isAlive() == true && map.getTile(getRow(), getCol()) instanceof SpeedUpgrade) { // if the player is on a SpeedUpgrade tile
      this.ticksPerTile = (int)(map.getTileSize()/getSpeed());
    }
  }
    //Graphics
    private BufferedImage enemyImage;

    public void getEnemyImage() {
        try {
            enemyImage = ImageIO.read(new File("graphics/GokuSprites/Goku.png")); // Load the enemy image from file
        } catch (IOException e) {
            e.printStackTrace(); // print the stack trace if there is an error loading the image
            System.out.println("Error loading enemy image!"); // print an error message
        }
    }
    public void draw(Graphics2D g2) {
        //System.out.println("Drawing Enemy at (" + getRow() + ", " + getCol() + ")");
        if (enemyImage != null) {
         g2.drawImage(enemyImage, getX()-24, getY()-24, 2*map.getTileSize(), 2*map.getTileSize(), null);
        } else {
            System.out.println("Enemy image not loaded, drawing placeholder.");
            g2.setColor(Color.RED);
            g2.fillRect(getX(), getY(), map.getTileSize(), map.getTileSize());
        }
    }

}