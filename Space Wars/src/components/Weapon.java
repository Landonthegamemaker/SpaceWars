package components;

import components.ShipComponent;

public class Weapon extends ShipComponent {

    private int damage;
    private String damageType;
    private double accuracy;
    public int fireRate;

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public Weapon(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
