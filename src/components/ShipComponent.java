package components;

public class ShipComponent {
    private String name;
    private int maxIntegrity;
    private int currIntegrity;
    private boolean isDestroyed;
    private double weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxIntegrity() {
        return maxIntegrity;
    }

    public void setMaxIntegrity(int maxIntegrity) {
        this.maxIntegrity = maxIntegrity;
    }

    public int getCurrIntegrity() {
        return currIntegrity;
    }

    public void setCurrIntegrity(int currIntegrity) {
        this.currIntegrity = currIntegrity;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ShipComponent() {this.setCurrIntegrity(0);}
    public ShipComponent(String name) {
        this.name = name;
    }

    public boolean getisDestroyed() {return isDestroyed;}
    public void setisDestroyed(boolean isDestroyed) {this.isDestroyed = isDestroyed;}

    @Override
    public String toString() {
        return this.getName();
    }
}
