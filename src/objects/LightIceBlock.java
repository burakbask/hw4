package objects;

import interfaces.IHazard;

public class LightIceBlock implements IHazard {

    @Override
    public String getSymbol() {
        return "LB";
    }

    @Override
    public boolean isMovable() {
        return true;
    }
}