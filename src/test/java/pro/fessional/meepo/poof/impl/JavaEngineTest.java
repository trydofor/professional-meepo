package pro.fessional.meepo.poof.impl;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.poof.RnaWarmed;
import pro.fessional.meepo.poof.impl.java.JavaEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-10-31
 */
public class JavaEngineTest extends TraceTest {

    @Test
    public void eval() {
        JavaEngine engine = new JavaEngine();
        HashMap<String, Object> ctx = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        ctx.put("NOW", now);
        String body = "import java.time.LocalDateTime;LocalDateTime.now();return ctx.get(\"NOW\")";
        RnaWarmed warmed = engine.warm("java", body);
        Object o1 = engine.eval(ctx, warmed, false);
        Object o2 = engine.eval(ctx, warmed, false);
        Assert.assertSame(now, o1);
        Assert.assertSame(now, o2);

        LocalDate date = LocalDate.parse("2020-07-09");
        LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ldt.format(fmt);
    }
}