import java.util.ArrayList;
import java.util.HashMap;

public class IdeaSet {
    private String name;
    private ArrayList<HashMap<String,Double>> ideas = new ArrayList<>();
    private String trigger;
    private double customPoints;

    public IdeaSet(String name, ArrayList<HashMap<String,Double>> ideas, String trigger) {
        this.name = name;
        this.ideas = ideas;
        this.trigger = trigger;
    }

    public void calcCustomPoints(HashMap<String, CustomIdea> customIdeas) {
        this.customPoints = 0.0;
        double admLevels = 0.0;
        double dipLevels = 0.0;
        double milLevels = 0.0;
        //System.out.println(name + "\n---------------------");
        double pointMultiplier;
        for(int i = 0; i < 9; i++) {
            if(i < 1) {
                pointMultiplier = 2.0;
            }
            else if (i < 9 && i > 1) {
                pointMultiplier = 2.0 - (i-2) * 0.2;
            }
            else {
                pointMultiplier = 1.0;
            }
            for(String modifier : ideas.get(i).keySet()) {
                String altModifier = modifier;

                if(modifier.equals("liberty_desire")) {
                    modifier = "reduced_liberty_desire";
                }
                else if(modifier.equals("imperial_authority_value")) {
                    modifier = "imperial_authority";
                }
                else if(modifier.equals("flagship_cost")) {
                    modifier = "light_ship_cost";
                }
                else if(modifier.equals("pr_captains_influence")) {
                    modifier = "republican_tradition";
                }

                if(customIdeas.containsKey(modifier)) {
                    CustomIdea customModifier = customIdeas.get(modifier);
                    double level;
                    level = ideas.get(i).get(altModifier) / customModifier.getModifier();

                    String category = customModifier.getCategory();
                    if(category.equals("ADM")) {
                        admLevels += 10 * level / customModifier.getMaxLevel();
                    }
                    else if(category.equals("DIP")) {
                        dipLevels += 10 * level / customModifier.getMaxLevel();
                    }
                    else if(category.equals("MIL")) {
                        milLevels += 10 * level / customModifier.getMaxLevel();
                    }
                    else {
                        System.out.println("Missing category: " + modifier);
                    }

                    /*
                    if(modifier.equals(altModifier)) {
                        level = ideas.get(i).get(modifier) / customModifier.getModifier();
                    }
                    else {
                        level = ideas.get(i).get(altModifier) / customModifier.getModifier();
                    }

                     */
                    double customCost = customModifier.getBaseCost();
                    if (customModifier.getLevelCostBase() != 0) {
                        customCost = (customModifier.getLevelCostBase()/2.0) *
                                ((level-1) * level + (customModifier.getBaseCost() * 2.0 / customModifier.getLevelCostBase()));
                        customCost *= pointMultiplier;
                    }

                    if(altModifier.equals("liberty_desire")) {
                        this.customPoints -= customCost;
                    }
                    else if(altModifier.equals("flagship_cost")) {
                        this.customPoints += customCost / 2.0;
                    }
                    else {
                        this.customPoints += customCost;
                    }

                    if(this.name.equals("generic_federation_ideas") && false) {
                        //System.out.println(customModifier.getName() + ": " + customCost);

                        System.out.println(modifier);
                        System.out.println("\tLevel = " + ideas.get(i).get(altModifier) + " (Idea modifier) / "
                                + customModifier.getModifier() + " (Custom modifier) " + " = " + level);
                        System.out.println("\tCustom points = " + customModifier.getBaseCost() + " (Custom base cost) + "
                                + customModifier.getLevelCostBase() + " (Custom level cost)" + " * " + (level-1) + " (level-1) = " +
                                customCost / pointMultiplier);
                        System.out.println("\tCustom points modified = " + customCost / pointMultiplier + " (Custom points) * " + pointMultiplier
                                + " (Point multipler) = " + customCost);
                        System.out.println("\tCumulative points = " + this.customPoints);
                        System.out.println("\n");
                    }


                }
                else {
                    //System.out.println("No custom: " + modifier);
                }
            }
        }

        double levelTotal = admLevels + dipLevels + milLevels;
        double extraPointModifier = 1.0;

        if(admLevels / levelTotal > 0.50) {
            extraPointModifier += (admLevels / levelTotal - 0.50) * 0.05;
        }
        else if(dipLevels / levelTotal > 0.50) {
            extraPointModifier += (dipLevels / levelTotal - 0.50) * 0.05;
        }
        else if(milLevels / levelTotal > 0.50) {
            extraPointModifier += (milLevels / levelTotal - 0.50) * 0.05;
        }

        if(this.name.equals("BUR_ideas") && false) {
            System.out.println("AdmLevels: " + admLevels / levelTotal);
            System.out.println("DipLevels: " + dipLevels / levelTotal);
            System.out.println("MilLevels: " + milLevels / levelTotal);
            System.out.println("Extra point modifier: " + extraPointModifier);
        }


        this.customPoints *= extraPointModifier;
    }

    public void print() {
        System.out.println(name + " (" + customPoints + ")");
        System.out.println("\tTraditions");
        for(String modifier : ideas.get(0).keySet()) {
            System.out.println("\t\t" + modifier + " = " + ideas.get(0).get(modifier));
        }
        System.out.println("\tIdeas");
        for(int i = 2; i < 9; i++) {
            for(String modifier : ideas.get(i).keySet()) {
                System.out.println("\t\t" + modifier + " = " + ideas.get(i).get(modifier));
            }
            System.out.println("\t\t---------------------------------");
        }
        System.out.println("\tAmbition");
        for(String modifier : ideas.get(1).keySet()) {
            System.out.println("\t\t" + modifier + " = " + ideas.get(1).get(modifier));
        }
        System.out.println("Trigger = {");
        System.out.println(trigger);
        System.out.println("}");
    }

    public String getName() {
        return name;
    }

    public String getTrigger() {
        return trigger;
    }

    public double getCustomPoints() {
        return customPoints;
    }
}
