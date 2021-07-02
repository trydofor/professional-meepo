package pro.fessional.meepo.tmpl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class EachTest extends TraceTest {

    private final Map<String, Object> ctx = new HashMap<String, Object>() {{
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, Object> it = new HashMap<>();
            it.put("name", "name-" + i);
            it.put("code", "code-" + i);
            int c = i % 3;
            if (c == 0) {
                it.put("rem0", true);
            }
            else if (c == 1) {
                it.put("rem1", true);
            }
            list.add(it);
        }
        put("items", list);
    }};

    @Test
    public void printGene() {
        TmplHelp.printGene("/template/each/each-c7s1f3-i.htm");
    }

    @Test
    public void eachC0s1f0() {
        TmplHelp.assertTmpl("/template/each/each-c0s1f0-o.htm", "/template/each/each-c0s1f0-i.htm");
    }

    @Test
    public void eachC7s1f3() {
        TmplHelp.assertTmpl("/template/each/each-c7s1f3-o.htm", "/template/each/each-c7s1f3-i.htm", ctx);
    }

    @Test
    public void eachC7n2f3() {
        TmplHelp.assertTmpl("/template/each/each-c7n2f3-o.htm", "/template/each/each-c7n2f3-i.htm", ctx);
    }

}
