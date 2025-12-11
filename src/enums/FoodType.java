package enums;

/**
 * FoodType enum represents the different types of food available on the icy terrain.
 * Each food item is randomly assigned one of these types during generation.
 * The type determines the food's display symbol on the grid:
 * - KRILL: Kr
 * - CRUSTACEAN: Cr
 * - ANCHOVY: An
 * - SQUID: Sq
 * - MACKEREL: Ma
 *
 * Each food also has a random weight (1-5 units) independent of its type.
 */
public enum FoodType {
    /**
     * Krill - small crustaceans (symbol: Kr)
     */
    KRILL,

    /**
     * Crustacean - shellfish (symbol: Cr)
     */
    CRUSTACEAN,

    /**
     * Anchovy - small fish (symbol: An)
     */
    ANCHOVY,

    /**
     * Squid - cephalopod (symbol: Sq)
     */
    SQUID,

    /**
     * Mackerel - medium-sized fish (symbol: Ma)
     */
    MACKEREL
}
