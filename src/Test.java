import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String str1 = "1111 ";
        String str2 = "2222";
        String str3 = "3333t";

        Matcher m1 = Pattern.compile("\\d+(?=\\s|$)").matcher(str1);
        if(m1.find()) {
            System.out.println("Str1: " + m1.group());
        }
        Matcher m2 = Pattern.compile("\\d+(?=\\s|$)").matcher(str2);
        if(m2.find()) {
            System.out.println("Str2: " + m2.group());
        }
        Matcher m3 = Pattern.compile("\\d+(?=\\s|$)").matcher(str3);
        if(m3.find()) {
            System.out.println("Str3: " + m3.group());
        }
    }
}
