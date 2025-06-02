
    import java.util.ArrayList;

public class BombFireGroup implements Cloneable {
    private int fuse;
    private ArrayList<BombFire> fires;

    public BombFireGroup(int fuse) {
        this.fuse = fuse;
        this.fires = new ArrayList<>();
    }
    //clone
    @Override
public BombFireGroup clone() {
    try {
        BombFireGroup cloned = (BombFireGroup) super.clone();
        cloned.fires = new ArrayList<>();
        for (BombFire fire : this.fires) {
            BombFire fireClone = fire.clone();
            fireClone.setGroup(cloned); // update group reference
            cloned.fires.add(fireClone);
        }
        return cloned;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError();
    }
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
    public void setFuse(int fuse) {
        this.fuse = fuse;
    }
}
