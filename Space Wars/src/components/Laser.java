package components;

public class Laser extends Weapon {
    private int batteryCost;

    public int getBatteryCost() {
        return batteryCost;
    }

    public void setBatteryCost(int batteryCost) {
        this.batteryCost = batteryCost;
    }

    public Laser(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
