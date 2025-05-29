
    import java.util.ArrayList;

public class BombFireGroup {
    private int fuse;
    private ArrayList<BombFire> fires;

    public BombFireGroup(int fuse) {
        this.fuse = fuse;
        this.fires = new ArrayList<>();
    }

    public void addFire(BombFire fire) {
        fires.add(fire);
        fire.setGroup(this);
    }

    public void tickFuse() {
        fuse--;
    }

    public int getFuse() {
        return fuse;
    }

    public ArrayList<BombFire> getFires() {
        return fires;
    }
}
