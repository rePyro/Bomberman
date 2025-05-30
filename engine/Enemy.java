import java.util.ArrayList;
import java.util.List;

public class Enemy extends Player {
    int desire;
    int targetRow;
    int targetCol;
    Map map;
    SuperMap superMap;

    public Enemy(Map map, int row, int col) {
        super(map, row, col);
        this.desire = 0; // Initialize desire to 0
        this.targetRow = row; // Set targetRow to current row
        this.targetCol = col; // Set targetCol to current column
        this.map = map; // Store the map reference
        this.superMap = new SuperMap(map); // Initialize SuperMap with the current map
    }
    // Weighting methods for actions
    public void setDesire(int desire) {
        this.desire = desire; // Set the desire value
    }
    public void setTarget(int row, int col) {
        this.targetRow = row; // Set the target row
        this.targetCol = col; // Set the target column
    }
    public void incRow(int inc) {
    this.setRow(this.getRow()+inc);
    }
    public void incCol(int inc) {
    this.setCol(this.getCol()+inc);
    }
    // Movement methods
    public void takeAction() {
        setTarget();
        pathFind();
    }
    public void pathFind() {
    if (this.getRow() < targetRow && canMoveDown(map)) {
        this.incRow(1); // Move down
        System.out.println("Enemy pathfinding towards target: (" + targetRow + ", " + targetCol + ")");
    } else if (this.getRow() > targetRow && canMoveUp(map)) {
        this.incRow(-1); // Move up
    } else if (this.getCol() < targetCol && canMoveRight(map)) {
        this.incCol(1); // Move right
    } else if (this.getCol() > targetCol && canMoveLeft(map)) {
        this.incCol(-1); // Move left
    }
}
    public boolean danger(Map map, int row, int col) {
        if (map.getTile(row, col) instanceof BombFire) {
            return true;
    }
    else return false;
    }
    public boolean inDanger(Map map) {
        if (map.getTile(this.getRow(), this.getCol()) instanceof BombFire) {
            return true;
        }
    else return false;
    }
    public List<Runnable> moveSim(Map map, int row, int col) {
        List<Runnable> moves = new ArrayList<>();
    if (canMoveUpSafe(map, row, col))    moves.add(() -> { incRow(-1); });
    if (canMoveDownSafe(map, row, col))  moves.add(() -> { incRow(1); });
    if (canMoveLeftSafe(map, row, col))  moves.add(() -> { incCol(-1); });
    if (canMoveRightSafe(map, row, col)) moves.add(() -> { incCol(1); });
        if (moves.isEmpty()) {
            System.out.println("Enemy cannot move safely in any direction");
            return null;
        }
        return moves; // Return the list of possible moves
    }
    public List<Coordinate> viableMoves(Map map, int row, int col) {
        List<Coordinate> moves = new ArrayList<>();
        if (canMoveUpSafe(map, row, col))    moves.add(new Coordinate(row - 1, col));
        if (canMoveDownSafe(map, row, col))  moves.add(new Coordinate(row + 1, col));
        if (canMoveLeftSafe(map, row, col))  moves.add(new Coordinate(row, col - 1));
        if (canMoveRightSafe(map, row, col)) moves.add(new Coordinate(row, col + 1));
        for (Coordinate move : moves) {
            System.out.println("Enemy can move to: (" + move.getRow() + ", " + move.getCol() + ")");
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
    public int needToMove(int ticks, Coordinate target) {
        // Check if the enemy is not in danger and not trapped
        superMap.makePrediction(); // Update predictions
        List<Map> prediction = superMap.getPrediction();
        for (int i = ticks; i < prediction.size(); i++) {
            if (danger(prediction.get(i), target.getRow(), target.getCol())) {
                System.out.println("Enemy needs to move to avoid danger at tick " + i);
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
                System.out.println("Enemy needs to move by " + i + " ticks to avoid danger");
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
            while (isTrapped(superMap.getPrediction(currentTick), getRow(), getCol())) {
                System.out.println("Enemy is trapped, cannot escape");
                currentTick++;
            } 
            System.out.println("Enemy is not trapped, moving to escape");
            safestRandomMove(currentTick); // Choose a safe tile
            }
            else {
                System.out.println("There is no escape, womp womp");
                setTarget(getRow(), getCol()); // Stay in place if no escape is possible
            }
        }    
    public void safestRandomMove(int currentTick) {
        List<Coordinate> moves = viableMoves(superMap.getPrediction(currentTick), getRow(), getCol());
            List<Coordinate> bestMoves = new ArrayList<Coordinate>();
            for (int i = moves.size()-1; i>=0; i--) {
                if (!canEscape(currentTick+1, moves.get(i))) {
                    System.out.println("Enemy cannot escape from " + moves.get(i) + " at tick " + (currentTick + 1));
                    moves.remove(i); // Remove moves that lead to death
                } else if (needToMove(currentTick + 1, moves.get(i)) == -1) {
                    bestMoves.add(moves.get(i)); // Add moves that lead to complete safety
                    System.out.println("Could move to " + moves.get(i).toString() + " at tick " + (currentTick + 1) + " without danger");
                }
            }
            int choice = 0;
            if (bestMoves.size() > 0) {
                choice = (int) (Math.random() * bestMoves.size());
                Coordinate move = bestMoves.get(choice);
                setTarget(move.getRow(), move.getCol());
                System.out.println("Enemy moved to a safe tile at tick " + currentTick + ": (" + move.getRow() + ", " + move.getCol() + ")");
                    return; // Exit after moving to a safe tile
                }
            else if (moves.size() > 0) {
                choice = (int) (Math.random() * moves.size());
                Coordinate move = moves.get(choice);
                setTarget(move.getRow(), move.getCol());
                System.out.println("Enemy moved to escape at tick " + currentTick + ": (" + move.getRow() + ", " + move.getCol() + ") but will need to move again");
                return; // Exit after moving to a tile that may not be completely safe
            } else {
                System.out.println("Enemy cannot escape, no viable moves available");
                setTarget(getRow(), getCol()); // Stay in place if no moves are available
                return; // Exit after attempting to escape
            }
        }
    public void setTarget() {
        if (needToMove()) {
            escape(); // Try to escape if in danger or trapped
        } else {
            safestRandomMove(0); // Move towards the target if not in danger
        }
    }
    }
