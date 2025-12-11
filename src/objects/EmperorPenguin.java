package objects;

/**
 * EmperorPenguin is a type of penguin with a special sliding ability.
 * Special Ability: When sliding, can choose to stop at the THIRD square they slide into.
 * If the path has less than 3 free squares, the ability is still consumed.
 * The ability can only be used once per game.
 */
public class EmperorPenguin extends Penguin {
    /**
     * Creates a new EmperorPenguin with the given name
     * @param name The penguin's identifier (e.g., "P2")
     */
    public EmperorPenguin(String name) {
        super(name);
    }
}
