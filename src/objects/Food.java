package objects;

import enums.FoodType;
import interfaces.ITerrainObject;
import java.util.Random;

/**
 * Represents a food item on the icy terrain.
 * Food items have a random type (from FoodType enum) and a random weight (1-5 units).
 * Penguins collect food to increase their score.
 */
public class Food implements ITerrainObject {

    private int weight;
    private FoodType type;

    /**
     * Creates a new Food item with random type and weight.
     * Weight is randomly assigned between 1-5 units.
     * Type is randomly chosen from: KRILL, CRUSTACEAN, ANCHOVY, SQUID, MACKEREL
     */
    public Food() {
        Random rand = new Random();
        this.weight = rand.nextInt(5) + 1; // Random weight: 1-5

        FoodType[] types = FoodType.values();
        this.type = types[rand.nextInt(types.length)]; // Random type
    }

    /**
     * Gets the weight of this food item in units
     * @return The weight value (1-5)
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Gets the type of this food item
     * @return The FoodType enum value
     */
    public FoodType getType() {
        return type;
    }

    /**
     * Returns the two-character symbol representation of the food type.
     * Kr = Krill, Cr = Crustacean, An = Anchovy, Sq = Squid, Ma = Mackerel
     * @return The food type abbreviation as a string
     */
    @Override
    public String getSymbol() {
        switch(type) {
            case KRILL: return "Kr";
            case CRUSTACEAN: return "Cr";
            case ANCHOVY: return "An";
            case SQUID: return "Sq";
            case MACKEREL: return "Ma";
            default: return "??";
        }
    }
}