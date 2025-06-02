import java.lang.reflect.Array;
import java.util.ArrayList;
public class CoordPath {
    private ArrayList<Coordinate> path;
    private int currentIndex;

    public CoordPath() {
        path = new ArrayList<>();
        currentIndex = 0;
    }
    public CoordPath clone() {
        CoordPath cloned = new CoordPath();
        for (Coordinate coord : this.path) {
            cloned.addCoord(new Coordinate(coord.getRow(), coord.getCol()));
        }
        cloned.currentIndex = this.currentIndex;
        return cloned;
    }
    public void addCoord(Coordinate coord) {
        path.add(coord);
    }
    public void addCoord(Coordinate coord, int tick) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        Coordinate newCoord = new Coordinate(coord.getRow(), coord.getCol());
        newCoord.setTick(tick);
        path.add(newCoord);
    }
    public Coordinate getCoord(int index) {
        if (index < 0 || index >= path.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return path.get(index);
    }
    public int size() {
        return path.size();
    }
    public ArrayList<Coordinate> getPath() {
        return new ArrayList<>(path); // Return a copy to prevent external modification
}
    public ArrayList<Coordinate> getCoords(int tick) {
        ArrayList<Coordinate> coordsAtTick = new ArrayList<>();
        for (Coordinate coord : path) {
            if (coord.getDepth() == tick) {
                coordsAtTick.add(coord);
            }
        }
        return coordsAtTick;
    }
}
