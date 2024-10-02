package components;

import components.ShipComponent;

public class Sensor extends ShipComponent {
    private double accuracyBoost;

    public double getAccuracyBoost() {
        return accuracyBoost;
    }

    public void setAccuracyBoost(double accuracyBoost) {
        this.accuracyBoost = accuracyBoost;
    }

    public Sensor(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
