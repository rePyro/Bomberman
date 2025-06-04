import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy extends Player {
    int targetRow; int overallTargetRow;
    int targetCol; int overallTargetCol;
    Map map;
    KeyHandler keyHandler;
    private int ticksPerTile;
    private int currentTick = 0;
    private int ticksPerSecond = 60;
    private List<Coordinate> currentPath = new ArrayList<>();
    private int pathIndex = 0;
    private boolean escapingBomb = false;
    private static final Random rand = new Random();
    private int randomTargetCooldown = 0;
    private static final int RANDOM_TARGET_COOLDOWN_TICKS = 10;

    private double bombFuseMultiplier = 2.5;
    private int fireFuseAdd = 6;
    
    //Constructors
    public Enemy(Map map, int row, int col) {
        super(map, row, col);
        this.targetRow = row;
        this.targetCol = col;
        this.map = map;
        this.setSpeed(1);
        this.setOverallTarget(-1, -1); // Default overall target to bottom-right corner
        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        this.escapingBomb = false;
        this.ticksPerTile = (int)(map.getTileSize()/getSpeed());
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

    //Checking if a tile is safe for all future ticks
    public boolean needToMove(Map map, int ticks, Coordinate target) {
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        int currentTick = 0;
        while (currentTick < 7 * ticksPerSecond) {
            cautiousMap = cautiousMap.mapUpdatedByTicks(ticks+currentTick);
            if (cautiousMap == null) {
                System.out.println("Cautious map is null at tick " + currentTick);
                return false; // No need to move if the map is null
            }
            if (danger(cautiousMap, target.getRow(), target.getCol())) {
                System.out.println("Danger detected at (" + target.getRow() + ", " + target.getCol() + ") at tick " + currentTick);
                return true; // Need to move
            }
            currentTick += ticksPerTile;
        }
        return false;
    }
     public boolean needToMove() {
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        int currentTick = 0;
        while (currentTick < 7 * ticksPerSecond) {
            cautiousMap = cautiousMap.mapUpdatedByTicks(currentTick);
            if (cautiousMap == null) {
                System.out.println("Cautious map is null at tick " + currentTick);
                return false; // No need to move if the map is null
            }
            if (danger(cautiousMap, this.getRow(), this.getCol())) {
                System.out.println("Danger detected at (" + this.getRow() + ", " + this.getCol() + ") at tick " + currentTick);
                return true; // Need to move
            }
            currentTick += ticksPerTile;
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
        //System.out.println("Enemy taking action at (" + getRow() + ", " + getCol() + ")");
        if (needToMove() && !escapingBomb && bestEscapePath(map, getRow(), getCol()) != null) {currentPath = bestEscapePath(map, getRow(), getCol()).getPath(); escapingBomb = true; pathIndex = 0; System.out.println("Enemy needs to move, found escape path: " + currentPath);}
        pathFind();
    }


    public void pathFind() {
        int y = getY();
        int x = getX();
        if (currentPath != null && pathIndex < currentPath.size()) {
            Coordinate step = currentPath.get(pathIndex);
            int targetPixelY = rowToY(step.getRow());
            int targetPixelX = colToX(step.getCol());
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
                    currentPath.clear(); // <-- Clear the path when finished
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

/* 
    // --- Bomb escape logic using EscapeResult ---
    public boolean couldEscapeBomb() {
        // Use a prediction map with normal bomb fuses but longer fire fuse for the enemy
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        cautiousMap.addBomb(getRow(), getCol());
        SuperMap bombPrediction = new SuperMap(cautiousMap);
        bombPrediction.makePrediction();

        EscapeResult best = bestEscapePath(bombPrediction, getRow(), getCol());
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
        cautiousMap.addBomb(row, col);
        SuperMap bombPrediction = new SuperMap(cautiousMap);
        bombPrediction.makePrediction();

        EscapeResult best = bestEscapePath(bombPrediction, row, col);
        if (best == null) {
            //System.out.println("No valid escape path found for bomb at (" + row + ", " + col + ")");
            return false;
        }
        //System.out.println("Best escape path chosen: " + best.path);
        return true;
    }

    public boolean worthPlacingBomb() {
        int power = 2;
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
        int power = 2;
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

    */
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

            // How do I make it so that I only check the destination tile for all future ticks after arrival, this is making it check from the current tick the game is at
            boolean safe = true;
            for (int t = 0; t <= 7*ticksPerSecond; t+=ticksPerTile) {
                Map fm = map.mapUpdatedByTicks(t);
                if (fm == null || danger(fm, row, col)) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                boolean isStart = (row == startRow && col == startCol);
                if (!isStart || (path.size() == 1 && !(map.getTile(row, col) instanceof Bomb))) {
                    validPaths.add(new Path(path, path.size() - 1));
                    System.out.println("ADDED ESCAPE PATH TO LIST: " + path);
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
            if (allowBomb && t instanceof Bomb) return true;
            return !(t instanceof HardWall) && !(t instanceof SoftWall) && !(t instanceof Bomb);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    private boolean canMoveTo(Map map, int row, int col) {
        return canMoveTo(map, row, col, false);
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
            g2.drawImage(enemyImage, getX(), getY(), null);
        } else {
            System.out.println("Enemy image not loaded, drawing placeholder.");
            g2.setColor(Color.RED);
            g2.fillRect(getX(), getY(), map.getTileSize(), map.getTileSize());
        }
    }
}