package components;

import components.ShipComponent;

public class Defence extends ShipComponent {
    private double resistanceToEM;
    private double resistanceToThermal;
    private double resistanceToExplosive;
    private double resistanceToKinetic;

    public double getResistanceToEM() {
        return resistanceToEM;
    }

    public void setResistanceToEM(double resistanceToEM) {
        this.resistanceToEM = resistanceToEM;
    }

    public double getResistanceToThermal() {
        return resistanceToThermal;
    }

    public void setResistanceToThermal(double resistanceToThermal) {
        this.resistanceToThermal = resistanceToThermal;
    }

    public double getResistanceToExplosive() {
        return resistanceToExplosive;
    }

    public void setResistanceToExplosive(double resistanceToExplosive) {
        this.resistanceToExplosive = resistanceToExplosive;
    }

    public double getResistanceToKinetic() {
        return resistanceToKinetic;
    }

    public void setResistanceToKinetic(double resistanceToKinetic) {
        this.resistanceToKinetic = resistanceToKinetic;
    }

    public Defence(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}