import java.io.IOException;

public class Main {
    private static final String baseFilePath = "C:\\Users\\ludvi\\Desktop\\Skola\\AI-Grunder\\EU4 Nation Finder\\src\\";

    public static void main(String[] args) {
        CoreParser coreParser = null;
        try {
            coreParser = new CoreParser();
            //coreParser.parseColors(baseFilePath + "ProvinceColors.txt");
            //coreParser.parseCores(baseFilePath + "provinces");
            //coreParser.recolorMap();
            coreParser.parseCustomIdeas();
            coreParser.parseIdeaSets();
            coreParser.parseCultures();
            coreParser.parseGeography();
            coreParser.parseGovernmentReforms();
            coreParser.parseCountries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //coreParser.printProvinceInfo();
        //coreParser.printCustomIdeas();
        //coreParser.printIdeaSets();
        coreParser.printNations();
        //coreParser.printReligions();
        //coreParser.printCultures();
        //coreParser.printGeography();
        //coreParser.printGovReforms();
    }
}