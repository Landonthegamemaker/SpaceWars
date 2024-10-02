package components;

import components.Weapon;

public class Railgun extends Weapon {
    private int ammo;

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public Railgun(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}