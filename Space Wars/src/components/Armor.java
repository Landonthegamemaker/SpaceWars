package components;

import components.Defence;

public class Armor extends Defence {
    public Armor(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
