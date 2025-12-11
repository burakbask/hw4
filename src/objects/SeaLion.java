package objects;

import interfaces.IHazard;

/**
 * SeaLion is a movable hazard with special bounce mechanics.
 * When a penguin collides with a SeaLion:
 * - The penguin BOUNCES back and starts sliding in the OPPOSITE direction
 * - The SeaLion starts sliding in the penguin's INITIAL direction
 * - Both can fall from edges or collide with other objects after bouncing
 *
 * When a LightIceBlock collides with a SeaLion:
 * - The LightIceBlock stops moving
 * - The SeaLion starts sliding in the LightIceBlock's direction
 */
public class SeaLion implements IHazard {

    /**
     * Returns the symbol representing this hazard on the grid
     * @return "SL" for SeaLion
     */
    @Override
    public String getSymbol() {
        return "SL";
    }

    /**
     * Indicates whether this hazard can be pushed/moved
     * @return true, as SeaLion can slide
     */
    @Override
    public boolean isMovable() {
        return true;
    }
}
