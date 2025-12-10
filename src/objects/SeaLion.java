package objects;

import interfaces.IHazard;

public class SeaLion implements IHazard {

    @Override
    public String getSymbol() {
        return "SL";
    }

    @Override
    public boolean isMovable() {
        return true;
    }
}