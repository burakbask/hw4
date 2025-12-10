package objects;

import enums.FoodType;
import interfaces.ITerrainObject;
import java.util.Random;

public class Food implements ITerrainObject {

    private int weight;
    private FoodType type;

    public Food() {
        Random rand = new Random();
        this.weight = rand.nextInt(5) + 1;

        FoodType[] types = FoodType.values();
        this.type = types[rand.nextInt(types.length)];
    }

    public int getWeight() {
        return weight;
    }

    public FoodType getType() {
        return type;
    }

    @Override
    public String getSymbol() {
        String name = type.toString();
        String sym = name.substring(0, 1).toUpperCase() + name.substring(1, 2).toLowerCase();
        return sym;
    }
}