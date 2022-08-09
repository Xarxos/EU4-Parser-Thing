import java.util.ArrayList;

public class CustomIdea {
    private String name;
    private String category;
    private double modifier;
    private int baseCost;
    private int levelCostBase;
    private int maxLevel;

    public CustomIdea(String name, String category, double modifier, int baseCost, int level2Cost, int maxLevel) {
        this.name = name;
        this.category = category;
        this.modifier = modifier;
        this.baseCost = baseCost;
        this.levelCostBase = level2Cost - baseCost;
        this.maxLevel = maxLevel;
    }

    public void print() {
        System.out.println("Name: " + name);
        System.out.println("Category: " + category);
        System.out.println("Modifier: " + modifier);
        System.out.println("Base cost: " + baseCost);
        System.out.println("Level cost base: " + levelCostBase);
        System.out.println("Max level: " + maxLevel);
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getModifier() {
        return modifier;
    }

    public int getBaseCost() {
        return baseCost;
    }

    public int getLevelCostBase() {
        return levelCostBase;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
