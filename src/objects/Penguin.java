package objects;

import interfaces.ITerrainObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all penguin types.
 * Penguins can slide on ice, collect food, and have special abilities.
 * Each penguin maintains an inventory of collected food items.
 */
public abstract class Penguin implements ITerrainObject {
    protected String name;
    protected List<Food> inventory;
    protected boolean isActive;
    protected boolean isStunned; // True if penguin is stunned and should skip next turn
    protected boolean hasUsedSpecialAbility; // Track if special ability has been used

    /**
     * Creates a new penguin with the given name.
     * @param name The identifier for this penguin (e.g., "P1", "P2", "P3")
     */
    public Penguin(String name){
        this.name = name;
        this.inventory = new ArrayList<>();
        this.isActive = true;
        this.isStunned = false;
        this.hasUsedSpecialAbility = false;
    }

    /**
     * Returns the symbol/name displayed on the grid
     * @return The penguin's name
     */
    @Override
    public String getSymbol() {
        return this.name;
    }

    /**
     * Eliminates this penguin from the game (fell into water/hole).
     * The penguin's collected food still counts for final scoring.
     */
    public void eliminate() {
        this.isActive = false;
        System.out.println(this.name + " has been removed from the game!");
    }

    /**
     * Adds a food item to this penguin's inventory.
     * @param food The food item to collect
     */
    public void eatFood(Food food) {
        this.inventory.add(food);
        System.out.println(this.name + " takes the " + food.getType() + " on the ground. (Weight=" + food.getWeight() + " units)");
    }

    /**
     * Calculates the total weight of all collected food items.
     * @return The sum of weights of all food in inventory
     */
    public int getTotalWeight() {
        int total = 0;
        for (Food f : inventory) {
            total += f.getWeight();
        }
        return total;
    }

    /**
     * Checks if this penguin is still active in the game
     * @return true if penguin hasn't been eliminated, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Gets the list of food items collected by this penguin.
     * @return The inventory list
     */
    public List<Food> getInventory() {
        return inventory;
    }

    /**
     * Removes the lightest food item from inventory as a penalty.
     * Used when penguin hits a HeavyIceBlock.
     * @return true if a food item was removed, false if inventory was empty
     */
    public boolean removeLightestFood() {
        if (inventory.isEmpty()) {
            return false;
        }

        // Find the lightest food item
        Food lightest = inventory.get(0);
        for (Food f : inventory) {
            if (f.getWeight() < lightest.getWeight()) {
                lightest = f;
            }
        }

        inventory.remove(lightest);
        System.out.println(this.name + " lost " + lightest.getType() + " (" + lightest.getWeight() + " units) as penalty!");
        return true;
    }

    /**
     * Stuns this penguin, causing it to skip its next turn.
     * Used when penguin collides with a LightIceBlock.
     */
    public void stun() {
        this.isStunned = true;
        System.out.println(this.name + " is stunned and will skip the next turn!");
    }

    /**
     * Checks if penguin is currently stunned
     * @return true if stunned, false otherwise
     */
    public boolean isStunned() {
        return isStunned;
    }

    /**
     * Clears the stun status (called at the start of the turn)
     */
    public void clearStun() {
        this.isStunned = false;
    }

    /**
     * Marks the special ability as used
     */
    public void useSpecialAbility() {
        this.hasUsedSpecialAbility = true;
    }

    /**
     * Checks if special ability has been used
     * @return true if already used, false otherwise
     */
    public boolean hasUsedSpecialAbility() {
        return hasUsedSpecialAbility;
    }
}
