import java.util.ArrayList;
import java.util.List;

public class Enemy extends Player {
    int desire;
    int targetRow;
    int targetCol;
    Map map;

    public Enemy(Map map, int row, int col) {
        super(map, row, col);
        this.desire = 0; // Initialize desire to 0
        this.targetRow = row; // Set targetRow to current row
        this.targetCol = col; // Set targetCol to current column
        this.map = map; // Store the map reference
    }
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
    public void pathFind() {
    if (this.getRow() > targetRow && canMoveDown(map)) {
        this.incRow(1);
        System.out.println("Enemy pathfinding towards target: (" + targetRow + ", " + targetCol + ")");
    } else if (this.getRow() < targetRow && canMoveUp(map)) {
        this.incRow(-1);
    } else if (this.getCol() < targetCol && canMoveRight(map)) {
        this.incCol(1);
    } else if (this.getCol() > targetCol && canMoveLeft(map)) {
        this.incCol(-1);
    }
}
    public void randomMove() {
    List<Runnable> moves = new ArrayList<>();
    if (canMoveUp(map))    moves.add(() -> { incRow(-1); System.out.println("Enemy moved up"); });
    if (canMoveDown(map))  moves.add(() -> { incRow(1);  System.out.println("Enemy moved down"); });
    if (canMoveLeft(map))  moves.add(() -> { incCol(-1); System.out.println("Enemy moved left"); });
    if (canMoveRight(map)) moves.add(() -> { incCol(1);  System.out.println("Enemy moved right"); });

    if (moves.isEmpty()) {
        System.out.println("Enemy cannot move in any direction");
        return;
    }
    int choice = (int) (Math.random() * moves.size());
    moves.get(choice).run();
}



}
