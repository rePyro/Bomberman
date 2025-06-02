import java.util.*;

public class Enemy extends Player {
    int targetRow; int overallTargetRow;
    int targetCol; int overallTargetCol;
    Map map;
    SuperMap superMap;
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

    private Coordinate breakingWallTarget = null;
    private int breakTileAttempts = 0;
    private static final int MAX_BREAK_TILE_ATTEMPTS = 5;

    private int goodBombTargetCooldown = 0;
    private List<Coordinate> cachedGoodBombTargets = new ArrayList<>();

    private int escapeCooldown = 0;

    private int failedEscapeAttempts = 0;
    private static final int MAX_FAILED_ESCAPE_ATTEMPTS = 20;

    private Coordinate lastRandomMove = null; // Add this to your Enemy class fields

    public Enemy(Map map, int row, int col) {
        super(map, row, col);
        this.targetRow = row;
        this.targetCol = col;
        this.map = map;
        this.superMap = new SuperMap(map);
        superMap.makePrediction();
        this.setSpeed(5);
        this.setOverallTarget(-1, -1); // Default overall target to bottom-right corner
        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        // Use cautious map for initial path
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        SuperMap cautiousSuperMap = new SuperMap(cautiousMap);
        cautiousSuperMap.makePrediction();
        this.currentPath = weightedSafePathFindTo(cautiousSuperMap, getRow(), getCol(), overallTargetRow, overallTargetCol, 0);
        System.out.println(this.currentPath);
    }

    public void setTarget(int row, int col) { this.targetRow = row; this.targetCol = col; }
    public void setTarget(Coordinate target) { this.targetRow = target.getRow(); this.targetCol = target.getCol(); }
    public void setOverallTarget(int row, int col) { this.overallTargetRow = row; this.overallTargetCol = col; }

    public int needToMove(SuperMap superMap, int ticks, Coordinate target) {
        superMap.makePrediction();
        List<Map> prediction = superMap.getPrediction();
        for (int i = ticks; i < prediction.size(); i++) {
            if (danger(prediction.get(i), target.getRow(), target.getCol())) {
                return i;
            }
        }
        return -1;
    }
    public boolean danger(Map map, int row, int col) {
        if (map.getTile(row, col) instanceof BombFire) {
            return true;
        }
        else return false;
    }

    // taking actions
    public void takeAction() {
        System.out.println("Enemy taking action at (" + getRow() + ", " + getCol() + ")");
        if (!isAlive()) return;

        // 1. Escaping bomb logic
        if (escapingBomb) {
            System.out.println("Currently escaping bomb, returned take action");
            setTarget();
            return;
        }

        // --- Wall breaking intent tracking ---
        System.out.println("wallBreakCheck");
        if (breakingWallTarget != null) {
            Tile t = map.getTile(breakingWallTarget.getRow(), breakingWallTarget.getCol());
            if (!(t instanceof SoftWall)) {
                System.out.println("Wall at (" + breakingWallTarget.getRow() + ", " + breakingWallTarget.getCol() + ") is gone!");
                breakingWallTarget = null;
            } else {
                breakTileAt(breakingWallTarget.getRow(), breakingWallTarget.getCol());
                return;
            }
        }

        // 2. Pathfinding to overall target
        System.out.println("got to stage 2");
        if (overallTargetRow >= 0 && overallTargetCol >= 0) {
            // Use cautious map for pathfinding
            Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
            SuperMap cautiousSuperMap = new SuperMap(cautiousMap);
            cautiousSuperMap.makePrediction();

            // If no path or finished path, recalculate
            if (currentPath.isEmpty() || pathIndex >= currentPath.size()) {
                currentPath = weightedSafePathFindTo(cautiousSuperMap, getRow(), getCol(), overallTargetRow, overallTargetCol, fireFuseAdd);
                pathIndex = 0;
            }

            // If path exists, check next step for soft wall
            if (!currentPath.isEmpty() && pathIndex < currentPath.size()) {
                Coordinate nextStep = currentPath.get(pathIndex);
                Tile nextTile = map.getTile(nextStep.getRow(), nextStep.getCol());
                if (nextTile instanceof SoftWall) {
                    System.out.println("Next step is a SoftWall at (" + nextStep.getRow() + ", " + nextStep.getCol() + "), breaking it.");
                    breakingWallTarget = nextStep;
                    breakTileAt(nextStep.getRow(), nextStep.getCol());
                    return; // After breakTileAt, escapingBomb will be set and handled next tick
                } else {
                    setTarget(nextStep.getRow(), nextStep.getCol());
                    // Continue following the path in pathFind()
                    // Check if we've reached the overall target
                    if (getRow() == overallTargetRow && getCol() == overallTargetCol) {
                        System.out.println("Enemy reached overall target at (" + overallTargetRow + ", " + overallTargetCol + "), clearing overall target.");
                        overallTargetRow = -1;
                        overallTargetCol = -1;
                        currentPath.clear();
                        pathIndex = 0;
                    }
                    return;
                }
            }
        }

        // 3. No overall target: try to place a bomb at current location if possible
        System.out.println("got to stage 3");
        if (couldEscapeBomb() && worthPlacingBomb()) {
            System.out.println("Enemy places a bomb at (" + getRow() + ", " + getCol() + ")");
            map.addBomb(getRow(), getCol());
            superMap.makePrediction();
            escapingBomb = true;
            setTarget();
            return;
        }
        System.out.println("got to stage 4");
        // 4. Pathfind to closest good bomb location
        Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
        SuperMap cautiousSuperMap = new SuperMap(cautiousMap);
        cautiousSuperMap.makePrediction();
        Coordinate bombTarget = findClosestGoodBombTarget(cautiousSuperMap);
        System.out.print("hang isn't findClosestGoodBomb");
        if (bombTarget != null) {
            List<Coordinate> path = pathFindToSpaceWithPath(cautiousSuperMap, getRow(), getCol(), bombTarget.getRow(), bombTarget.getCol());
            if (!path.isEmpty()) {
                currentPath = path;
                pathIndex = 1;
                if (currentPath.size() > 1) {
                    Coordinate nextStep = currentPath.get(pathIndex);
                    setTarget(nextStep.getRow(), nextStep.getCol());
                    return;
                }
            }
        }

        // 5. Move randomly if nothing else
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        List<Coordinate> safeAdj = new ArrayList<>();
        List<Map> predictions = cautiousSuperMap.getPrediction();
        for (int[] dir : dirs) {
            int r = getRow() + dir[0];
            int c = getCol() + dir[1];
            if (!canMoveTo(map, r, c)) continue;
            // Prevent stepping into BombFire right now
            if (map.getTile(r, c) instanceof BombFire) continue;
            boolean safe = true;
            for (int t = 0; t < predictions.size(); t++) {
                Map futureMap = predictions.get(t);
                if (futureMap == null || danger(futureMap, r, c)) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                safeAdj.add(new Coordinate(r, c));
            }
        }
        if (!safeAdj.isEmpty()) {
            Coordinate target = safeAdj.get(rand.nextInt(safeAdj.size()));
            setTarget(target.getRow(), target.getCol());
            currentPath = new ArrayList<>();
            pathIndex = 0;
            randomTargetCooldown = RANDOM_TARGET_COOLDOWN_TICKS;
        } else {
            setTarget(getRow(), getCol());
        }
    }

    public void enemyTick() {
    // Debug: Output current position
    //System.out.println("");
    //System.out.println("Enemy position: (" + getRow() + ", " + getCol() + ")");

    if (currentTick == ticksPerSecond) {
        currentTick = 0;
        System.out.println("taking action");
        takeAction();
    } else {
        currentTick++;
    }
    //System.out.println(currentTick);
    if (escapingBomb) {
        if (escapeCooldown <= 0) {
            setTarget();
            escapeCooldown = 5;
        } else {
            escapeCooldown--;
        }
        if (isSafeNow()) {
            escapingBomb = false;
            currentPath.clear();
            pathIndex = 0;
            escapeCooldown = 0;
        }
    } else if (getY() == rowToY(targetRow) && getX() == colToX(targetCol)) {
        setTarget();
    }
    pathFind();
    //System.out.println("About to check if enemy can place a bomb at (" + getRow() + ", " + getCol() + ")");
    //System.out.println(couldEscapeBomb());
    //System.out.println(worthPlacingBomb());
    //System.out.println("");
    if (!escapingBomb && currentPath.isEmpty() && couldEscapeBomb() && worthPlacingBomb()) {
        System.out.println("Enemy places a bomb at (" + getRow() + ", " + getCol() + ") [immediate check]");
        map.addBomb(getRow(), getCol());
        superMap.makePrediction();
        escapingBomb = true;
        setTarget();
    }
    // Always check if the current tile is dangerous
    if (danger(map, getRow(), getCol())) {
        escapingBomb = true;
        setTarget();
    }
}
    private boolean isSafeNow() {
    // Check current and next few ticks for danger
    List<Map> predictions = superMap.getPrediction();
    int row = getRow(), col = getCol();
    for (int t = 0; t < Math.min(5, predictions.size()); t++) {
        if (danger(predictions.get(t), row, col)) return false;
    }
    return true;
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
            Map cautiousMap = map.copyWithModifiedFuses(bombFuseMultiplier, fireFuseAdd);
            cautiousMap.addBomb(getRow(), getCol());
            SuperMap bombPrediction = new SuperMap(cautiousMap);
            bombPrediction.makePrediction();

            EscapeResult best = bestEscapePath(bombPrediction, getRow(), getCol());
            if (best != null && best.path.size() > 1) {
                currentPath = best.path;
                pathIndex = 1;
                Coordinate nextStep = currentPath.get(pathIndex);
                setTarget(nextStep.getRow(), nextStep.getCol());
                System.out.println("FoundEscape");
                failedEscapeAttempts = 0; // Reset on success
            } else {
                failedEscapeAttempts++;
                System.out.println("No escape path found, attempt " + failedEscapeAttempts);
                if (failedEscapeAttempts > MAX_FAILED_ESCAPE_ATTEMPTS) {
                    System.out.println("Giving up on escaping after too many failed attempts.");
                    escapingBomb = false;
                    failedEscapeAttempts = 0;
                }
            }
            return;
        } else if (currentPath.isEmpty()) {
            // Only search for good bomb targets every 20 ticks
            if (goodBombTargetCooldown <= 0) {
                System.out.println("Enemy is not escaping a bomb or going to overall target, finding minor targets");
                cachedGoodBombTargets = findAllGoodBombTargets(superMap);
                goodBombTargetCooldown = 240;
            } else {
                goodBombTargetCooldown--;
            }
            List<Coordinate> goodTargets = cachedGoodBombTargets;
            if (!goodTargets.isEmpty()) {
                Coordinate target = goodTargets.get(0);
                int minDist = Math.abs(getRow() - target.getRow()) + Math.abs(getCol() - target.getCol());
                for (Coordinate t : goodTargets) {
                    int dist = Math.abs(getRow() - t.getRow()) + Math.abs(getCol() - t.getCol());
                    if (dist < minDist) {
                        target = t;
                        minDist = dist;
                    }
                }
                List<Coordinate> path = pathFindToSpaceWithPath(superMap, getRow(), getCol(), target.getRow(), target.getCol());
                if (!path.isEmpty()) {
                    currentPath = path;
                    pathIndex = 1;
                    if (currentPath.size() > 1) {
                        Coordinate nextStep = currentPath.get(pathIndex);
                        setTarget(nextStep.getRow(), nextStep.getCol());
                        return;
                    }
                }
            }
            if (randomTargetCooldown > 0) {
                randomTargetCooldown--;
                return;
            }
            System.out.println("Setting random target");
            int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
            List<Coordinate> safeAdj = new ArrayList<>();
            List<Map> predictions = superMap.getPrediction();
            for (int[] dir : dirs) {
                int r = getRow() + dir[0];
                int c = getCol() + dir[1];
                if (!canMoveTo(map, r, c)) continue;
                // Prevent stepping into BombFire right now
                if (map.getTile(r, c) instanceof BombFire) continue;
                boolean safe = true;
                for (int t = 0; t < predictions.size(); t++) {
                    Map futureMap = predictions.get(t);
                    if (futureMap == null || danger(futureMap, r, c)) {
                        safe = false;
                        break;
                    }
                }
                if (safe) {
                    safeAdj.add(new Coordinate(r, c));
                    System.out.println("DEBUG: addedSafe");
                }
            }
            if (!safeAdj.isEmpty()) {
                Coordinate target = safeAdj.get(rand.nextInt(safeAdj.size()));
                setTarget(target.getRow(), target.getCol());
                currentPath = new ArrayList<>();
                pathIndex = 0;
                randomTargetCooldown = RANDOM_TARGET_COOLDOWN_TICKS;
            } else {
                setTarget(getRow(), getCol());
            }
        }
    }

    // --- EscapeResult class for accurate tick tracking ---
    public static class EscapeResult {
        public final List<Coordinate> path;
        public final int mapTick;
        public EscapeResult(List<Coordinate> path, int mapTick) {
            this.path = path;
            this.mapTick = mapTick;
        }
    }

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

    public int rowToY(int row) { return (int)((row)*map.getTileSize()); }
    public int colToX(int col) { return (int)((col)*map.getTileSize()); }

    public List<Coordinate> findEscapePath(SuperMap superMap) {
        return findEscapePath(superMap, getRow(), getCol());
    }

    public List<Coordinate> findEscapePath(SuperMap superMap, int startRow, int startCol) {
        return findEscapePathResult(superMap, startRow, startCol).path;
    }

    // --- New: findEscapePathResult returns both path and mapTick ---
    public EscapeResult findEscapePathResult(SuperMap superMap, int startRow, int startCol) {
        List<Map> predictions = superMap.getPrediction();
        int bombFuseMapTicks = (int)Math.ceil((double)map.getBombFuse() / ticksPerSecond);

        Queue<List<Coordinate>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<Coordinate> startPath = new ArrayList<>();
        startPath.add(new Coordinate(startRow, startCol));
        queue.add(startPath);
        visited.add(startRow + "," + startCol);

        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        while (!queue.isEmpty()) {
            List<Coordinate> path = queue.poll();
            Coordinate last = path.get(path.size() - 1);
            int row = last.getRow();
            int col = last.getCol();

            // Only check the destination tile for all future ticks after arrival
            boolean safe = true;
            for (int t = path.size() - 1; t < predictions.size(); t++) {
                Map fm = predictions.get(t);
                if (fm == null) {
                    //System.out.println("Prediction map is null at tick " + t + " for (" + row + "," + col + ")");
                    safe = false;
                    break;
                }
                if (danger(fm, row, col)) {
                    //System.out.println("Tile (" + row + "," + col + ") is dangerous at tick " + t);
                    safe = false;
                    break;
                }
            }
            if (safe) {
                //System.out.println("Tile (" + row + "," + col + ") is safe for ALL future ticks from " + (path.size() - 1) + " onward");
                boolean isStart = (row == startRow && col == startCol);
                if (!isStart || (path.size() == 1 && !(superMap.getPrediction().get(0).getTile(row, col) instanceof Bomb))) {
                    //System.out.println("CHOSEN SAFE ESCAPE PATH: " + path);
                    //System.out.println("Enemy CAN escape to (" + row + "," + col + ") in " + (path.size() - 1) + " ticks!");
                    return new EscapeResult(path, path.size() - 1);
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
                if (!canMoveTo(predictions.get(Math.min(path.size(), predictions.size() - 1)), nrow, ncol, isStart)) continue;
                if (danger(predictions.get(Math.min(path.size(), predictions.size() - 1)), nrow, ncol)) continue;

                List<Coordinate> newPath = new ArrayList<>(path);
                newPath.add(new Coordinate(nrow, ncol));
                queue.add(newPath);
                visited.add(key);
            }
        }
        //System.out.println("Enemy can't get to a safe tile from (" + startRow + ", " + startCol + ") before bomb explodes.");
        List<Coordinate> stay = new ArrayList<>();
        stay.add(new Coordinate(startRow, startCol));
        return new EscapeResult(stay, 0);
    }

    public List<EscapeResult> findAllEscapePaths(SuperMap superMap, int startRow, int startCol) {
        List<Map> predictions = superMap.getPrediction();
        int bombFuseMapTicks = (int)Math.ceil((double)map.getBombFuse() / ticksPerSecond);

        Queue<List<Coordinate>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<Coordinate> startPath = new ArrayList<>();
        startPath.add(new Coordinate(startRow, startCol));
        queue.add(startPath);
        visited.add(startRow + "," + startCol);

        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        List<EscapeResult> validPaths = new ArrayList<>();

        while (!queue.isEmpty()) {
            List<Coordinate> path = queue.poll();
            Coordinate last = path.get(path.size() - 1);
            int row = last.getRow();
            int col = last.getCol();

            // Only check the destination tile for all future ticks after arrival
            boolean safe = true;
            for (int t = path.size() - 1; t < predictions.size(); t++) {
                Map fm = predictions.get(t);
                if (fm == null || danger(fm, row, col)) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                boolean isStart = (row == startRow && col == startCol);
                if (!isStart || (path.size() == 1 && !(superMap.getPrediction().get(0).getTile(row, col) instanceof Bomb))) {
                    validPaths.add(new EscapeResult(path, path.size() - 1));
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
                if (!canMoveTo(predictions.get(Math.min(path.size(), predictions.size() - 1)), nrow, ncol, isStart)) continue;
                if (danger(predictions.get(Math.min(path.size(), predictions.size() - 1)), nrow, ncol)) continue;

                List<Coordinate> newPath = new ArrayList<>(path);
                newPath.add(new Coordinate(nrow, ncol));
                queue.add(newPath);
                visited.add(key);
            }
        }
        return validPaths;
    }

    public EscapeResult bestEscapePath(SuperMap superMap, int startRow, int startCol) {
        //System.out.println("Finding best escape path from (" + startRow + ", " + startCol + ")");
        List<EscapeResult> allPaths = findAllEscapePaths(superMap, startRow, startCol);
        if (allPaths.isEmpty()) return null;

        // Find the minimum path length
        int minLen = Integer.MAX_VALUE;
        for (EscapeResult er : allPaths) {
            if (er.path.size() < minLen) minLen = er.path.size();
        }

        // Collect all shortest paths
        List<EscapeResult> shortest = new ArrayList<>();
        for (EscapeResult er : allPaths) {
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

    public boolean pathFindToSpace(SuperMap superMap, int startRow, int startCol, int endRow, int endCol) {
        List<Map> predictions = superMap.getPrediction();
        int maxTick = Math.min(20, predictions.size() - 1);

        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new int[]{startRow, startCol, 0});
        visited.add(startRow + "," + startCol + ",0");

        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };

        while (!queue.isEmpty()) {
            int[] state = queue.poll();
            int row = state[0];
            int col = state[1];
            int tick = state[2];

            if (row == endRow && col == endCol) {
                return true;
            }
            // if (tick >= maxTick) continue; // Removed to allow search to continue past maxTick

            int nextTick = tick + 1;
            Map nextMap = predictions.get(Math.min(nextTick, maxTick));

            for (int[] dir : dirs) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String key = newRow + "," + newCol + "," + nextTick;
                if (newRow < 0 || newRow >= map.getHeight() || newCol < 0 || newCol >= map.getWidth()) continue;
                if (visited.contains(key)) continue;
                if (!canMoveTo(nextMap, newRow, newCol)) continue;
                if (danger(nextMap, newRow, newCol)) continue;

                queue.add(new int[]{newRow, newCol, nextTick});
                visited.add(key);
            }
        }
        return false;
    }

    public List<Coordinate> pathFindToSpaceWithPath(SuperMap superMap, int startRow, int startCol, int endRow, int endCol) {
        List<Map> predictions = superMap.getPrediction();
        int maxTick = Math.min(10, predictions.size() - 1);
        System.out.println("running PathfindToSpaceWith");
        Queue<List<int[]>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        List<int[]> startPath = new ArrayList<>();
        startPath.add(new int[]{startRow, startCol, 0});
        queue.add(startPath);
        visited.add(startRow + "," + startCol + ",0");

        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        final int MAX_Path_CHECKS = 100; // Limit to prevent hanging
        final int MAX_BFS_DEPTH = 30; // BFS depth limit
        while (!queue.isEmpty()) {
            //System.out.println("check");
            List<int[]> path = queue.poll();
            int[] state = path.get(path.size() - 1);
            int row = state[0];
            int col = state[1];
            int tick = state[2];

            if (tick > MAX_BFS_DEPTH) continue;

            
            if (row == endRow && col == endCol) {
                List<Coordinate> coordPath = new ArrayList<>();
                for (int[] step : path) {
                    coordPath.add(new Coordinate(step[0], step[1]));
                }
                System.out.println("returned coordPath");
                return coordPath;
            }
            // if (tick >= maxTick) continue; // Removed to allow search to continue past maxTick

            int nextTick = tick + 1;
            Map nextMap;
            if (nextTick < predictions.size()) {
                nextMap = predictions.get(nextTick);
            } else {
                nextMap = predictions.get(predictions.size() - 1); // Use last map if out of bounds
            }

            for (int[] dir : dirs) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String key = newRow + "," + newCol + "," + nextTick;
                if (newRow < 0 || newRow >= map.getHeight() || newCol < 0 || newCol >= map.getWidth()) continue;
                if (visited.contains(key)) continue;
                if (!canMoveTo(nextMap, newRow, newCol)) continue;
                if (danger(nextMap, newRow, newCol)) continue;

                List<int[]> newPath = new ArrayList<>(path);
                newPath.add(new int[]{newRow, newCol, nextTick});
                queue.add(newPath);
                visited.add(key);
            }
        }
        return new ArrayList<>();
    }

    public List<Coordinate> weightedSafePathFindTo(SuperMap superMap, int startRow, int startCol, int endRow, int endCol, int breakablePenalty) {
            if (startRow < 0 || startCol < 0 || endRow < 0 || endCol < 0) {
                return new ArrayList<>(); // Invalid coordinates
            }
        class Node implements Comparable<Node> {
            int row, col, tick, cost;
            List<Coordinate> path;
            Node(int row, int col, int tick, int cost, List<Coordinate> path) {
                this.row = row; this.col = col; this.tick = tick; this.cost = cost; this.path = path;
            }
            public int compareTo(Node o) { return Integer.compare(this.cost, o.cost); }
        }

        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        List<Coordinate> startPath = new ArrayList<>();
        startPath.add(new Coordinate(startRow, startCol));
        queue.add(new Node(startRow, startCol, 0, 0, startPath));
        visited.add(startRow + "," + startCol + ",0");

        List<Map> predictions = superMap.getPrediction();
        int maxTicks = 30; // <-- Tick cap

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            int row = node.row, col = node.col, tick = node.tick, cost = node.cost;
            List<Coordinate> path = node.path;

            if (tick > maxTicks) {
                System.out.println("Skipping node at (" + row + "," + col + ") tick " + tick + " due to tick cap.");
                continue;
            }
            if (row == endRow && col == endCol) {
                System.out.println("Found path to (" + endRow + ", " + endCol + ") with cost " + cost);
                return path;
            }

            int nextTick = tick + 1;
            Map nextMap;
            if (nextTick < predictions.size()) {
                nextMap = predictions.get(nextTick);
            } else {
                nextMap = predictions.get(predictions.size() - 1); // Use last map if out of bounds
            }

            for (int[] dir : dirs) {
                int nrow = row + dir[0];
                int ncol = col + dir[1];
                String key = nrow + "," + ncol + "," + nextTick;
                if (nrow < 0 || nrow >= map.getHeight() || ncol < 0 || ncol >= map.getWidth()) continue;
                if (visited.contains(key)) continue;

                Tile t = nextMap.getTile(nrow, ncol);
                int newCost = cost + 1;
                if (t instanceof SoftWall) {
                    newCost += breakablePenalty;
                } else if (t instanceof HardWall || t instanceof Bomb) {
                    System.out.println("Blocked by HardWall/Bomb at (" + nrow + "," + ncol + ") tick " + nextTick);
                    continue;
                }
                if (danger(nextMap, nrow, ncol)) {
                    System.out.println("Danger at (" + nrow + "," + ncol + ") tick " + nextTick);
                    continue;
                }

                List<Coordinate> newPath = new ArrayList<>(path);
                newPath.add(new Coordinate(nrow, ncol));
                queue.add(new Node(nrow, ncol, nextTick, newCost, newPath));
                visited.add(key);
            }
        }
        return new ArrayList<>(); // No path found
    }

    public List<Coordinate> findAllGoodBombTargets(SuperMap superMap) {
        System.out.println("Finding all good bomb targets for the enemy");
        List<Coordinate> goodTargets = new ArrayList<>();
        int startRow = getRow();
        int startCol = getCol();

        final int MAX_GOOD_BOMB_TARGET_CHECKS = 1000; // Limit to prevent hanging
        final int MAX_BFS_DEPTH = 25; // BFS depth limit
        int checks = 0;

        // Store both coordinate and depth in the queue
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new int[]{startRow, startCol, 0}); // row, col, depth
        visited.add(startRow + "," + startCol);

        while (!queue.isEmpty() && checks < MAX_GOOD_BOMB_TARGET_CHECKS) {
            checks++;
            int[] state = queue.poll();
            int row = state[0];
            int col = state[1];
            int depth = state[2];

            // BFS depth limit
            if (depth > MAX_BFS_DEPTH) continue;

            // Check if this tile is a good bomb target
            if (canMoveTo(map, row, col) && couldEscapeBombAt(row, col) && worthPlacingBombAt(row, col)) {
                goodTargets.add(new Coordinate(row, col));
                System.out.println("Found good bomb target at (" + row + ", " + col + ")");
            }

            // Explore neighbors
            int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
            for (int[] dir : dirs) {
                int nrow = row + dir[0];
                int ncol = col + dir[1];
                String key = nrow + "," + ncol;
                if (nrow < 0 || nrow >= map.getHeight() || ncol < 0 || ncol >= map.getWidth()) continue;
                if (visited.contains(key)) continue;
                if (!canMoveTo(map, nrow, ncol)) continue;
                queue.add(new int[]{nrow, ncol, depth + 1});
                visited.add(key);
            }
        }
        if (checks >= MAX_GOOD_BOMB_TARGET_CHECKS) {
            System.out.println("Stopped searching for good bomb targets after reaching the check limit.");
        }
        return goodTargets;
    }

    private Coordinate findClosestGoodBombTarget(SuperMap superMap) {
        int minDist = Integer.MAX_VALUE;
        Coordinate bestTarget = null;
        int startRow = getRow();
        int startCol = getCol();

        // Scan the map for all tiles
        for (int r = 0; r < map.getHeight(); r++) {
            for (int c = 0; c < map.getWidth(); c++) {
                if (!canMoveTo(map, r, c)) continue;
                if (!worthPlacingBombAt(r, c)) continue;
                if (!couldEscapeBombAt(r, c)) continue;
                System.out.println("got through initial checks");
                // Check if path exists
                List<Coordinate> path = pathFindToSpaceWithPath(superMap, startRow, startCol, r, c);
                System.out.println("got through PathFindTo");
                if (!path.isEmpty()) {
                    int dist = path.size();
                    if (dist < minDist) {
                        minDist = dist;
                        bestTarget = new Coordinate(r, c);
                    }
                }
            }
        }
        return bestTarget;
    }

    public boolean breakTileAt(int targetRow, int targetCol) {
        System.out.println("Enemy is trying to break tile at (" + targetRow + ", " + targetCol + ")");
        Tile targetTile = map.getTile(targetRow, targetCol);
        if (!(targetTile instanceof SoftWall)) {
            System.out.println("Target tile is no longer a SoftWall, aborting breakTileAt.");
            breakingWallTarget = null;
            breakTileAttempts = 0;
            return false;
        }

        // Limit attempts to prevent hanging
        breakTileAttempts++;
        if (breakTileAttempts > MAX_BREAK_TILE_ATTEMPTS) {
            System.out.println("Too many attempts to break (" + targetRow + ", " + targetCol + "), aborting.");
            breakingWallTarget = null;
            breakTileAttempts = 0;
            return false;
        }

        int startRow = getRow();
        int startCol = getCol();
        int bombPower = 2; // You may want to get this dynamically

        // Find all tiles from which a bomb would break the target tile
        List<Coordinate> bombSpots = new ArrayList<>();
        int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        for (int[] dir : dirs) {
            for (int i = 1; i <= bombPower; i++) {
                int r = targetRow + dir[0] * i;
                int c = targetCol + dir[1] * i;
                if (r < 0 || r >= map.getHeight() || c < 0 || c >= map.getWidth()) break;
                Tile t = map.getTile(r, c);
                if (t instanceof HardWall) break;
                bombSpots.add(new Coordinate(r, c));
                if (t instanceof SoftWall) break;
            }
        }

        // Find the shortest safe path to any bomb spot (using pathFindToSpaceWithPath)
        List<Coordinate> bestPath = null;
        Coordinate bestSpot = null;
        int minLen = Integer.MAX_VALUE;
        for (Coordinate spot : bombSpots) {
            if (!canMoveTo(map, spot.getRow(), spot.getCol())) continue;
            List<Coordinate> path = pathFindToSpaceWithPath(superMap, startRow, startCol, spot.getRow(), spot.getCol());
            if (!path.isEmpty() && path.size() < minLen) {
                if (couldEscapeBombAt(spot.getRow(), spot.getCol())) {
                    minLen = path.size();
                    bestPath = path;
                    bestSpot = spot;
                }
            }
        }

        if (bestPath != null && bestSpot != null) {
            currentPath = bestPath;
            pathIndex = 1;
            setTarget(bestSpot.getRow(), bestSpot.getCol());
            if (getRow() == bestSpot.getRow() && getCol() == bestSpot.getCol()) {
                // Double-check if we can place a bomb here
                if (couldEscapeBombAt(getRow(), getCol()) && worthPlacingBombAt(getRow(), getCol())) {
                    map.addBomb(getRow(), getCol());
                    System.out.println("Enemy placed a bomb at (" + getRow() + ", " + getCol() + ") to break (" + targetRow + ", " + targetCol + ")");
                    // --- Escape logic after placing bomb ---
                    superMap.makePrediction();
                    escapingBomb = true;
                    setTarget(); // This will trigger escape path logic
                    breakTileAttempts = 0; // Reset attempts after success
                } else {
                    System.out.println("Cannot place bomb at (" + getRow() + ", " + getCol() + "), aborting breakTileAt.");
                    breakingWallTarget = null;
                    breakTileAttempts = 0;
                }
            } else {
                System.out.println("Enemy is moving to (" + bestSpot.getRow() + ", " + bestSpot.getCol() + ") to break (" + targetRow + ", " + targetCol + ")");
            }
            return true;
        } else {
            System.out.println("No valid bomb spot found to break (" + targetRow + ", " + targetCol + "), aborting breakTileAt.");
            breakingWallTarget = null;
            breakTileAttempts = 0;
            return false;
        }
    }

}