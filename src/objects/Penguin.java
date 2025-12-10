package objects;

import interfaces.ITerrainObject;
import java.util.ArrayList;
import java.util.List;

public abstract class Penguin implements ITerrainObject {
    protected String name;
    protected List<Food> inventory;
    public boolean isActive;


    public Penguin(String name){
        this.name = name;
        this.inventory = new ArrayList<>();
        this.isActive = true;
    }

    @Override
    public String getSymbol() {
        return this.name;
    }

    public void eliminate() {
        this.isActive = false;
        System.out.println(this.name + " elendi!");
    }

    public void eatFood(Food food) {
        this.inventory.add(food);
        System.out.println(this.name + " ate " + food.getType() + " (" + food.getWeight() + " units).");
    }

    public int getTotalWeight() {
        int total = 0;
        for (Food f : inventory) {
            total += f.getWeight();
        }
        return total;
    }

    public boolean isActive() {
        return isActive;
    }
}
