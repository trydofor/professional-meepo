package pro.fessional.meepo.poof;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.poof.impl.java.JavaEval;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
        Assert.assertTrue(ptn.matcher(out).find());
    }

    @Test
    public void testFunNow2() {
        String meepo = "/* HI-MEEPO */\n/* Rna:Use /now/fun:now yyyy-MM-dd/*/now";
        Gene gene = Parser.parse(meepo);
        Map<String, Object> ctx = new HashMap<>();
        String out = gene.merge(ctx);
        System.out.println(out);
        Pattern ptn = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Assert.assertTrue(ptn.matcher(out).find());
    }

    @Test
    public void testFunNow() {
        JavaEval fun = RnaManager.getFunction("fun:now");
        Map<String, Object> ctx = new HashMap<>();
        {
            Object obj = fun.eval(ctx, null);
            System.out.println(obj);
        }
        {
            Object obj = fun.eval(ctx, new java.util.Date(0));
            Assert.assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Date(0));
            Assert.assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Timestamp(0));
            Assert.assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, LocalDateTime.of(1970, 1, 1, 0, 0, 0));
            Assert.assertEquals("1970-01-01 00:00:00", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.util.Date(0), "yyyy-MM-dd");
            Assert.assertEquals("1970-01-01", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Date(0), "yyyy-MM-dd");
            Assert.assertEquals("1970-01-01", obj);
        }
        {
            Object obj = fun.eval(ctx, new java.sql.Timestamp(0), "yyyy-MM-dd");
            Assert.assertEquals("1970-01-01", obj);
        }
        {
            Object obj = fun.eval(ctx, LocalDate.of(1970, 1, 1), "yyyy-MM-dd");
            Assert.assertEquals("1970-01-01", obj);
        }
    }

    @Test
    public void testFunAbs() {
        String meepo = "/* hi-meepo */\n" +
                "/* RNA:put fun/fun:abs/return Math.abs(((Number)obj).intValue())/ */\n" +
                "/* rna:use /now/number|fun:abs/*/now";
        Gene gene = Parser.parse(meepo);
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("number", -1);
        String out = gene.merge(ctx);
        System.out.println(out);
        Assert.assertEquals("1", out);
    }
}