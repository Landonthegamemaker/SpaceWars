package components;

import components.Defence;

import java.util.ArrayList;

public class Shield extends Defence {
    private int regenRate;
    private boolean passThrough;

    public int getRegenRate() {
        return regenRate;
    }

    public void setRegenRate(int regenRate) {
        this.regenRate = regenRate;
    }

    public boolean isPassThrough() {
        return passThrough;
    }

    public void setPassThrough(boolean passThrough) {
        this.passThrough = passThrough;
    }

    public Shield(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
