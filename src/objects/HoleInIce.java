package objects;

import interfaces.IHazard;

public class HoleInIce implements IHazard {

    private boolean isPlugged;

    public HoleInIce() {
        this.isPlugged = false;
    }

    public void setPlugged(boolean plugged) {
        this.isPlugged = plugged;
    }

    public boolean isPlugged() {
        return isPlugged;
    }

    @Override
    public String getSymbol() {
        if (isPlugged) {
            return "PH";
        }
        return "HI";
    }

    @Override
    public boolean isMovable() {
        return false;
    }
}