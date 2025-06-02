public class Coordinate {
    private int row;
    private int col;
    private int tick;
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
    public void setTick(int tick) {
        this.tick = tick;
    }
    public int getDepth() {
        return tick;
    }
    public void tick() {
        this.tick++;
    }
    public boolean equals(Coordinate other) {
        if (other == null) {
            return false;
        }
        return this.row == other.row && this.col == other.col && this.tick == other.tick;
    }
    @Override
    public String toString() {
        return "Coordinate: (" + row + ", " + col + ")";
    }
    public void print() {
        System.out.println(this.toString());
    }
}
