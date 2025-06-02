import java.util.ArrayList;
import java.util.List;

public class Enemy extends Player {
    int desire;
    int targetRow;
    int targetCol;
    Map map;
    SuperMap superMap;
    KeyHandler keyHandler;

    public Enemy(Map map, int row, int col, KeyHandler keyHandler) {
        super(map, row, col, keyHandler);
        this.desire = 0; // Initialize desire to 0
        this.targetRow = row; // Set targetRow to current row
        this.targetCol = col; // Set targetCol to current column
        this.map = map; // Store the map reference
        this.superMap = new SuperMap(map); // Initialize SuperMap with the current map
        this.keyHandler = keyHandler; // Initialize keyHandler
    }
    // Weighting methods for actions
    public void setDesire(int desire) {
        this.desire = desire; // Set the desire value
    }
    public void setTarget(int row, int col) {
        this.targetRow = row; // Set the target row
        this.targetCol = col; // Set the target column
    }
    public void setTarget(Coordinate target) {
        this.targetRow = target.getRow(); // Set the target row from Coordinate
        this.targetCol = target.getCol(); // Set the target column from Coordinate
    }
    public void incRow(int inc) {
    this.setRow(this.getRow()+inc);
    }
    public void incCol(int inc) {
    this.setCol(this.getCol()+inc);
    }
    // Movement methods
    public void takeAction() {
        if (isAlive()) {
            superMap.makePrediction();
        if (canPlaceBomb() && worthPlacingBomb()) {
            System.out.println("Enemy places a bomb at (" + getRow() + ", " + getCol() + ")");
            map.addBomb(getRow(), getCol()); // Place a bomb at the enemy's current position
            superMap.makePrediction(); // Update predictions after placing the bomb
        } else {
            System.out.println("Enemy cannot place a bomb safely, moving instead");
        }
        setTarget();
        if (inDanger(map)) {
            //System.out.println("Enemy is in danger, trying to escape");
            escape(); // Try to escape if in danger
        } else if (isTrapped(map, getRow(), getCol())) {
            System.out.println("Enemy is trapped, trying to find a way out");
            escape(); // Try to escape if trapped
        } else {
            //System.out.println("Enemy is safe, moving towards target");
        }
        pathFind();
    } else {
        System.out.println("Enemy is dead, womp womp");
    }
    System.out.println("");
}
    public void pathFind() {
    if (this.getRow() < targetRow && canMoveDown(map)) {
        this.incRow(1); // Move down
    } else if (this.getRow() > targetRow && canMoveUp(map)) {
        this.incRow(-1); // Move up
    } else if (this.getCol() < targetCol && canMoveRight(map)) {
        this.incCol(1); // Move right
    } else if (this.getCol() > targetCol && canMoveLeft(map)) {
        this.incCol(-1); // Move left
    }
}
    public boolean danger(Map map, int row, int col) {
        if (map.getTile(row, col) instanceof BombFire|| map.getTile(row, col) instanceof Bomb) {
            return true;
    }
    else return false;
    }
    public boolean inDanger(Map map) {
        if (map.getTile(this.getRow(), this.getCol()) instanceof BombFire|| map.getTile(this.getRow(), this.getCol()) instanceof Bomb) {
            return true;
        }
    else return false;
    }
    public List<Coordinate> viableMoves(Map map, int row, int col) {
        List<Coordinate> moves = new ArrayList<>();
        
        if (canMoveUpSafe(map, row, col))    moves.add(new Coordinate(row - 1, col));
        if (canMoveDownSafe(map, row, col))  moves.add(new Coordinate(row + 1, col));
        if (canMoveLeftSafe(map, row, col))  moves.add(new Coordinate(row, col - 1));
        if (canMoveRightSafe(map, row, col)) moves.add(new Coordinate(row, col + 1));
        for (Coordinate move : moves) {
            //System.out.println("Enemy can move to: (" + move.getRow() + ", " + move.getCol() + ")");
        }
        return moves; // Return the list of coordinates for possible moves
    }
    public boolean isTrapped(Map map, int row, int col) {
            // Check if the enemy is surrounded by walls or fire
            return !canMoveUpSafe(map, row, col) && !canMoveDownSafe(map, row, col) &&
                   !canMoveLeftSafe(map, row, col) && !canMoveRightSafe(map, row, col);
    }
    public boolean needToMove() {
        // Check if the enemy is not in danger and not trapped
        superMap.makePrediction(); // Update predictions
        List<Map> prediction = superMap.getPrediction();
        for (Map predictedMap : prediction) {
            if (inDanger(predictedMap)) {
                return true; // Enemy is in danger at some point in the future 
            }
    }
    return false; // Enemy is not in danger
    }
   public int needToMove(SuperMap superMap, int ticks, Coordinate target) {
    superMap.makePrediction(); // Ensure predictions are up to date
    List<Map> prediction = superMap.getPrediction();
    for (int i = ticks; i < prediction.size(); i++) {
        if (danger(prediction.get(i), target.getRow(), target.getCol())) {
            //System.out.println("Enemy needs to move to avoid danger at tick " + i);
            return i; // Enemy is in danger at some point in the future 
        }
    }
    return -1; // Enemy is not in danger
}
    public int needToMove(int ticks, Coordinate target) {
        // Check if the enemy is not in danger and not trapped
        superMap.makePrediction(); // Update predictions
        List<Map> prediction = superMap.getPrediction();
        for (int i = ticks; i < prediction.size(); i++) {
            if (danger(prediction.get(i), target.getRow(), target.getCol())) {
                //System.out.println("Enemy needs to move to avoid danger at tick " + i);
                return i; // Enemy is in danger at some point in the future 
            }
    }
    return -1; // Enemy is not in danger
    }
    public int needToMoveBy() {
        superMap.makePrediction(); // Update predictions
        List<Map> prediction = superMap.getPrediction();
        for (int i = 0; i < prediction.size(); i++) {
            Map predictedMap = prediction.get(i);
            Map priorMap = (i > 0) ? prediction.get(i - 1) : null;
            if (inDanger(predictedMap)) {
                //System.out.println("Enemy needs to move by " + i + " ticks to avoid danger");
                return i; // Return the number of ticks needed to avoid danger
            }
        }
        return -1; // Return -1 if no danger is detected in the predictions
    }
    public boolean canEscape() {
        // Check if the enemy can escape by moving to a safe tile
        superMap.makePrediction(); // Update predictions
        List<Map> prediction = superMap.getPrediction();
        for (Map predictedMap : prediction) {
            if (!isTrapped(predictedMap, getRow(), getCol()) && !inDanger(predictedMap)) {
                return true; // Enemy can escape to a safe tile
            }
        }
        return false; // Enemy cannot escape
    }
    public boolean canEscape(int ticks, Coordinate target) {
        // Check if the enemy can escape by moving to a safe tile
        superMap.makePrediction(); // Update predictions
        List<Map> prediction = superMap.getPrediction();
        for (int i = ticks; i < prediction.size(); i++) {
            if (!isTrapped(prediction.get(i), target.getRow(), target.getCol()) && !danger(prediction.get(i), target.getRow(), target.getCol())) {
                return true; // Enemy can escape to a safe tile
            }
        }
        return false; // Enemy cannot escape
    }   
    public void escape() {
            if (canEscape()) {
            int currentTick = 0;
           setTarget(randomBestMove(currentTick, getRow(), getCol())); // Choose a safe tile
            }
            else {
                System.out.println("There is no escape, womp womp");
                setTarget(getRow(), getCol());// Stay in place if no escape is possible
            }
        }    
   public boolean hasBestMove(int currentTick, int row, int col) {
    List<Coordinate> moves = viableMoves(superMap.getPrediction(currentTick+1), row, col);
            List<Coordinate> bestMoves = new ArrayList<Coordinate>();
            for (int i = moves.size()-1; i>=0; i--) {
                if (needToMove(currentTick + 1, moves.get(i)) == -1) {
                   return true;
                }
            }
            return false; // No completely safe moves available
    }
    public boolean hasBestMove(SuperMap superMap, int currentTick, int row, int col) {
    Map predictedMap = superMap.getPrediction(currentTick + 1);
    if (predictedMap == null) return false;
    List<Coordinate> moves = viableMoves(predictedMap, row, col);
    for (Coordinate move : moves) {
        if (needToMove(superMap, currentTick + 1, move) == -1) {
            return true;
        }
    }
    return false;
}
    public boolean isBestMove(int currentTick, Coordinate move) {
        if (needToMove(currentTick + 1, move) == -1) {
            return true; // The move leads to complete safety
            }
            return false; // Not a best move
    }
    public boolean hasEventualBestMove(int currentTick, int row, int col) {
        if (currentTick >= superMap.getPrediction().size() - 1) {
            return false; // No future predictions available
        }
        List<Coordinate> moves = viableMoves(superMap.getPrediction(currentTick+1), row, col);
            for (int i = moves.size()-1; i>=0; i--) {
                 if (hasBestMove(currentTick + 1, moves.get(i).getRow(), moves.get(i).getCol())) {
                    return true; // There is a move that leads to full safety in the future
                }
            }
            for (Coordinate move : moves) {
                if (hasEventualBestMove(currentTick + 1, move.getRow(), move.getCol())) {
                    //System.out.println("Enemy can escape from " + move + " at tick " + (currentTick + 2));
                    return true; // There is a move that leads to full safety in the future
                }
            }
            return false; // No eventual best move available
    }
    public boolean hasEventualBestMove(SuperMap superMap, int currentTick, int row, int col) {
    if (currentTick >= superMap.getPrediction().size() - 1) {
        return false; // No future predictions available
    }
    List<Coordinate> moves = viableMoves(superMap.getPrediction(currentTick + 1), row, col);
    for (int i = moves.size() - 1; i >= 0; i--) {
        if (hasBestMove(superMap, currentTick + 1, moves.get(i).getRow(), moves.get(i).getCol())) {
            return true; // There is a move that leads to full safety in the future
        }
    }
    for (Coordinate move : moves) {
        if (hasEventualBestMove(superMap, currentTick + 1, move.getRow(), move.getCol())) {
            //System.out.println("Enemy can escape from " + move + " at tick " + (currentTick + 2));
            return true; // There is a move that leads to full safety in the future
        }
    }
    return false; // No eventual best move available
}
    public void eventualBestMove(List<Coordinate> moves, List<Coordinate> bestMoves, int currentTick, int row, int col) {
    for (Coordinate move : moves) {
        if (isBestMove(currentTick, move)) {
            bestMoves.add(move); // Add moves that lead to complete safety
            System.out.println("best move found");
        }
    }
    if (bestMoves.size() < 1) {
        System.out.println("No best move found, regressing");
        for (int i = 0; i < moves.size(); i++) {
            // Recurse with the move's coordinates and incremented tick
            System.out.println("checking move "+(i));
            List<Coordinate> nextMoves = viableMoves(superMap.getPrediction(currentTick + 1), moves.get(i).getRow(), moves.get(i).getCol());
            eventualBestMove(nextMoves, bestMoves, currentTick + 1, moves.get(i).getRow(), moves.get(i).getCol());
        }
    }
}
    public Coordinate randomBestMove(int currentTick, int row, int col) {
        List<Coordinate> moves = viableMoves(superMap.getPrediction(currentTick), row, col);
        List<Coordinate> bestMoves = new ArrayList<Coordinate>();
        eventualBestMove(moves, bestMoves, currentTick, row, col);
        if (bestMoves.size() < 1) {
            //System.out.println("Enemy has no best moves available at tick " + currentTick);
            return new Coordinate(getRow(), getCol()); // Stay in place if no best moves are available
        }
        else {
            System.out.println("Enemy has " + bestMoves.size() + " best moves available at tick " + currentTick);
            int choice = (int) (Math.random() * bestMoves.size());
            Coordinate move = bestMoves.get(choice);
            return move; // Return a random best move
        }
    }
        //public Coordinate returnSafestRandomMove()
    public void setTarget() {
        if (needToMove()) {
            escape(); // Try to escape if in danger or trapped
        } else {
            setTarget(randomBestMove(0,getRow(), getCol())); // Move towards the target if not in danger
        }
    }
    public boolean canPlaceBomb() {
        if (isTrapped(map, getRow(), getCol())) {
            System.out.println("Enemy is trapped, should not place a bomb at (" + getRow() + ", " + getCol() + ")");
            return false; // Enemy is trapped, cannot place a bomb
        }
        if (couldEscapeBomb()) {
            System.out.println("Enemy can place a bomb safely at (" + getRow() + ", " + getCol() + ")");
            return true; // Enemy can place a bomb safely
        } else {
            System.out.println("Enemy cannot place a bomb safely at (" + getRow() + ", " + getCol() + ")");
            return false; // Enemy cannot place a bomb safely
    }
    }
//Lets try to handle bombs!!!
public boolean couldEscapeBomb() {
    Map bombMap = new Map(map);
    bombMap.addBomb(getRow(), getCol()); // Add a bomb at the enemy's current position
    SuperMap bombPrediction = new SuperMap(bombMap); // Create a SuperMap for bomb predictions
    bombPrediction.getMainMap().addBomb(getRow(), getCol()); // Add a bomb at the enemy's current position
    bombPrediction.makePrediction(); // Make predictions based on the bomb placement
    List<Map> bombPredictions = bombPrediction.getPrediction();
    Coordinate enemyPosition = new Coordinate(getRow(), getCol());
    if (hasEventualBestMove(bombPrediction, 0, enemyPosition.getRow(), enemyPosition.getCol())) {
        System.out.println("Enemy can escape the bomb at (" + getRow() + ", " + getCol() + ")");
        return true; // Enemy can escape the bomb
    } else {
        System.out.println("Enemy cannot escape the bomb at (" + getRow() + ", " + getCol() + ")");
        return false; // Enemy cannot escape the bomb
    }
}
public boolean worthPlacingBomb() {
    int power = 2; // Default power
    Tile tile = map.getTile(getRow(), getCol());
    if (tile instanceof Bomb) {
        power = ((Bomb) tile).getPower();
    } else if (map.getBombList().size() > 0) {
        // Try to get power from the most recent bomb if needed
        power = map.getBombList().get(map.getBombList().size() - 1).getPower();
    }
    int wallsBroken = 0;

    // Check in all four directions
    int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };
    for (int[] dir : directions) {
        for (int i = 1; i <= power; i++) {
            int r = getRow() + dir[0] * i;
            int c = getCol() + dir[1] * i;
            Tile t = map.getTile(r, c);
            if (t instanceof HardWall) break; // Stop at hard wall
            if (t instanceof SoftWall) {
                wallsBroken++;
                break; // Stop after breaking the first soft wall in this direction
            }
        }
    }

    if (wallsBroken > 0) {
        System.out.println("Enemy can break " + wallsBroken + " soft wall(s) at (" + getRow() + ", " + getCol() + ") with bomb power " + power);
        return true;
    } else {
        System.out.println("Enemy cannot break any soft walls at (" + getRow() + ", " + getCol() + ") with bomb power " + power);
        return false;
    }
}
//More advanced logic (Avoiding dead ends, etc.)



}    // This class represents an enemy in the game, extending the Player class.
