package objects;

/**
 * RockhopperPenguin is a type of penguin with a special jumping ability.
 * Special Ability: Before sliding, can prepare to jump over ONE hazard in their path.
 * The penguin can only jump to an empty square (or square with food).
 * If the landing square is occupied by another hazard/penguin, the jump fails.
 * The ability can only be used once per game.
 *
 * For AI-controlled RockhopperPenguins: The first time they decide to move towards
 * a hazard, they will automatically use their jumping ability.
 */
public class RockhopperPenguin extends Penguin {
    /**
     * Creates a new RockhopperPenguin with the given name
     * @param name The penguin's identifier (e.g., "P1")
     */
    public RockhopperPenguin(String name) {
        super(name);
    }
}
