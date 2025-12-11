package enums;

/**
 * Direction enum represents the four cardinal directions for penguin movement.
 * Penguins can slide UP, DOWN, LEFT, or RIGHT on the icy terrain.
 * These directions are used for both player input and AI decision making.
 */
public enum Direction {
    /**
     * Move upward (decrease row index)
     */
    UP,

    /**
     * Move leftward (decrease column index)
     */
    LEFT,

    /**
     * Move rightward (increase column index)
     */
    RIGHT,

    /**
     * Move downward (increase row index)
     */
    DOWN
}
