package objects;

import interfaces.IHazard;

public class HeavyIceBlock implements IHazard {

    @Override
    public String getSymbol() {
        return "HB";
    }

    @Override
    public boolean isMovable() {
        return false;
    }
}