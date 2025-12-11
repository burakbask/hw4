package objects;

/**
 * KingPenguin is a type of penguin with a special sliding ability.
 * Special Ability: When sliding, can choose to stop at the FIFTH square they slide into.
 * If the path has less than 5 free squares, the ability is still consumed.
 * The ability can only be used once per game.
 */
public class KingPenguin extends Penguin {
    /**
     * Creates a new KingPenguin with the given name
     * @param name The penguin's identifier (e.g., "P1")
     */
    public KingPenguin(String name) {
        super(name);
    }
}
