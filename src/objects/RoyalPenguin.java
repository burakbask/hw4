package objects;

/**
 * RoyalPenguin is a type of penguin with a special movement ability.
 * Special Ability: Before sliding, can choose to safely move into ONE adjacent square
 * (only horizontally or vertically). Then the penguin slides normally from that position.
 * It is possible to accidentally step out of the grid while using this ability.
 * The ability can only be used once per game.
 */
public class RoyalPenguin extends Penguin {
    /**
     * Creates a new RoyalPenguin with the given name
     * @param name The penguin's identifier (e.g., "P3")
     */
    public RoyalPenguin(String name) {
        super(name);
    }
}
