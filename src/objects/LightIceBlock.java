package objects;

import interfaces.IHazard;

/**
 * LightIceBlock is a movable hazard that can slide on ice.
 * When a penguin or another sliding hazard collides with it:
 * - The LightIceBlock starts sliding in the transmitted direction
 * - The colliding object stops at its current position
 * - The colliding penguin becomes STUNNED and skips their next turn
 * A sliding LightIceBlock can fall from the edges of the grid.
 */
public class LightIceBlock implements IHazard {

    /**
     * Returns the symbol representing this hazard on the grid
     * @return "LB" for LightIceBlock
     */
    @Override
    public String getSymbol() {
        return "LB";
    }

    /**
     * Indicates whether this hazard can be pushed/moved
     * @return true, as LightIceBlock can slide
     */
    @Override
    public boolean isMovable() {
        return true;
    }
}
