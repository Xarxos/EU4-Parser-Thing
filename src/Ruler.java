import java.util.ArrayList;

public class Ruler {
    private String name;
    private String type;
    private String gender;
    private String dynasty;
    private int age;
    private int adm;
    private int dip;
    private int mil;
    private String culture;
    private String religion;
    private ArrayList<String> personalities = new ArrayList<>();
    private double customCost = 0.0;

    public Ruler(String name, String type, String gender, String dynasty, int age, int adm, int dip, int mil,
                 String culture, String religion, ArrayList<String> personalities) {
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.dynasty = dynasty;
        this.age = age;
        this.adm = adm;
        this.dip = dip;
        this.mil = mil;
        this.culture = culture;
        this.religion = religion;
        this.personalities = personalities;

        calcCustomCost();
    }

    private void calcCustomCost() {
        int cost = (adm + dip + mil - 6) * 2;
        int personalityCost = 0;

        for(String personality : personalities) {
            if(personality.equals("kind_hearted_personality") || personality.equals("careful_personality")
                || personality.equals("lawgiver_personality") || personality.equals("scholar_personality")
                || personality.equals("strict_personality") || personality.equals("inspiring_leader_personality")) {
                personalityCost += 5;
            }
            else if(personality.equals("babbling_buffoon_personality") || personality.equals("embezzler_personality")
                    || personality.equals("infertile_personality") || personality.equals("indulgent_personality")
                    || personality.equals("cruel_personality") || personality.equals("naive_enthusiast_personality")
                    || personality.equals("craven_personality") || personality.equals("loose_lips_personality")
                    || personality.equals("obsessive_perfectionist_personality") || personality.equals("malevolent_personality")
                    || personality.equals("petty_personality") || personality.equals("sinner_personality")
                    || personality.equals("greedy_personality")) {
                personalityCost -= 2;
            }
            else {
                personalityCost += 2;
            }
        }
        cost += personalityCost;
        double totCost = (double)cost;

        if(type.equals("monarch")) {
            totCost *= 30.0 / (double)age;
        }
        else if(type.equals("heir")) {
            totCost *= 30.0 / (double)(age + 15);
        }
        else if(type.equals("consort")) {
            totCost *= 0.25;
        }

        customCost = totCost;
    }

    public void print() {
        System.out.println("\t\tName: " + name);
        System.out.println("\t\tCustom Cost: " + customCost);
        System.out.println("\t\tGender: " + gender);
        if(dynasty != null) {
            System.out.println("\t\tDynasty: " + dynasty);
        }
        System.out.println("\t\tAge: " + age);
        System.out.println("\t\tStats: " + adm + "/" + dip + "/" + mil);
        System.out.println("\t\tCulture: " + culture);
        System.out.println("\t\tPersonalities:");
        for(String personality : personalities) {
            System.out.println("\t\t\t" + personality);
        }
    }

    public void setPersonalities(ArrayList<String> personalities) {
        this.personalities = personalities;
        calcCustomCost();
    }

    public double getCustomCost() {
        return customCost;
    }
}
