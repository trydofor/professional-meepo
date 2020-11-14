package pro.fessional.meepo.benchmark;

import pro.fessional.meepo.sack.Gene;

import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-28
 */

public class MeepoAsyncProfile {

    private Gene template;
    private Map<String, Object> context;

    public void setup() {
        context = Stock.mockContext();
        template = pro.fessional.meepo.Meepo.parse("classpath:/template/jmh/stocks.meepo.html");
    }

    public String benchmark() {
        return template.merge(context);
    }

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo", "error");
        MeepoAsyncProfile profile = new MeepoAsyncProfile();
        profile.setup();
        int end = 1000_0000;
        System.out.println("don setup, loop= " + end);
        for (int i = 1; i <= end; i++) {
            profile.benchmark();
            if (i % 1_0000 == 0) {
                System.out.println(" - looped= " + i);
            }
        }
    }
}
