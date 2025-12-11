package objects;

import interfaces.IHazard;

/**
 * HeavyIceBlock is an immovable hazard that acts as a wall.
 * When a penguin collides with it:
 * - The penguin stops in its tracks
 * - The penguin loses the LIGHTEST food item from their inventory as a penalty
 * - If the penguin has no food, they are unaffected
 * HeavyIceBlock cannot be moved by any sliding object.
 */
public class HeavyIceBlock implements IHazard {

    /**
     * Returns the symbol representing this hazard on the grid
     * @return "HB" for HeavyIceBlock
     */
    @Override
    public String getSymbol() {
        return "HB";
    }

    /**
     * Indicates whether this hazard can be pushed/moved
     * @return false, as HeavyIceBlock is immovable
     */
    @Override
    public boolean isMovable() {
        return false;
    }
}
