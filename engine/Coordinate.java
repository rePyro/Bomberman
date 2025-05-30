public class Coordinate {
    int row;
    int col;
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
    @Override
    public String toString() {
        return "Coordinate: (" + row + ", " + col + ")";
    }
    public void print() {
        System.out.println(this.toString());
    }
}
