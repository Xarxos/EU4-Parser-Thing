import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nation {
    enum BlockScope {
        AND,
        OR,
        NOT,
        OTHER
    }

    private String tag;
    private String name;
    private String governmentForm;
    private GovernmentReform governmentReform;
    private int governmentRank;
    private String technologyGroup;
    private String religion;
    private String religionGroup;
    private String primaryCulture;
    private String cultureGroup;
    private int capital;
    private String capitalArea;
    private String capitalRegion;
    private String capitalSuperregion;
    private String capitalContinent;
    private Ruler monarch;
    private Ruler heir;
    private Ruler consort;
    private IdeaSet ideaSet;
    private double customCost;

    public Nation(String tag, String name, String governmentForm, GovernmentReform governmentReform, int governmentRank,
                  String technologyGroup, String religion, String religionGroup, String primaryCulture,
                  String cultureGroup, int capital, String capitalArea, String capitalRegion, String capitalSuperregion,
                  String capitalContinent, Ruler monarch, Ruler heir, Ruler consort) {
        this.tag = tag;
        this.name = name;
        this.governmentForm = governmentForm;
        this.governmentReform = governmentReform;
        this.governmentRank = governmentRank;
        this.technologyGroup = technologyGroup;
        this.religion = religion;
        this.religionGroup = religionGroup;
        this.primaryCulture = primaryCulture;
        this.cultureGroup = cultureGroup;
        this.capital = capital;
        this.capitalArea = capitalArea;
        this.capitalRegion = capitalRegion;
        this.capitalSuperregion = capitalSuperregion;
        this.capitalContinent = capitalContinent;
        this.monarch = monarch;
        this.heir = heir;
        this.consort = consort;
    }

    public void calcIdeaSet(ArrayList<IdeaSet> ideaSets) {
        if(governmentReform == null) {
            System.out.println("NULL GOVERNMENT: " + tag);
        }
        for(IdeaSet ideaSet : ideaSets) {
            String trigger = ideaSet.getTrigger();
            if(!trigger.equals("")) {
                int i;
                for(i = 0; trigger.charAt(i) != '{'; i++) { }

                if(evaluateBlock(trigger, i+1, BlockScope.AND)) {
                    this.ideaSet = ideaSet;
                    return;
                }
            }
            else {
                this.ideaSet = ideaSet;
            }
        }
    }

    private boolean evaluateBlock(String triggerStr, int start, BlockScope logicBlock) {
        int openBrackets = 1;
        String thisBlock = "";
        ArrayList<Boolean> triggers = new ArrayList<>();

        for(int i = start; openBrackets >= 1; i++) {
            thisBlock += triggerStr.charAt(i);

            if(triggerStr.charAt(i) == '{') {
                openBrackets++;
                BlockScope logic = null;

                if(triggerStr.charAt(i-4) == 'T') {
                    logic = BlockScope.NOT;
                }
                else if(triggerStr.charAt(i-4) == 'R') {
                    logic = BlockScope.OR;
                }
                else if(triggerStr.charAt(i-4) == 'D') {
                    logic = BlockScope.AND;
                }
                else {
                    logic = BlockScope.OTHER;
                }
                triggers.add(evaluateBlock(triggerStr, i+1, logic));
                while(openBrackets > 1) {
                    i++;
                    if(triggerStr.charAt(i) == '{') {
                        openBrackets++;
                    }
                    if(triggerStr.charAt(i) == '}') {
                        openBrackets--;
                    }
                }
                openBrackets++;
            }
            if(triggerStr.charAt(i) == '}') {
                openBrackets--;
            }
        }
        if(true) {
            Matcher m_tag = Pattern.compile("(?<=tag \\= )\\S+", Pattern.CASE_INSENSITIVE).matcher(thisBlock);
            while(m_tag.find()) {
                String foundTag = m_tag.group();
                triggers.add(foundTag.equals(this.tag));
            }

            Matcher m_reform = Pattern.compile("(?<=has_reform \\= )\\S+").matcher(thisBlock);
            while(m_reform.find()) {
                String foundReform = m_reform.group();
                if(governmentReform != null) {
                    triggers.add(foundReform.equals(this.governmentReform.getName()));
                }
                else {
                    triggers.add(false);
                }
            }

            Matcher m_cultureGroup = Pattern.compile("(?<=culture_group \\= )\\S+").matcher(thisBlock);
            while(m_cultureGroup.find()) {
                triggers.add(m_cultureGroup.group().equals(this.cultureGroup));
            }

            Matcher m_culture = Pattern.compile("(?<=primary_culture \\= )\\S+").matcher(thisBlock);
            while(m_culture.find()) {
                triggers.add(m_culture.group().equals(this.primaryCulture));
            }

            Matcher m_religionGroup = Pattern.compile("(?<=religion_group \\= )\\S+").matcher(thisBlock);
            while(m_religionGroup.find()) {
                triggers.add(m_religionGroup.group().equals(this.religionGroup));
            }

            Matcher m_religion = Pattern.compile("(?<=religion \\= )\\S+").matcher(thisBlock);
            while(m_religion.find()) {
                triggers.add(m_religion.group().equals(this.religion));
            }

            Matcher m_government = Pattern.compile("(?<=government \\= )\\S+").matcher(thisBlock);
            while(m_government.find()) {
                triggers.add(m_government.group().equals(this.governmentForm));
            }

            Matcher m_capitalRegion = Pattern.compile("(?<=region \\= )\\S+").matcher(thisBlock);
            while(m_capitalRegion.find()) {
                triggers.add(m_capitalRegion.group().equals(this.capitalRegion));
            }

            Matcher m_capitalSuperregion = Pattern.compile("(?<=superregion \\= )\\S+").matcher(thisBlock);
            while(m_capitalSuperregion.find()) {
                triggers.add(m_capitalSuperregion.group().equals(this.capitalSuperregion));
            }

            Matcher m_colonialNation = Pattern.compile("(?<=is_colonial_nation \\= )\\S+").matcher(thisBlock);
            while(m_colonialNation.find()) {
                triggers.add(false);
            }

            Matcher m_clientState = Pattern.compile("(?<=is_client_nation \\= )\\S+").matcher(thisBlock);
            while(m_clientState.find()) {
                triggers.add(false);
            }

            Matcher m_technologyGroup = Pattern.compile("(?<=technology_group \\= )\\S+").matcher(thisBlock);
            while(m_technologyGroup.find()) {
                triggers.add(m_technologyGroup.group().equals(this.technologyGroup));
            }

            Matcher m_federation = Pattern.compile("(?<=is_federation_nation \\= )\\S+").matcher(thisBlock);
            while(m_federation.find()) {
                triggers.add(false);
            }

            Matcher m_dynasty = Pattern.compile("(?<=dynasty \\= )\\S+").matcher(thisBlock);
            while(m_dynasty.find()) {
                triggers.add(true);
            }

            Matcher m_countryFlag = Pattern.compile("(?<=has_country_flag \\= )\\S+").matcher(thisBlock);
            while(m_countryFlag.find()) {
                triggers.add(false);
            }

        }

        switch (logicBlock) {
            case OR:
                for(Boolean trigger : triggers) {
                    if(trigger) {
                        return true;
                    }
                }
                return false;
            case AND:
                for(Boolean trigger : triggers) {
                    if(!trigger) {
                        return false;
                    }
                }
                return true;
            case NOT:
                return !triggers.get(0);
            default:
                return triggers.get(0);
        }
    }

    public void calcCustomCost() {
        double cost = 0.0;
        if(monarch != null) { cost += monarch.getCustomCost(); }
        if(heir != null) { cost += heir.getCustomCost(); }
        if(consort != null) { cost += consort.getCustomCost(); }
        if(governmentReform != null) { cost += governmentReform.getCustomCost(); }
        if(ideaSet != null) { cost += ideaSet.getCustomPoints(); }

        if(technologyGroup.equals("north_american") || technologyGroup.equals("mesoamerican")
            || technologyGroup.equals("south_american") || technologyGroup.equals("andean")) {
            cost += 0.0;
        }
        else if(technologyGroup.equals("aboriginal_tech")) {
            cost += 50.0;
        }
        else if(technologyGroup.equals("high_american")) {
            cost += 75.0;
        }
        else if(capitalContinent.equals("north_america") || capitalContinent.equals("south_america") || capitalContinent.equals("oceania")) {
            if(technologyGroup.equals("central_african") || technologyGroup.equals("sub_saharan") || technologyGroup.equals("polynesian_tech")) {
                cost += 50.0;
            }
            else {
                cost += 75.0;
            }
        }
        else {
            cost += 0.0;
        }

        customCost = cost;
    }

    public void print() {
        System.out.println(name);
        System.out.println("\t" + tag);
        System.out.println("\t" + governmentForm);
        if(governmentReform != null) {
            System.out.println("\t" + governmentReform.getName());
        }
        else {
            System.out.println("\tNULL GOVERNMENT!");
        }
        System.out.println("\t" + governmentRank);
        System.out.println("\t" + technologyGroup);
        System.out.println("\t" + religion + " (" + religionGroup + ")");
        System.out.println("\t" + primaryCulture + " (" + cultureGroup + ")");
        System.out.println("\t" + capital + " (" + capitalArea + " : " + capitalRegion + ": " + capitalSuperregion + ": " + capitalContinent + ")");
        if(monarch != null) {
            System.out.println("\tRuler:");
            monarch.print();
        }
        if(heir != null) {
            System.out.println("\tHeir:");
            heir.print();
        }
        if(consort != null) {
            System.out.println("\tConsort:");
            consort.print();
        }
        System.out.println("\t" + ideaSet.getName());
    }
}
