package pro.fessional.meepo.poof;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.eval.JavaEval;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2020-11-14
 */
public class RnaManagerTest {

    @Test
    public void testFunNow1() {
        String meepo = "/* HI-MEEPO */\n/* RNA:USE /now/fun:now/*/now";
        Gene gene = Parser.parse(meepo);
        Map<String, Object> ctx = new HashMap<>();
        String out = gene.merge(ctx);
        System.out.println(out);
        Pattern ptn = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        assertTrue(ptn.matcher(out).find());
    }

    @Test
    public void testFunNow2() {
        String meepo = "/* HI-MEEPO */\n/* Rna:Use /now/fun:now yyyy-MM-dd/*/now";
        Gene gene = Parser.parse(meepo);
        Map<String, Object> ctx = new HashMap<>();
        String out = gene.merge(ctx);
        System.out.println(out);
        Pattern ptn = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        assertTrue(ptn.matcher(out).find());
    }

    @Test
    public void testFunNow() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        JavaEval fun = RnaManager.getFunction("fun:now");
        Map<String, Object> ctx = new HashMap<>();
        {
            Object obj = fun.eval(ctx, null);
            System.out.println(obj);
        }
        {
            Object obj = fun.eval(ctx, new java.util.Date(0));
            assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Date(0));
            assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Timestamp(0));
            assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, LocalDateTime.of(1970, 1, 1, 0, 0, 0));
            assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.util.Date(0), "yyyy-MM-dd");
            assertEquals("1970-01-01", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Date(0), "yyyy-MM-dd");
            assertEquals("1970-01-01", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Timestamp(0), "yyyy-MM-dd");
            assertEquals("1970-01-01", obj);
        }
        {
            Object obj = fun.eval(ctx, LocalDate.of(1970, 1, 1), "yyyy-MM-dd");
            assertEquals("1970-01-01", obj);
        }
    }

    @Test
    public void testFunAbs() {
        String meepo = "/* hi-meepo */\n" +
                       "/* RNA:put fun/fun:abs/return Math.abs(((Number)obj).intValue())/ */\n" +
                       "/* rna:use /now/number|fun:abs/*/now";
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("number", -1);
        Gene gene = Parser.parse(meepo);
        String out = gene.merge(ctx);
        System.out.println(out);
        assertEquals("1", out);
    }

    @Test
    public void testFunCtx() {
        String meepo = "/* hi-meepo */\n" +
                       "/* rna:use /now/number|abs/*/now";
        Map<String, Object> ctx = new HashMap<>();
        Function<Number, Integer> abs = number -> Math.abs(number.intValue());
        ctx.put("fun:abs", abs);
        ctx.put("number", -1);

        Gene gene = Parser.parse(meepo);
        String out = gene.merge(ctx);
        System.out.println(out);
        assertEquals("1", out);
    }
}
