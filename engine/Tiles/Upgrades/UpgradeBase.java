// package declarations and imports here;
public class UpgradeBase extends Tile
{
  // constructors
  public UpgradeBase() {
    super("UpgradeBase", false, true);
  }
  public UpgradeBase(String type) {
    super(type, false, true);
  }

  // methods
  public void upgradePlayer(Player player) {
    //Player.addSpeed(5);
  }
}
