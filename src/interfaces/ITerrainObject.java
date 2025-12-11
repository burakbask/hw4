package interfaces;

/**
 * ITerrainObject is the base interface for all objects that can exist on the icy terrain grid.
 * This includes penguins, food items, and hazards.
 *
 * All terrain objects must provide a symbol for display on the grid.
 * This interface enables polymorphism, allowing different object types to be stored
 * in the same grid structure.
 */
public interface ITerrainObject {
    /**
     * Returns the symbol representing this object on the grid display.
     * Symbols are typically 2-3 characters (e.g., "P1", "Kr", "LB", "HI").
     *
     * @return A string symbol for grid display
     */
    String getSymbol();
}
