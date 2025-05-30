public class Coordinate {
    private int row;
    private int col;
    private int depth;
    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }
    public int getDepth() {
        return depth;
    }
    public void increaseDepth() {
        this.depth++;
    }
    @Override
    public String toString() {
        return "Coordinate: (" + row + ", " + col + ")";
    }
    public void print() {
        System.out.println(this.toString());
    }
}
