import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreParser {
    private final String baseFilePath = "C:\\Users\\ludvi\\Desktop\\Skola\\AI-Grunder\\EU4 Nation Finder\\src\\";
    private final String gameFolder = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Europa Universalis IV";

    private Map<Integer,Integer> provinceColors = new HashMap<>();
    private ArrayList<Integer> provinceCores = new ArrayList<>();
    private ArrayList<Integer> discoveredBy = new ArrayList<>();
    private HashMap<String,CustomIdea> customIdeas = new HashMap<>();
    private ArrayList<IdeaSet> ideaSets = new ArrayList<>();
    private ArrayList<Nation> nations = new ArrayList<>();
    private HashMap<String,GovernmentReform> governmentReforms = new HashMap<>();
    private HashMap<String,String> religionGroups = new HashMap<>();
    private HashMap<String,String> cultureGroups = new HashMap<>();
    private HashMap<String, ArrayList<Integer>> areas = new HashMap<>();
    private HashMap<String, ArrayList<Integer>> continents = new HashMap<>();
    private HashMap<String, ArrayList<String>> regions = new HashMap<>();
    private HashMap<String, ArrayList<String>> superregions = new HashMap<>();
    private BufferedImage originalMap;
    private BufferedImage newMap;
    private BufferedImage discoverMap;

    private ArrayList<Integer> coreColors = new ArrayList<>();

    public CoreParser() throws IOException {
        for(int i = 0; i <= 4941; i++) {
            provinceCores.add(0);
            discoveredBy.add(0);
        }
        originalMap = ImageIO.read(getClass().getResource("/provinces.png"));
        newMap = ImageIO.read(getClass().getResource("/provinces2.png"));
        discoverMap = ImageIO.read(getClass().getResource("/provinces2.png"));

        coreColors.add(toColor(0,0,0));
        coreColors.add(toColor(0,0,125));
        coreColors.add(toColor(0,0,255));
        coreColors.add(toColor(55,0,155));
        coreColors.add(toColor(155,0,255));
        coreColors.add(toColor(255,0,0));
        coreColors.add(toColor(255,0,255));
        coreColors.add(toColor(255,255,255));
        coreColors.add(toColor(125,0,0));
        coreColors.add(toColor(125,125,0));
        coreColors.add(toColor(0,125,0));
        coreColors.add(toColor(0,125,125));
        coreColors.add(toColor(0,0,125));

        religionGroups.put("catholic","christian");
        religionGroups.put("anglican","christian");
        religionGroups.put("hussite","christian");
        religionGroups.put("protestant","christian");
        religionGroups.put("reformed","christian");
        religionGroups.put("orthodox","christian");
        religionGroups.put("coptic","christian");
        religionGroups.put("sunni","muslim");
        religionGroups.put("shiite","muslim");
        religionGroups.put("ibadi","muslim");
        religionGroups.put("buddhism","eastern");
        religionGroups.put("vajrayana","eastern");
        religionGroups.put("mahayana","eastern");
        religionGroups.put("confucianism","eastern");
        religionGroups.put("shinto","eastern");
        religionGroups.put("hinduism","dharmic");
        religionGroups.put("sikhism","dharmic");
        religionGroups.put("animism","pagan");
        religionGroups.put("shamanism","pagan");
        religionGroups.put("totemism","pagan");
        religionGroups.put("inti","pagan");
        religionGroups.put("nahuatl","pagan");
        religionGroups.put("mesoamerican_religion","pagan");
        religionGroups.put("norse_pagan_reformed","pagan");
        religionGroups.put("tengri_pagan_reformed","pagan");
        religionGroups.put("dreamtime","pagan");
        religionGroups.put("jewish","jewish_group");
        religionGroups.put("zoroastrian","zoroastrian_group");
    }

    private int toColor(int r, int g, int b) {
        r = (r << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        g = (g << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        b = b & 0x000000FF; //Mask out anything not blue.
        int color = 0xFF000000 | r | g | b;

        return color;
    }

    public void parseColors(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String readLine;

        while((readLine = reader.readLine()) != null) {
            int provId = 0;
            ArrayList<Integer> rgb = new ArrayList<>();
            int c = 0;

            for(int i = 0; i < 4; i++) {
                String thing = "";
                while(readLine.charAt(c) != ';') {
                    thing += readLine.charAt(c);
                    c++;
                }
                if(i > 0) {
                    rgb.add(Integer.parseInt(thing));
                }
                else {
                    provId = Integer.parseInt(thing);
                }
                c++;
            }

            int r = (rgb.get(0) << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
            int g = (rgb.get(1) << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
            int b = rgb.get(2) & 0x000000FF; //Mask out anything not blue.
            int color = 0xFF000000 | r | g | b;

            provinceColors.put(color, provId);
        }
    }

    public void parseCores(String folderPath) throws IOException {
        File folder = new File(folderPath);
        for(File file : folder.listFiles()) {
            String[] fileNameParts = file.getName().split("-|\\s");
            int provId = Integer.parseInt(fileNameParts[0]);
            int[] provInfo = parseProvinceInfo(folderPath + "/" + file.getName());
            //for(int i = 0; i < 2; i++) {
            //    System.out.println(provInfo[i]);
            //}
            provinceCores.set(provId, provInfo[0]);
            discoveredBy.set(provId, provInfo[1]);
        }
    }

    private int[] parseProvinceInfo(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String readLine;

        int[] infos = new int[2];
        while((readLine = reader.readLine()) != null) {
            if(readLine.length() > 0 && readLine.charAt(0) == '1') {
                if(Integer.parseInt(readLine.substring(0,4)) > 1444) {
                    break;
                }
            }
            if(readLine.contains("add_core") && !readLine.contains("#add_core")) {
                infos[0]++;
            }
            if(readLine.contains("remove_core") && !readLine.contains("#remove_core")) {
                infos[0]--;
            }
            if(readLine.contains("discovered_by")) {
                if(readLine.length() >= readLine.indexOf("discovered_by") + 20 && readLine.charAt(readLine.indexOf("discovered_by") + 19) != ' ') {
                    infos[1]++;
                }
            }
        }

        return infos;
    }



    public void recolorMap() throws IOException {
        for(int x = 0; x < originalMap.getWidth(); x++) {
            for(int y = 0; y < originalMap.getHeight(); y++) {
                int rgb = originalMap.getRGB(x,y);
                int provId = provinceColors.get(rgb);
                int cores = provinceCores.get(provId);
                newMap.setRGB(x, y, coreColors.get(cores));
                discoverMap.setRGB(x, y, coreColors.get(discoveredBy.get(provId)));
            }
        }

        Graphics2D g = newMap.createGraphics();
        File newMapFile = new File("coreMap.png");
        ImageIO.write(newMap, "png", newMapFile);

        g = discoverMap.createGraphics();
        newMapFile = new File("discoverMap.png");
        ImageIO.write(discoverMap, "png", newMapFile);
    }

    public void printProvinceInfo() {
        int[] num = new int[6];
        int max = 0;
        for(int i = 1; i <= 4941; i++) {
            num[provinceCores.get(i)]++;
            if(provinceCores.get(i) == 5) {
                System.out.println(i);
            }
            if (discoveredBy.get(i) > max) {
                max = discoveredBy.get(i);
            }
            //ArrayList<Integer> rgb = provinceColors.get(i-1);
            System.out.println(i + ": Cores: " + provinceCores.get(i) + ", Discoveres: " + discoveredBy.get(i));
        }
        System.out.println(max);
        for(int i = 0; i < 6; i++) {
            System.out.println(num[i]);
        }
    }

    public void parseCustomIdeas() throws IOException {
        File folder = new File(gameFolder + "/common/custom_ideas");
        for(File file : folder.listFiles()) {
            String fileName = gameFolder + "/common/custom_ideas/" + file.getName();
            //System.out.println(file.getName());

            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readLine;
            String prevLine = "";

            Pattern p_category = Pattern.compile("(?<=category \\= ).{3}");
            Pattern p_custom = Pattern.compile("(?<=custom_idea_)\\S*");
            String category = null;
            String name = null;
            Double modifier = null;
            Integer baseCost = null;
            Integer level2Cost = null;
            Integer maxLevel = 4;

            while((readLine = reader.readLine()) != null) {
                /*
                Matcher m2 = Pattern.compile("(?<!_)chance \\= ").matcher(readLine);
                if(m2.find()) {

                }

                 */

                Matcher m = p_category.matcher(readLine);
                if(m.find()) {
                    category = m.group();
                }

                m = p_custom.matcher(prevLine);
                Matcher mc = Pattern.compile("(?<=custom_)\\S*").matcher(prevLine);
                if(m.find() || mc.find()) {
                    if(name != null) {
                        if(baseCost == null) {
                            baseCost = 0;
                            if(level2Cost == null) {
                                level2Cost = 5;
                            }
                        }
                        if(level2Cost == null) {
                            level2Cost = baseCost;
                            baseCost = 0;
                        }

                        customIdeas.put(name, new CustomIdea(name, category, modifier, baseCost, level2Cost, maxLevel));
                        ;
                        name = null;
                        modifier = null;
                        baseCost = null;
                        level2Cost = null;
                        maxLevel = 4;
                    }

                    m = Pattern.compile("(?<= \\= )\\S*").matcher(readLine);
                    Matcher mn = Pattern.compile("\\S+(?= \\= )").matcher(readLine);

                    if(m.find()) {
                        String modString = m.group();
                        if(modString.equals("yes")) {
                            modifier = 1.0;
                        }
                        else if(modString.equals("no")) {
                            modifier = 0.0;
                        }
                        else {
                            modifier = Double.parseDouble(modString);
                        }
                    }
                    if(mn.find()) {
                        name = mn.group();
                    }
                }

                m = Pattern.compile("(?<=level_cost_1 \\= )\\d+").matcher(readLine);
                if(m.find()) {
                    baseCost = Integer.parseInt(m.group());
                }

                m = Pattern.compile("(?<=level_cost_2 \\= )\\d+").matcher(readLine);
                if(m.find()) {
                    level2Cost = Integer.parseInt(m.group());
                }

                m = Pattern.compile("(?<=max_level \\= )\\d+").matcher(readLine);
                if(m.find()) {
                    maxLevel = Integer.parseInt(m.group());
                }

                prevLine = readLine;
            }

            if(baseCost == null) {
                baseCost = 0;
                if(level2Cost == null) {
                    level2Cost = 5;
                }
            }
            if(level2Cost == null) {
                level2Cost = baseCost;
                baseCost = 0;
            }

            customIdeas.put(name, new CustomIdea(name, category, modifier, baseCost, level2Cost, maxLevel));
        }
    }

    public void printCustomIdeas() {
        for(String idea : customIdeas.keySet()) {
            customIdeas.get(idea).print();
            System.out.println();
        }
    }

    public void printIdeaSets() {
        for(IdeaSet ideaSet : ideaSets) {
            ideaSet.print();
            System.out.println();
        }
    }

    public void printNations() {
        for(Nation nation : nations) {
            nation.print();
            System.out.println();
        }
    }

    public void printReligions() {
        for(String religion : religionGroups.keySet()) {
            System.out.println(religion + " - " + religionGroups.get(religion));
        }
        System.out.println();
    }

    public void printCultures() {
        for(String culture : cultureGroups.keySet()) {
            System.out.println(culture + " - " + cultureGroups.get(culture));
        }
        System.out.println();
    }

    public void printGovReforms() {
        for(String reform : governmentReforms.keySet()) {
            System.out.println(reform + " - " + governmentReforms.get(reform).getCustomCost());
        }
        System.out.println();
    }

    public void printGeography() {
        for(String superregion : superregions.keySet()) {
            System.out.println(superregion);
            for(String region : superregions.get(superregion)) {
                System.out.println("\t" + region);
                for(String area : regions.get(region)) {
                    System.out.println("\t\t" + area);
                    for(Integer province : areas.get(area)) {
                        System.out.println("\t\t\t" + province);
                    }
                }
            }
        }
        System.out.println();
    }

    public void parseIdeaSets() throws IOException {
        IdeaSet defaultIdeas = null;
        File folder = new File(gameFolder + "/common/ideas");
        for(File file : folder.listFiles()) {
            if(!file.getName().contains("basic") && !file.getName().contains("compatibility")) {
                String fileName = gameFolder + "/common/ideas/" + file.getName();

                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String readLine;
                String prevLine = "";
                Matcher m;

                String name = null;
                ArrayList<HashMap<String,Double>> ideas = null;
                String trigger = "";

                while((readLine = reader.readLine()) != null) {
                    m = Pattern.compile("^\\S+(?=_ideas|_Ideas)").matcher(readLine);
                    if(m.find()) {
                        if(name != null) {
                            IdeaSet ideaSet = new IdeaSet(name, ideas, trigger);
                            ideaSet.calcCustomPoints(customIdeas);
                            ideaSets.add(ideaSet);
                        }

                        name = m.group() + "_ideas";
                        ideas = new ArrayList<>();
                        trigger = "";
                    }
                    else if(readLine.contains("trigger = ")) {
                        int danglingBrackets = 1;
                        trigger = readLine;
                        while(danglingBrackets > 0) {
                            readLine = reader.readLine();
                            trigger += readLine;
                            if(readLine.contains("{")) {
                                danglingBrackets++;
                            }
                            if(readLine.contains("}")) {
                                danglingBrackets--;
                            }
                        }
                    }
                    else if(readLine.contains("{")) {
                        //System.out.println(readLine);
                        HashMap<String,Double> idea = new HashMap<>();
                        int danglingBrackets = 1;
                        while(danglingBrackets > 0) {
                            readLine = reader.readLine();
                            //System.out.println(readLine);
                            m = Pattern.compile("(?<= \\= )\\S*").matcher(readLine);
                            Matcher mn = Pattern.compile("\\S+(?= \\= )").matcher(readLine);
                            if(m.find() && mn.find()) {
                                String modString = m.group();
                                if(modString.equals("yes")) {
                                    idea.put(mn.group(), 1.0);
                                }
                                else if(modString.equals("no")) {
                                    idea.put(mn.group(), 0.0);
                                }
                                else {
                                    idea.put(mn.group(), Double.parseDouble(modString));
                                }
                            }
                            if(readLine.contains("{")) {
                                danglingBrackets++;
                            }
                            if(readLine.contains("}")) {
                                danglingBrackets--;
                            }
                        }
                        ideas.add(idea);
                    }
                }
                IdeaSet ideaSet = new IdeaSet(name, ideas, trigger);
                ideaSet.calcCustomPoints(customIdeas);
                if(ideaSet.getName().equals("default_ideas")) {
                    defaultIdeas = ideaSet;
                }
                else {
                    ideaSets.add(ideaSet);
                }
            }
        }
        ideaSets.add(defaultIdeas);
    }

    private String makePattern(String centralWord, boolean hasParantheses) {
        if(hasParantheses) {
            return "(?<=" + centralWord + "\\s=\\s\"|" + centralWord + "=\\s\"|" + centralWord + "\\s=\"|" + centralWord + "=\").+(?=\")";
        }
        else {
            return "(?<=" + centralWord + "\\s=\\s|" + centralWord + "=\\s|" + centralWord + "\\s=|" + centralWord + "=)\\S+";
        }
    }

    public void parseGovernmentReforms() throws IOException {
        File folder = new File(gameFolder + "/common/government_reforms");
        for (File file : folder.listFiles()) {
            String fileName = gameFolder + "/common/government_reforms/" + file.getName();

            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readLine;

            String reform = null;
            boolean legacyGov = false;
            int cost = -1;

            while ((readLine = reader.readLine()) != null) {
                Matcher m = Pattern.compile("^\\S+").matcher(readLine);
                if(m.find()) {
                    String str = m.group();
                    if(str.charAt(0) != '#') {
                        if(reform != null) {
                            GovernmentReform governmentReform = new GovernmentReform(reform, cost);
                            governmentReforms.put(reform, governmentReform);
                        }
                        reform = str;
                        legacyGov = false;
                        cost = -1;
                    }
                }

                m = Pattern.compile(makePattern("legacy_government", false)).matcher(readLine);
                if(m.find()) {
                    if(m.group().equals("yes")) {
                        legacyGov = true;
                    }
                    else {
                        legacyGov = false;
                    }
                }
                m = Pattern.compile(makePattern("nation_designer_cost", false)).matcher(readLine);
                if(m.find()) {
                    cost = Integer.parseInt(m.group());
                }
            }

            if(reform != null) {
                GovernmentReform governmentReform = new GovernmentReform(reform, cost);
                governmentReforms.put(reform, governmentReform);
            }
        }
    }

    public void parseCountries() throws IOException {
        File folder = new File(gameFolder + "/history/countries");
        for (File file : folder.listFiles()) {
            String fileName = gameFolder + "/history/countries/" + file.getName();

            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readLine;
            String prevLine = "";
            Matcher m;

            String tag = file.getName().substring(0,3);
            String name = file.getName().substring(6, file.getName().length()-4);
            String governmentForm = null;
            GovernmentReform governmentReform = null;
            int governmentRank = -1;
            String technologyGroup = null;
            String religion = null;
            String primaryCulture = null;
            int capital = -1;

            Ruler ruler = null;
            Ruler heir = null;
            Ruler consort = null;
            String rulerName = null;
            String heirMonarchName = null;
            String rulerDynasty = null;
            String rulerType = null;
            String rulerGender = "male";
            int rulerAge = -1;
            int rulerAdm = -1;
            int rulerDip = -1;
            int rulerMil = -1;
            String rulerCulture = null;
            String rulerReligion = null;
            ArrayList<String> rulerPersonalities = new ArrayList<>();
            ArrayList<String> heirPersonalities = new ArrayList<>();
            ArrayList<String> consortPersonalities = new ArrayList<>();
            boolean regent = false;
            int currentYear = -1;
            int monarchCreationYear = -1;

            while ((readLine = reader.readLine()) != null) {
                m = Pattern.compile("^\\d{4}\\.\\d").matcher(readLine);
                if(m.find()) {
                    currentYear = Integer.parseInt(readLine.substring(0,4));
                    if(Integer.parseInt(readLine.substring(0,4)) == 1444) {
                        if(readLine.charAt(6) != '.') {
                            if(Integer.parseInt(readLine.substring(5,7)) == 11) {
                                if(readLine.charAt(9) != ' ' && Integer.parseInt(readLine.substring(8,10)) > 11) {
                                    break;
                                }
                            }
                            else if(Integer.parseInt(readLine.substring(5,7)) > 11) {
                                break;
                            }
                        }
                    }
                    else if(Integer.parseInt(readLine.substring(0,4)) > 1444) {
                        break;
                    }
                }

                m = Pattern.compile(makePattern("government", false)).matcher(readLine);
                if(m.find()) {
                    governmentForm = m.group();
                }
                m = Pattern.compile(makePattern("add_government_reform", false)).matcher(readLine);
                if(m.find()) {
                    governmentReform = governmentReforms.get(m.group());
                }
                m = Pattern.compile(makePattern("government_rank", false)).matcher(readLine);
                if(m.find()) {
                    governmentRank = Integer.parseInt(m.group());
                }
                m = Pattern.compile(makePattern("technology_group", false)).matcher(readLine);
                if(m.find()) {
                    technologyGroup = m.group();
                }
                m = Pattern.compile(makePattern("religion", false)).matcher(readLine);
                if(m.find()) {
                    religion = m.group();
                }
                m = Pattern.compile(makePattern("primary_culture", false)).matcher(readLine);
                if(m.find()) {
                    primaryCulture = m.group();
                }
                m = Pattern.compile(makePattern("capital", false)).matcher(readLine);
                if(m.find()) {
                    capital = Integer.parseInt(m.group());
                }

                Matcher mm = Pattern.compile("(?<=monarch \\= )\\S+").matcher(readLine);
                Matcher mh = Pattern.compile("(?<=heir \\= )\\S+").matcher(readLine);
                Matcher mc = Pattern.compile("(?<=queen \\= )\\S+").matcher(readLine);
                Matcher ml = Pattern.compile("(?<=leader \\= )\\S+").matcher(readLine);
                boolean mmFind = mm.find();
                boolean mhFind = mh.find();
                boolean mcFind = mc.find();
                boolean mlFind = ml.find();
                if(mmFind || mhFind || mcFind || mlFind) {
                    if(rulerType != null) {
                        if(rulerAge == -1) {
                            rulerAge = 1444 - monarchCreationYear + 20;
                        }
                        if(rulerType.equals("monarch")) {
                            if(regent) {
                                rulerAge = 30;
                            }
                            if(heirMonarchName != null && heirMonarchName.equals(rulerName)) {
                                heir = null;
                            }
                            if(rulerName == null) {
                                if(regent) {
                                    rulerName = "Regency";
                                }
                                else {
                                    System.out.println("Null monarch: " + tag);
                                }
                            }
                            ruler = new Ruler(rulerName, rulerType, rulerGender, rulerDynasty, rulerAge, rulerAdm, rulerDip,
                                    rulerMil, rulerCulture, rulerReligion, rulerPersonalities);
                        }
                        else if(rulerType.equals("heir")) {
                            if(rulerName == null) {
                                System.out.println("Null heir: " + tag);
                            }
                            heir = new Ruler(rulerName, rulerType, rulerGender, rulerDynasty, rulerAge, rulerAdm, rulerDip,
                                    rulerMil, rulerCulture, rulerReligion, heirPersonalities);
                        }
                        else if(rulerType.equals("consort")) {
                            if(rulerName == null) {
                                System.out.println("Null consort: " + tag);
                            }
                            consort = new Ruler(rulerName, rulerType, rulerGender, rulerDynasty, rulerAge, rulerAdm, rulerDip,
                                    rulerMil, rulerCulture, rulerReligion, consortPersonalities);
                        }
                    }
                    rulerName = null;
                    rulerGender = "male";
                    rulerDynasty = null;
                    rulerAge = -1;
                    rulerAdm = -1;
                    rulerDip = -1;
                    rulerMil = -1;
                    rulerCulture = primaryCulture;
                    rulerReligion = null;
                    regent = false;
                    monarchCreationYear = currentYear;

                    if(mmFind) {
                        rulerType = "monarch";
                        rulerPersonalities = new ArrayList<>();
                    }
                    else if(mhFind) {
                        rulerType = "heir";
                        heirPersonalities = new ArrayList<>();
                    }
                    else if(mcFind) {
                        rulerType = "consort";
                        consortPersonalities = new ArrayList<>();
                    }
                    else {
                        rulerType = null;
                    }
                }

                m = Pattern.compile(makePattern("\\sname", true)).matcher(readLine);
                if(m.find()) {
                    rulerName = m.group();
                }
                m = Pattern.compile(makePattern("monarch_name", true)).matcher(readLine);
                if(m.find()) {
                    heirMonarchName = m.group();
                }
                m = Pattern.compile(makePattern("dynasty", true)).matcher(readLine);
                if(m.find()) {
                    rulerDynasty = m.group();
                }
                m = Pattern.compile(makePattern("female", false)).matcher(readLine);
                if(m.find()) {
                    if(m.group().equals("yes")) {
                        rulerGender = "female";
                    }
                    else {
                        rulerGender = "male";
                    }
                }
                m = Pattern.compile(makePattern("birth_date", false)).matcher(readLine);
                if(m.find()) {
                    String birthDate = m.group();
                    int years = 1444 - Integer.parseInt(birthDate.substring(0,4));
                    int months = 11;
                    int days = 11;
                    if(birthDate.charAt(6) == '.') {
                        months -= Integer.parseInt(birthDate.substring(5,6));
                        days -= Integer.parseInt(birthDate.substring(7));
                    }
                    else {
                        months -= Integer.parseInt(birthDate.substring(5,7));
                        days -= Integer.parseInt(birthDate.substring(8));
                    }
                    if(days < 0) {
                        months -= 1;
                    }
                    if(months < 0) {
                        years -= 1;
                    }
                    rulerAge = years;
                }
                m = Pattern.compile(makePattern("adm", false)).matcher(readLine);
                if(m.find()) {
                    rulerAdm = Integer.parseInt(m.group());
                }
                m = Pattern.compile(makePattern("dip", false)).matcher(readLine);
                if(m.find()) {
                    rulerDip = Integer.parseInt(m.group());
                }
                m = Pattern.compile(makePattern("mil", false)).matcher(readLine);
                if(m.find()) {
                    rulerMil = Integer.parseInt(m.group());
                }
                m = Pattern.compile(makePattern("\\sculture", false)).matcher(readLine);
                if(m.find()) {
                    rulerCulture = m.group();
                }
                m = Pattern.compile(makePattern("add_ruler_personality", false)).matcher(readLine);
                if(m.find()) {
                    rulerPersonalities.add(m.group());
                }
                m = Pattern.compile(makePattern("add_heir_personality", false)).matcher(readLine);
                if(m.find()) {
                    heirPersonalities.add(m.group());
                }
                m = Pattern.compile(makePattern("add_queen_personality", false)).matcher(readLine);
                if(m.find()) {
                    consortPersonalities.add(m.group());
                }
                m = Pattern.compile(makePattern("regent", false)).matcher(readLine);
                if(m.find()) {
                    if(m.group().equals("yes")) {
                        regent = true;
                    }
                }
            }
            if(rulerType != null) {
                if(rulerAge == -1) {
                    rulerAge = 1444 - monarchCreationYear + 20;
                }
                if(rulerType.equals("monarch")) {
                    if(regent) {
                        rulerAge = 30;
                    }
                    if(heirMonarchName != null && heirMonarchName.equals(rulerName)) {
                        heir = null;
                    }
                    if(rulerName == null) {
                        if(regent) {
                            rulerName = "Regency";
                        }
                        else {
                            System.out.println("Null monarch: " + tag);
                        }
                    }
                    ruler = new Ruler(rulerName, rulerType, rulerGender, rulerDynasty, rulerAge, rulerAdm, rulerDip,
                            rulerMil, rulerCulture, rulerReligion, rulerPersonalities);
                }
                else if(rulerType.equals("heir")) {
                    if(rulerName == null) {
                        System.out.println("Null heir: " + tag);
                    }
                    heir = new Ruler(rulerName, rulerType, rulerGender, rulerDynasty, rulerAge, rulerAdm, rulerDip,
                            rulerMil, rulerCulture, rulerReligion, heirPersonalities);
                }
                else if(rulerType.equals("consort")) {
                    if(rulerName == null) {
                        System.out.println("Null consort: " + tag);
                    }
                    consort = new Ruler(rulerName, rulerType, rulerGender, rulerDynasty, rulerAge, rulerAdm, rulerDip,
                            rulerMil, rulerCulture, rulerReligion, consortPersonalities);
                }
            }
            if(ruler != null) {
                ruler.setPersonalities(rulerPersonalities);
            }
            if(heir != null) {
                heir.setPersonalities(heirPersonalities);
            }
            if(consort != null) {
                consort.setPersonalities(consortPersonalities);
            }

            String capitalArea = null;
            String capitalRegion = null;
            String capitalSuperregion = null;
            String capitalContinent = null;

            for(String area : areas.keySet()) {
                if(areas.get(area).contains(capital)) {
                    capitalArea = area;
                    break;
                }
            }
            for(String region : regions.keySet()) {
                if(regions.get(region).contains(capitalArea)) {
                    capitalRegion = region;
                    break;
                }
            }
            for(String superregion : superregions.keySet()) {
                if(superregions.get(superregion).contains(capitalRegion)) {
                    capitalSuperregion = superregion;
                    break;
                }
            }
            for(String continent : continents.keySet()) {
                if(continents.get(continent).contains(capital)) {
                    capitalContinent = continent;
                    break;
                }
            }

            if(tag == "HUN" || tag == "CRO") {
                ruler = new Ruler("(Interregnum)", "monarch", "male", null, 30, 0,
                        0, 0, "hungarian", null, new ArrayList<String>());
            }
            else if(tag == "POL") {
                ruler = new Ruler("(Interregnum)", "monarch", "male", null, 30, 0,
                        0, 0, "polish", null, new ArrayList<String>());
            }
            else if(tag == "BOH") {
                ruler = new Ruler("Landfriedens (Interregnum)", "monarch", "male", null, 30, 2,
                        2, 2, "czech", null, new ArrayList<String>());
            }
            else if(tag == "SWE" || tag == "NOR") {
                ruler = new Ruler("Christopher III", "monarch", "male", "von Wittelsbach", 28, 3,
                        2, 2, "bavarian", null, new ArrayList<String>(Arrays.asList("infertile_personality")));
            }
            else if(tag == "FLA" || tag == "BRB" || tag == "HOL") {
                ruler = new Ruler("Philippe III", "monarch", "male", "de Bourgogne", 48, 5,
                        5, 5, "burgundian", null,
                        new ArrayList<String>(Arrays.asList("calm_personality", "well_connected_personality", "well_advised_personality")));
                heir = new Ruler("Charles", "heir", "male", "de Bourgogne", 11, 2,
                        0, 4, "burgundian", null,
                        new ArrayList<String>(Arrays.asList("bold_fighter_personality")));
                consort = new Ruler("Isabella", "consort", "female", "de Avis", 47, 3,
                        4, 1, "portuguese", null,
                        new ArrayList<String>());
            }
            else if(tag == "NAP") {
                ruler = new Ruler("Alfons V", "monarch", "male", "de Trastámara", 48, 2,
                        4, 6, "aragonese", null,
                        new ArrayList<String>(Arrays.asList("well_connected_personality", "architectural_visionary_personality")));
                heir = new Ruler("Joan", "heir", "male", "de Trastámara", 47, 4,
                        1, 0, "aragonese", null,
                        new ArrayList<String>());
                consort = new Ruler("Maria", "consort", "female", "de Trastámara", 43, 4,
                        4, 3, "castilian", null,
                        new ArrayList<String>());
            }
            else if(tag == "LOR") {
                ruler = new Ruler("René I", "monarch", "male", "de Valois", 35, 3,
                        5, 2, "occitan", null,
                        new ArrayList<String>());
                heir = new Ruler("Charles", "heir", "male", "de Valois", 8, 2,
                        2, 2, "occitan", null,
                        new ArrayList<String>());
                consort = new Ruler("Isabella", "consort", "female", "de Metz", 44, 0,
                        2, 1, "burgundian", null,
                        new ArrayList<String>());
            }
            else if(tag == "THU") {
                ruler = new Ruler("Friedrich II", "monarch", "male", "von Wettin", 32, 3,
                        4, 0, "saxon", null,
                        new ArrayList<String>(Arrays.asList("fertile_personality", "industrious_personality")));
                heir = new Ruler("Albrecht", "heir", "male", "von Wettin", 1, 2,
                        2, 2, "saxon", null,
                        new ArrayList<String>());
                consort = new Ruler("Margaret", "consort", "female", "von Habsburg", 28, 2,
                        1, 5, "austrian", null,
                        new ArrayList<String>());
            }
            else if(tag == "BYT") {
                ruler = new Ruler("Albrecht I Achilles", "monarch", "male", "von Hohenzollern", 30, 2,
                        2, 1, "franconian", null,
                        new ArrayList<String>(Arrays.asList("inspiring_leader_personality")));
            }

            if(governmentRank == -1) {
                governmentRank = 2;
            }
            Nation nation = new Nation(tag, name, governmentForm, governmentReform, governmentRank, technologyGroup,
                    religion, religionGroups.get(religion), primaryCulture, cultureGroups.get(primaryCulture), capital,
                    capitalArea, capitalRegion, capitalSuperregion, capitalContinent, ruler, heir, consort);
            nation.calcIdeaSet(ideaSets);
            nations.add(nation);

        }
    }

    public void parseCultures() throws IOException {
        File folder = new File(gameFolder + "/common/cultures");
        for (File file : folder.listFiles()) {
            String fileName = gameFolder + "/common/cultures/" + file.getName();

            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String readLine;
            String prevLine = "";
            Matcher m;

            String cultureGroup = null;
            String culture = null;

            while ((readLine = reader.readLine()) != null) {
                //System.out.println(readLine);
                m = Pattern.compile("^\\S+(?=\\s\\=)").matcher(readLine);
                if(m.find()) {
                    cultureGroup = m.group();
                    //System.out.println(cultureGroup);
                }
                m = Pattern.compile("(?<=\t)\\S+(?=\\s\\=)").matcher(readLine);
                if(m.find()) {
                    String str = m.group();
                    if(!str.contains("graphical_culture") && !str.contains("_names") && !str.startsWith("#")) {
                        culture = str;
                        //System.out.println(culture);
                        cultureGroups.put(culture, cultureGroup);

                        int openBrackets = 1;
                        while(openBrackets >= 1) {
                            readLine = reader.readLine();
                            if(readLine.contains("{")) {
                                openBrackets++;
                            }
                            if(readLine.contains("}")) {
                                openBrackets--;
                            }
                        }
                    }
                }
            }
        }
    }

    public void parseGeography() throws IOException {
        String fileName = gameFolder + "/map/area.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String readLine;
        Matcher m;

        String area = null;
        ArrayList<Integer> provinces = new ArrayList<>();

        while ((readLine = reader.readLine()) != null) {
            if(!readLine.startsWith("#")) {
                m = Pattern.compile("^\\S+\\s*(?=\\=)").matcher(readLine);
                if (m.find()) {
                    if (area != null) {
                        areas.put(area, provinces);
                        provinces = new ArrayList<>();
                    }
                    area = m.group();
                    area = area.replace(" ","");
                    //System.out.println(area);
                } else {
                    if (!readLine.contains("color")) {
                        m = Pattern.compile("\\d+").matcher(readLine);
                        while (m.find()) {
                            provinces.add(Integer.parseInt(m.group()));
                            //System.out.println(provinces.get(provinces.size()-1));
                        }
                    }
                }
            }
            else if (readLine.contains("deprecated")) {
                break;
            }
        }
        areas.put(area, provinces);

        fileName = gameFolder + "/map/region.txt";
        reader = new BufferedReader(new FileReader(fileName));

        String region = null;
        ArrayList<String> areaNames = new ArrayList<>();

        while ((readLine = reader.readLine()) != null) {
            if(!readLine.startsWith("#")) {
                m = Pattern.compile("^\\S+\\s*(?=\\=)").matcher(readLine);
                if (m.find()) {
                    if (region != null) {
                        regions.put(region, areaNames);
                        areaNames = new ArrayList<>();
                    }
                    region = m.group();
                    region = region.replace(" ","");
                    //System.out.println(region);
                } else {
                    m = Pattern.compile("(?<=\t)areas\\s*(?=\\=)").matcher(readLine);
                    if(m.find()) {
                        while (!(readLine = reader.readLine()).contains("}")) {
                            if(!readLine.contains("#")) {
                                m = Pattern.compile("\\S+").matcher(readLine);
                                if(m.find()) {
                                    areaNames.add(m.group());
                                    //System.out.println(areaNames.get(areaNames.size()-1));
                                }
                            }
                        }
                    }
                }
            }
        }
        regions.put(region, areaNames);

        fileName = gameFolder + "/map/superregion.txt";
        reader = new BufferedReader(new FileReader(fileName));

        String superregion = null;
        ArrayList<String> regionNames = new ArrayList<>();

        while ((readLine = reader.readLine()) != null) {
            if(!readLine.startsWith("#")) {
                m = Pattern.compile("^\\S+\\s*(?=\\=)").matcher(readLine);
                if (m.find()) {
                    if (superregion != null) {
                        superregions.put(superregion, regionNames);
                        regionNames = new ArrayList<>();
                    }
                    superregion = m.group();
                    //System.out.println(superregion);
                    superregion = superregion.replace(" ","");
                    //System.out.println(superregion);
                } else {
                    if(!readLine.contains("}")) {
                        m = Pattern.compile("\\S+").matcher(readLine);
                        String regionName = null;
                        if(m.find() && !(regionName = m.group()).equals("restrict_charter")) {
                            regionNames.add(regionName);
                        }
                    }
                }
            }
        }
        superregions.put(superregion, regionNames);

        fileName = gameFolder + "/map/continent.txt";
        reader = new BufferedReader(new FileReader(fileName));

        String continent = null;
        provinces = new ArrayList<>();

        while ((readLine = reader.readLine()) != null) {
            m = Pattern.compile("^\\S+").matcher(readLine);
            if(m.find()) {
                if(continent != null) {
                    continents.put(continent, provinces);
                    continent = null;
                    provinces = new ArrayList<>();
                }
                String str = m.group();
                if(str.charAt(0) != '#' && str.charAt(0) != '}' && !str.contains("island_check_provinces") && !str.contains("new_world")) {
                    continent = str;
                }
            }

            m = Pattern.compile("(?<=\\s)\\d+(?=\\s|$)").matcher(readLine);
            while(m.find()) {
                provinces.add(Integer.parseInt(m.group()));
            }
        }
        if(continent != null) {
            continents.put(continent, provinces);
        }
    }
}

