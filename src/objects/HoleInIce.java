package objects;

import interfaces.IHazard;

/**
 * HoleInIce is an immovable hazard that traps objects.
 * Anything that slides into an unplugged hole falls into it:
 * - Penguins are eliminated from the game (but their food still counts)
 * - LightIceBlocks and SeaLions plug the hole when they fall in
 *
 * Once plugged, the hole becomes passable and displays as "PH" instead of "HI".
 * Sliding objects can pass through plugged holes without issues.
 */
public class HoleInIce implements IHazard {

    private boolean isPlugged; // Tracks if this hole has been plugged

    /**
     * Creates a new unplugged HoleInIce
     */
    public HoleInIce() {
        this.isPlugged = false;
    }

    /**
     * Sets whether this hole is plugged
     * @param plugged true to plug the hole, false to unplug
     */
    public void setPlugged(boolean plugged) {
        this.isPlugged = plugged;
    }

    /**
     * Checks if this hole is plugged
     * @return true if plugged, false if open
     */
    public boolean isPlugged() {
        return isPlugged;
    }

    /**
     * Returns the symbol representing this hazard on the grid
     * @return "PH" if plugged, "HI" if unplugged
     */
    @Override
    public String getSymbol() {
        if (isPlugged) {
            return "PH";
        }
        return "HI";
    }

    /**
     * Indicates whether this hazard can be pushed/moved
     * @return false, as HoleInIce cannot move
     */
    @Override
    public boolean isMovable() {
        return false;
    }
}
