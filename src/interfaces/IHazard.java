package interfaces;

/**
 * IHazard interface represents hazardous objects on the icy terrain.
 * All hazards implement ITerrainObject and can appear on the grid.
 *
 * There are four types of hazards in the game:
 * - LightIceBlock: Movable block that stuns penguins
 * - HeavyIceBlock: Immovable wall that causes food loss
 * - SeaLion: Movable creature with bounce mechanics
 * - HoleInIce: Immovable trap that eliminates objects
 *
 * Some hazards can slide on ice (movable), while others remain stationary (immovable).
 */
public interface IHazard extends ITerrainObject {
    /**
     * Indicates whether this hazard can be pushed/moved by sliding objects.
     *
     * Movable hazards (return true):
     * - LightIceBlock: Slides when hit by penguins or other hazards
     * - SeaLion: Slides when hit, with special bounce mechanics
     *
     * Immovable hazards (return false):
     * - HeavyIceBlock: Acts as a solid wall
     * - HoleInIce: Remains in place, trapping sliding objects
     *
     * @return true if this hazard can slide, false if it's immovable
     */
    boolean isMovable();
}
