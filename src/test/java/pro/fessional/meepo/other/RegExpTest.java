package pro.fessional.meepo.other;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class RegExpTest {

    public static void main(String[] args) {
        Pattern find = Pattern.compile("a+");
        String txt = "aaa|aa|a|";
        Matcher m = find.matcher(txt);
        while (m.find()) {
            int p0, p1;
            if (m.groupCount() > 0) {
                p0 = m.start(1);
                p1 = m.end(1);
            } else {
                p0 = m.start();
                p1 = m.end();
            }
            System.out.println(txt.substring(p0, p1));
        }
    }
}
