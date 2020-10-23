package pro.fessional.meepo.sack;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;
import pro.fessional.meepo.bind.dna.DnaEnd;
import pro.fessional.meepo.bind.dna.DnaSet;
import pro.fessional.meepo.bind.rna.RnaPut;
import pro.fessional.meepo.bind.rna.RnaRun;
import pro.fessional.meepo.bind.rna.RnaUse;
import pro.fessional.meepo.bind.txt.HiMeepo;
import pro.fessional.meepo.bind.txt.TxtPlain;
import pro.fessional.meepo.poof.RnaManager;
import pro.fessional.meepo.poof.impl.JsEngine;
import pro.fessional.meepo.poof.impl.MapEngine;
import pro.fessional.meepo.poof.impl.OsEngine;
import pro.fessional.meepo.poof.impl.RawEngine;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static pro.fessional.meepo.bind.Const.TKN_DNA$;
import static pro.fessional.meepo.bind.Const.TKN_RNA$;

/**
 * @author trydofor
 * @since 2020-10-18
 */
public class ParserTest {

    static {
        RnaManager.register(new JsEngine());
        RnaManager.register(new MapEngine());
        RnaManager.register(new OsEngine());
        RnaManager.register(new RawEngine());
    }

    private static class TestCtx extends Parser.Ctx {
        public TestCtx(String txt) {
            super(txt);
            edge0 = 0;
            edge1 = txt.length();
        }

        public TestCtx(String txt, HiMeepo mp) {
            super(txt);
            edge0 = 0;
            edge1 = txt.length();
            meepo = mp;
        }
    }

    private final HiMeepo level5 = new HiMeepo("/*H!MEEPO*/", new Clop(0, 11), new Clop(2, 9), "/*", "*/");
    private final HiMeepo single = new HiMeepo("//H!MEEPO", new Clop(0, 9), new Clop(2, 9), "//", "\n");

    @Test
    public void findXnaGrp() {
//        {
//            String str = " //  DNA:MEEPO  ";
//            int s3 = str.length();
//            int[] p1 = {str.indexOf("//"), str.indexOf("DNA:")+4, s3};
//            int[] t1 = {0, -1, s3};
//            Parser.findXnaGrp(str, t1, "//", "DNA:", null);
//            Assert.assertArrayEquals(p1, t1);
//        }
//
//        {
//            String str = "//DNA:";
//            int s3 = str.length();
//            int[] p1 = {str.indexOf("//"), str.indexOf("DNA:")+4, s3};
//            int[] t1 = {0, -1, s3};
//            Parser.findXnaGrp(str, t1, "//", "DNA:", null);
//            Assert.assertArrayEquals(p1, t1);
//        }
//
//        {
//            String str = " // w DNA:MEEPO";
//            int s3 = str.length();
//            int[] t1 = {0, -1, s3};
//            int[] p1 = {str.indexOf("//"), -1, s3};
//            Seek.findXnaGrp(str, t1, "//", "DNA:", null);
//            Assert.assertArrayEquals(p1, t1);
//        }
    }

    private void checkHiMeepo(String txt, String pre, String suf, String edge, String main) {
        TestCtx ctx = new TestCtx(txt);
        Parser.markHiMeepo(ctx);
        HiMeepo meepo = ctx.meepo;
        List<Exon> gene = ctx.gene;
        Exon exon = gene.get(gene.size() - 1);

        StringBuilder buf = new StringBuilder();
        if (pre == null) {
            assertNull(meepo);
            Assert.assertTrue(exon instanceof TxtPlain);
            Assert.assertEquals(txt, exon.text);
        } else {
            Assert.assertNotNull(meepo);
            Assert.assertEquals(pre, meepo.head);
            Assert.assertEquals(suf, meepo.tail);
            Assert.assertEquals(edge, txt.substring(meepo.edge.start, meepo.edge.until));
            Assert.assertEquals(main, txt.substring(meepo.main.start, meepo.main.until));

            exon.merge(new HashMap<>(), buf);
            assertEquals("", buf.toString());
        }
        buf.setLength(0);
        exon.build(buf);
        assertEquals(edge, buf.toString());
    }

    @Test
    public void dealHiMeepo() {
        checkHiMeepo("//H!MEEPO", "//", "\n", "//H!MEEPO", "H!MEEPO");
        checkHiMeepo(" //H!MEEPO", "//", "\n", "//H!MEEPO", "H!MEEPO");
        checkHiMeepo(" // H!MEEPO", "//", "\n", "// H!MEEPO", "H!MEEPO");
        checkHiMeepo(" // H!MEEPO ", "//", "\n", "// H!MEEPO ", "H!MEEPO");
        checkHiMeepo(" // H!MEEPO \n", "//", "\n", "// H!MEEPO \n", "H!MEEPO");
        checkHiMeepo(" // H!MEEPO \nhaha", "//", "\n", "// H!MEEPO \n", "H!MEEPO");

        checkHiMeepo("/*H!MEEPO*/", "/*", "*/", "/*H!MEEPO*/", "H!MEEPO");
        checkHiMeepo("/* H!MEEPO*/", "/*", "*/", "/* H!MEEPO*/", "H!MEEPO");
        checkHiMeepo("/* H!MEEPO */", "/*", "*/", "/* H!MEEPO */", "H!MEEPO");
        checkHiMeepo(" /* H!MEEPO */", "/*", "*/", "/* H!MEEPO */", "H!MEEPO");
        checkHiMeepo(" /* H!MEEPO */ ", "/*", "*/", "/* H!MEEPO */", "H!MEEPO");
        checkHiMeepo(" /* H!MEEPO */ \n", "/*", "*/", "/* H!MEEPO */", "H!MEEPO");
        checkHiMeepo(" /* H!MEEPO */ \nhaha", "/*", "*/", "/* H!MEEPO */", "H!MEEPO");

        checkHiMeepo(" H!MEEPO */ \nhaha", null, null, " H!MEEPO */ \nhaha", null);
        checkHiMeepo(" H!MEEPO ", null, null, " H!MEEPO ", null);
        checkHiMeepo("H!MEEPO", null, null, "H!MEEPO", null);
        checkHiMeepo("\nH!MEEPO\n", null, null, "\nH!MEEPO\n", null);
    }

    private void checkDnaRaw(HiMeepo meepo, String txt, String merge, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        ctx.done1 = 0;
        ctx.edge0 = ctx.txt.indexOf(ctx.meepo.head);
        Parser.findXnaGrp(ctx, TKN_DNA$);

        Parser.dealDnaRaw(ctx);
        List<Exon> gene = ctx.gene;

        StringBuilder buf = new StringBuilder();
        for (Exon exon : gene) {
            exon.merge(new HashMap<>(), buf);
        }
        assertEquals(merge, buf.toString());
        buf.setLength(0);
        for (Exon exon : gene) {
            exon.build(buf);
        }
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealDnaRaw() {
        checkDnaRaw(single, "// DNA:RAW SUPER @@", " SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "@@// DNA:RAW SUPER @@", " SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "@@// DNA:RAW SUPER @@", " SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "@@// DNA:RAW SUPER @@\n", " SUPER @@", "// DNA:RAW SUPER @@\n");

        checkDnaRaw(single, "// DNA:RAW\n", "", "// DNA:RAW\n");

        checkDnaRaw(level5, "/* DNA:RAW SUPER */", " SUPER ", "/* DNA:RAW SUPER */");
        checkDnaRaw(level5, "@@/* DNA:RAW SUPER */", " SUPER ", "/* DNA:RAW SUPER */");
        checkDnaRaw(level5, "@@/* DNA:RAW SUPER */@@", " SUPER ", "/* DNA:RAW SUPER */");
        checkDnaRaw(level5, "@@/* DNA:RAW SUPER */\n@@", " SUPER ", "/* DNA:RAW SUPER */");

        checkDnaRaw(level5, "@@/* DNA:RAW*/\n@@", "", "/* DNA:RAW*/");
    }

    private void checkDnaBkb(HiMeepo meepo, String txt, String name, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        ctx.done1 = 0;
        ctx.edge0 = txt.indexOf("/");
        Parser.findXnaGrp(ctx, TKN_DNA$);

        Parser.dealDnaBkb(ctx);
        List<Exon> gene = ctx.gene;
        Exon exon = gene.get(gene.size() - 1);
        StringBuilder buf = new StringBuilder();

        System.out.println(exon.toString());
        if (name == null) {
            Assert.assertTrue(exon instanceof TxtPlain);
        } else {
            assertEquals(name, exon.life.name);
            exon.merge(new HashMap<>(), buf);
            assertEquals("", buf.toString());
        }
        buf.setLength(0);
        exon.build(buf);
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealDnaBkb() {
        checkDnaBkb(single, "// DNA:BKB 黑皇杖", "黑皇杖", "// DNA:BKB 黑皇杖");
        checkDnaBkb(single, "@@// DNA:BKB 黑皇杖 @@", "黑皇杖", "// DNA:BKB 黑皇杖 @@");
        checkDnaBkb(single, "@@// DNA:BKB 黑皇杖 @@\n", "黑皇杖", "// DNA:BKB 黑皇杖 @@\n");

        checkDnaBkb(single, "@@// DNA:BKB \n", null, "// DNA:BKB \n");
        checkDnaBkb(single, "@@// DNA:BKB\n", null, "// DNA:BKB\n");

        checkDnaBkb(level5, "/* DNA:BKB 黑皇杖 */", "黑皇杖", "/* DNA:BKB 黑皇杖 */");
        checkDnaBkb(level5, "@@/* DNA:BKB 黑皇杖 */", "黑皇杖", "/* DNA:BKB 黑皇杖 */");
        checkDnaBkb(level5, "@@/* DNA:BKB 黑皇杖 */\n", "黑皇杖", "/* DNA:BKB 黑皇杖 */");
        checkDnaBkb(level5, "@@/* DNA:BKB 黑皇杖 */@@", "黑皇杖", "/* DNA:BKB 黑皇杖 */");
        checkDnaBkb(level5, "@@/* DNA:BKB 黑皇杖 */\n@@", "黑皇杖", "/* DNA:BKB 黑皇杖 */");

        checkDnaBkb(level5, "@@/* DNA:BKB */\n@@", null, "/* DNA:BKB */");
        checkDnaBkb(level5, "@@/* DNA:BKB*/\n@@", null, "/* DNA:BKB*/");
    }

    private void checkDnaEnd(HiMeepo meepo, String txt, String name, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        ctx.done1 = 0;
        ctx.edge0 = txt.indexOf("/");
        Parser.findXnaGrp(ctx, TKN_DNA$);

        Parser.dealDnaEnd(ctx);
        List<Exon> gene = ctx.gene;
        Exon exon = gene.get(gene.size() - 1);
        System.out.println(exon.toString());

        StringBuilder buf = new StringBuilder();
        if (name == null) {
            Assert.assertTrue(exon instanceof TxtPlain);
        } else {
            DnaEnd dna = (DnaEnd) exon;
            String[] nms = name.split("[, ]+");
            assertEquals(nms.length, dna.name.size());
            for (String nm : nms) {
                assertTrue(dna.name.contains(nm));
            }
            dna.merge(new HashMap<>(), buf);
            assertEquals("", buf.toString());
        }
        buf.setLength(0);
        exon.build(buf);
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealDnaEnd() {
        checkDnaEnd(single, "// DNA:END 黑皇杖 id", "黑皇杖,id", "// DNA:END 黑皇杖 id");
        checkDnaEnd(single, "@@// DNA:END 黑皇杖 id @@", "黑皇杖,id,@@", "// DNA:END 黑皇杖 id @@");
        checkDnaEnd(single, "@@// DNA:END 黑皇杖 id @@\n", "黑皇杖,id,@@", "// DNA:END 黑皇杖 id @@\n");

        checkDnaEnd(single, "@@// DNA:END \n", null, "// DNA:END \n");
        checkDnaEnd(single, "@@// DNA:END\n", null, "// DNA:END\n");

        checkDnaEnd(level5, "/* DNA:END 黑皇杖 id */", "黑皇杖,id", "/* DNA:END 黑皇杖 id */");
        checkDnaEnd(level5, "@@/* DNA:END 黑皇杖 id */", "黑皇杖,id", "/* DNA:END 黑皇杖 id */");
        checkDnaEnd(level5, "@@/* DNA:END 黑皇杖 id */\n", "黑皇杖,id", "/* DNA:END 黑皇杖 id */");
        checkDnaEnd(level5, "@@/* DNA:END 黑皇杖 id */@@", "黑皇杖,id", "/* DNA:END 黑皇杖 id */");
        checkDnaEnd(level5, "@@/* DNA:END 黑皇杖 id */\n@@", "黑皇杖,id", "/* DNA:END 黑皇杖 id */");

        checkDnaEnd(single, "@@/* DNA:END \n", null, "/* DNA:END \n");
        checkDnaEnd(single, "@@/* DNA:END\n", null, "/* DNA:END\n");
    }


    private void checkDnaSet(HiMeepo meepo, String txt, Life life, String find, String repl, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        ctx.done1 = 0;
        ctx.edge0 = txt.indexOf("/");
        Parser.findXnaGrp(ctx, TKN_DNA$);

        Parser.dealDnaSet(ctx);
        List<Exon> gene = ctx.gene;
        Exon exon = gene.get(gene.size() - 1);
        System.out.println(exon.toString());

        StringBuilder buf = new StringBuilder();
        if (life == null) {
            Assert.assertTrue(exon instanceof TxtPlain);
        } else {
            DnaSet dna = (DnaSet) exon;
            Assert.assertEquals(find, dna.find.pattern());
            Assert.assertEquals(repl, dna.repl);
            Assert.assertEquals(life, dna.life);

            dna.merge(new HashMap<>(), buf);
            assertEquals("", buf.toString());
        }
        buf.setLength(0);
        exon.build(buf);
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealDnaSet() {
        Life one = Life.nobodyOne();
        Life num = Life.parse("1,3-5,9 ");
        Life name = Life.namedAny("mail");
        checkDnaSet(single, "// DNA:SET ///", one, "", "", "// DNA:SET ///");
        checkDnaSet(single, "// DNA:SET //{{user.male}}/", one, "", "{{user.male}}", "// DNA:SET //{{user.male}}/");
        checkDnaSet(single, "// DNA:SET /false//", one, "false", "", "// DNA:SET /false//");
        checkDnaSet(single, "// DNA:SET /false/{{user.male}}/", one, "false", "{{user.male}}", "// DNA:SET /false/{{user.male}}/");
        checkDnaSet(single, "@@// DNA:SET /false/{{user.male}}/ @@", one, "false", "{{user.male}}", "// DNA:SET /false/{{user.male}}/ @@");
        checkDnaSet(single, "@@// DNA:SET /false/{{user.male}}/ @@\n", one, "false", "{{user.male}}", "// DNA:SET /false/{{user.male}}/ @@\n");
        checkDnaSet(single, "@@// DNA:SET /false/{{user.male}}/1,3-5,9 @@\n", num, "false", "{{user.male}}", "// DNA:SET /false/{{user.male}}/1,3-5,9 @@\n");
        checkDnaSet(single, "@@// DNA:SET /false/{{user.male}}/mail @@\n", name, "false", "{{user.male}}", "// DNA:SET /false/{{user.male}}/mail @@\n");

        checkDnaSet(single, "@@// DNA:SET false/{{user.male}}/mail @@\n", null, null, null, "// DNA:SET false/{{user.male}}/mail @@\n");
        checkDnaSet(single, "@@// DNA:SET /false/mail @@\n", null, null, null, "// DNA:SET /false/mail @@\n");

        checkDnaSet(level5, "/* DNA:SET /false/{{user.male}}/ */", one, "false", "{{user.male}}", "/* DNA:SET /false/{{user.male}}/ */");
        checkDnaSet(level5, "@@/* DNA:SET /false/{{user.male}}/ */", one, "false", "{{user.male}}", "/* DNA:SET /false/{{user.male}}/ */");
        checkDnaSet(level5, "@@/* DNA:SET /false/{{user.male}}/ */\n", one, "false", "{{user.male}}", "/* DNA:SET /false/{{user.male}}/ */");
        checkDnaSet(level5, "@@/* DNA:SET /false/{{user.male}}/ */@@", one, "false", "{{user.male}}", "/* DNA:SET /false/{{user.male}}/ */");
        checkDnaSet(level5, "@@/* DNA:SET /false/{{user.male}}/ */\n@@", one, "false", "{{user.male}}", "/* DNA:SET /false/{{user.male}}/ */");
        checkDnaSet(level5, "@@/* DNA:SET /false/{{user.male}}/1,3-5,9 */\n@@", num, "false", "{{user.male}}", "/* DNA:SET /false/{{user.male}}/1,3-5,9 */");
        checkDnaSet(level5, "@@/* DNA:SET /false/{{user.male}}/mail */\n@@", name, "false", "{{user.male}}", "/* DNA:SET /false/{{user.male}}/mail */");

        checkDnaSet(level5, "@@/* DNA:SET false/{{user.male}}/mail */\n@@", null, null, null, "/* DNA:SET false/{{user.male}}/mail */");
        checkDnaSet(level5, "@@/* DNA:SET /false/mail */\n@@", null, null, null, "/* DNA:SET /false/mail */");
    }

    private void checkRnaRun(HiMeepo meepo, String txt, Life life, String type, String find, String expr, boolean quiet, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        ctx.done1 = 0;
        ctx.edge0 = txt.indexOf("/");
        Parser.findXnaGrp(ctx, TKN_RNA$);

        Parser.dealRnaRun(ctx);
        List<Exon> gene = ctx.gene;
        Exon exon = gene.get(gene.size() - 1);
        System.out.println(exon.toString());

        StringBuilder buf = new StringBuilder();
        if (type == null) {
            Assert.assertTrue(exon instanceof TxtPlain);
        } else {
            RnaRun rna = (RnaRun) exon;
            Assert.assertEquals(type, rna.type);
            Assert.assertEquals(find, rna.find.pattern());
            Assert.assertEquals(expr, rna.expr);
            Assert.assertEquals(life, rna.life);
            Assert.assertEquals(quiet, rna.mute);
            rna.merge(new HashMap<>(), buf);
            assertEquals("", buf.toString());
        }
        buf.setLength(0);
        exon.build(buf);
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealRnaRun() {
        Life one = Life.nobodyOne();
        Life num = Life.parse("1,3-5,9 ");
        Life name = Life.namedAny("mail");
        checkRnaRun(single, "// RNA:RUN /who/meepo/", one, "map", "who", "meepo", false, "// RNA:RUN /who/meepo/");
        checkRnaRun(single, "// RNA:RUN !/who/meepo/", one, "map", "who", "meepo", true, "// RNA:RUN !/who/meepo/");
        checkRnaRun(single, "// RNA:RUN js/how/2*(1+3)/", one, "js", "how", "2*(1+3)", false, "// RNA:RUN js/how/2*(1+3)/");
        checkRnaRun(single, "// RNA:RUN js!/how/2*(1+3)/", one, "js", "how", "2*(1+3)", true, "// RNA:RUN js!/how/2*(1+3)/");
        checkRnaRun(single, "// RNA:RUN ///", one, "map", "", "", false, "// RNA:RUN ///");
        checkRnaRun(single, "// RNA:RUN //meepo/", one, "map", "", "meepo", false, "// RNA:RUN //meepo/");
        checkRnaRun(single, "// RNA:RUN /who//", one, "map", "who", "", false, "// RNA:RUN /who//");
        checkRnaRun(single, "// RNA:RUN /who/meepo/ @@", one, "map", "who", "meepo", false, "// RNA:RUN /who/meepo/ @@");
        checkRnaRun(single, "// RNA:RUN raw/who/meepo/1,3-5,9 @@", num, "raw", "who", "meepo", false, "// RNA:RUN raw/who/meepo/1,3-5,9 @@");
        checkRnaRun(single, "// RNA:RUN /who/meepo/ @@\n", one, "map", "who", "meepo", false, "// RNA:RUN /who/meepo/ @@\n");
        checkRnaRun(single, "// RNA:RUN raw/who/meepo/mail @@\n", name, "raw", "who", "meepo", false, "// RNA:RUN raw/who/meepo/mail @@\n");

        checkRnaRun(single, "// RNA:RUN who/meepo/ @@\n", null, null, null, null, false, "// RNA:RUN who/meepo/ @@\n");
        checkRnaRun(single, "// RNA:RUN /who/meepo @@\n", null, null, null, null, false, "// RNA:RUN /who/meepo @@\n");

        checkRnaRun(level5, "/* RNA:RUN /who/meepo/*/", one, "map", "who", "meepo", false, "/* RNA:RUN /who/meepo/*/");
        checkRnaRun(level5, "/* RNA:RUN js/how/2*(1+3)/*/", one, "js", "how", "2*(1+3)", false, "/* RNA:RUN js/how/2*(1+3)/*/");
        checkRnaRun(level5, "/* RNA:RUN js!/how/2*(1+3)/*/", one, "js", "how", "2*(1+3)", true, "/* RNA:RUN js!/how/2*(1+3)/*/");
        checkRnaRun(level5, "/* RNA:RUN ///*/", one, "map", "", "", false, "/* RNA:RUN ///*/");
        checkRnaRun(level5, "/* RNA:RUN //meepo/*/", one, "map", "", "meepo", false, "/* RNA:RUN //meepo/*/");
        checkRnaRun(level5, "/* RNA:RUN /who//*/", one, "map", "who", "", false, "/* RNA:RUN /who//*/");
        checkRnaRun(level5, "/* RNA:RUN /who/meepo/*/@@", one, "map", "who", "meepo", false, "/* RNA:RUN /who/meepo/*/");
        checkRnaRun(level5, "/* RNA:RUN raw/who/meepo/*/@@", one, "raw", "who", "meepo", false, "/* RNA:RUN raw/who/meepo/*/");
        checkRnaRun(level5, "/* RNA:RUN /who/meepo/1,3-5,9*/@@\n", num, "map", "who", "meepo", false, "/* RNA:RUN /who/meepo/1,3-5,9*/");
        checkRnaRun(level5, "/* RNA:RUN raw/who/meepo/mail*/@@\n", name, "raw", "who", "meepo", false, "/* RNA:RUN raw/who/meepo/mail*/");

        checkRnaRun(level5, "/* RNA:RUN who/meepo/*/", null, null, null, null, false, "/* RNA:RUN who/meepo/*/");
        checkRnaRun(level5, "/* RNA:RUN /who/meepo*/", null, null, null, null, false, "/* RNA:RUN /who/meepo*/");
    }

    private void checkRnaUse(HiMeepo meepo, String txt, Life life, String find, String para, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        ctx.done1 = 0;
        ctx.edge0 = txt.indexOf("/");
        Parser.findXnaGrp(ctx, TKN_RNA$);

        Parser.dealRnaUse(ctx);
        List<Exon> gene = ctx.gene;
        Exon exon = gene.get(gene.size() - 1);
        System.out.println(exon.toString());

        StringBuilder buf = new StringBuilder();
        if (life == null) {
            Assert.assertTrue(exon instanceof TxtPlain);
        } else {
            RnaUse rna = (RnaUse) exon;
            Assert.assertEquals(find, rna.find.pattern());
            Assert.assertEquals(para, rna.para);
            Assert.assertEquals(life, rna.life);

            HashMap<String, Object> ctx1 = new HashMap<>();
            ctx1.put("who", "meepo");
            rna.merge(ctx1, buf);
            assertEquals("", buf.toString());
        }
        buf.setLength(0);
        exon.build(buf);
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealRnaUse() {
        Life one = Life.nobodyOne();
        Life num = Life.parse("1,3-5,9 ");
        Life name = Life.namedAny("mail");
        checkRnaUse(single, "// RNA:USE ///", one, "", "", "// RNA:USE ///");
        checkRnaUse(single, "// RNA:USE //who/", one, "", "who", "// RNA:USE //who/");
        checkRnaUse(single, "// RNA:USE /meepo//", one, "meepo", "", "// RNA:USE /meepo//");
        checkRnaUse(single, "// RNA:USE /meepo/who/", one, "meepo", "who", "// RNA:USE /meepo/who/");
        checkRnaUse(single, "@@// RNA:USE /meepo/who/ @@", one, "meepo", "who", "// RNA:USE /meepo/who/ @@");
        checkRnaUse(single, "@@// RNA:USE /meepo/who/ @@\n", one, "meepo", "who", "// RNA:USE /meepo/who/ @@\n");
        checkRnaUse(single, "@@// RNA:USE /meepo/who/1,3-5,9 @@\n", num, "meepo", "who", "// RNA:USE /meepo/who/1,3-5,9 @@\n");
        checkRnaUse(single, "@@// RNA:USE /meepo/who/mail @@\n", name, "meepo", "who", "// RNA:USE /meepo/who/mail @@\n");

        checkRnaUse(single, "@@// RNA:USE meepo/who/mail @@\n", null, null, null, "// RNA:USE meepo/who/mail @@\n");
        checkRnaUse(single, "@@// RNA:USE /meepo/mail @@\n", null, null, null, "// RNA:USE /meepo/mail @@\n");

        checkRnaUse(level5, "/* RNA:USE /meepo/who/ */", one, "meepo", "who", "/* RNA:USE /meepo/who/ */");
        checkRnaUse(level5, "@@/* RNA:USE /meepo/who/ */", one, "meepo", "who", "/* RNA:USE /meepo/who/ */");
        checkRnaUse(level5, "@@/* RNA:USE /meepo/who/ */\n", one, "meepo", "who", "/* RNA:USE /meepo/who/ */");
        checkRnaUse(level5, "@@/* RNA:USE /meepo/who/ */@@", one, "meepo", "who", "/* RNA:USE /meepo/who/ */");
        checkRnaUse(level5, "@@/* RNA:USE /meepo/who/ */\n@@", one, "meepo", "who", "/* RNA:USE /meepo/who/ */");
        checkRnaUse(level5, "@@/* RNA:USE /meepo/who/1,3-5,9 */\n@@", num, "meepo", "who", "/* RNA:USE /meepo/who/1,3-5,9 */");
        checkRnaUse(level5, "@@/* RNA:USE /meepo/who/mail */\n@@", name, "meepo", "who", "/* RNA:USE /meepo/who/mail */");

        checkRnaUse(level5, "@@/* RNA:USE meepo/who/mail */\n@@", null, null, null, "/* RNA:USE meepo/who/mail */");
        checkRnaUse(level5, "@@/* RNA:USE /meepo/mail */\n@@", null, null, null, "/* RNA:USE /meepo/mail */");
    }

    private void checkRnaPut(HiMeepo meepo, String txt, String type, String para, String expr, boolean mute, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        ctx.done1 = 0;
        ctx.edge0 = txt.indexOf("/");
        Parser.findXnaGrp(ctx, TKN_RNA$);

        Parser.dealRnaPut(ctx);
        List<Exon> gene = ctx.gene;
        Exon exon = gene.get(gene.size() - 1);
        System.out.println(exon.toString());

        StringBuilder buf = new StringBuilder();
        if (type == null) {
            Assert.assertTrue(exon instanceof TxtPlain);
        } else {
            RnaPut rna = (RnaPut) exon;
            Assert.assertEquals(type, rna.type);
            Assert.assertEquals(para, rna.para);
            Assert.assertEquals(expr, rna.expr);
            Assert.assertEquals(mute, rna.mute);
            rna.merge(new HashMap<>(), buf);
            assertEquals("", buf.toString());
        }
        buf.setLength(0);
        exon.build(buf);
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealRnaPut() {
        checkRnaPut(single, "// RNA:PUT /who/meepo/", "map", "who", "meepo", false,"// RNA:PUT /who/meepo/");
        checkRnaPut(single, "// RNA:PUT !/who/meepo/", "map", "who", "meepo", true,"// RNA:PUT !/who/meepo/");
        checkRnaPut(single, "// RNA:PUT js/how/2*(1+3)/", "js", "how", "2*(1+3)", false,"// RNA:PUT js/how/2*(1+3)/");
        checkRnaPut(single, "// RNA:PUT js!/how/2*(1+3)/", "js", "how", "2*(1+3)", true,"// RNA:PUT js!/how/2*(1+3)/");
        checkRnaPut(single, "// RNA:PUT ///", "map", "", "", false,"// RNA:PUT ///");
        checkRnaPut(single, "// RNA:PUT //meepo/", "map", "", "meepo", false,"// RNA:PUT //meepo/");
        checkRnaPut(single, "// RNA:PUT /who//", "map", "who", "", false,"// RNA:PUT /who//");
        checkRnaPut(single, "// RNA:PUT /who/meepo/ @@", "map", "who", "meepo", false,"// RNA:PUT /who/meepo/ @@");
        checkRnaPut(single, "// RNA:PUT raw/who/meepo/ @@", "raw", "who", "meepo", false,"// RNA:PUT raw/who/meepo/ @@");
        checkRnaPut(single, "// RNA:PUT /who/meepo/ @@\n", "map", "who", "meepo", false,"// RNA:PUT /who/meepo/ @@\n");
        checkRnaPut(single, "// RNA:PUT raw/who/meepo/ @@\n", "raw", "who", "meepo", false,"// RNA:PUT raw/who/meepo/ @@\n");

        checkRnaPut(single, "// RNA:PUT who/meepo/ @@\n", null, null, null, false,"// RNA:PUT who/meepo/ @@\n");
        checkRnaPut(single, "// RNA:PUT /who/meepo @@\n", null, null, null, false,"// RNA:PUT /who/meepo @@\n");

        checkRnaPut(level5, "/* RNA:PUT /who/meepo/*/", "map", "who", "meepo", false,"/* RNA:PUT /who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT js/how/2*(1+3)/*/", "js", "how", "2*(1+3)", false,"/* RNA:PUT js/how/2*(1+3)/*/");
        checkRnaPut(level5, "/* RNA:PUT js!/how/2*(1+3)/*/", "js", "how", "2*(1+3)", true,"/* RNA:PUT js!/how/2*(1+3)/*/");
        checkRnaPut(level5, "/* RNA:PUT ///*/", "map", "", "", false,"/* RNA:PUT ///*/");
        checkRnaPut(level5, "/* RNA:PUT //meepo/*/", "map", "", "meepo", false,"/* RNA:PUT //meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT /who//*/", "map", "who", "", false,"/* RNA:PUT /who//*/");
        checkRnaPut(level5, "/* RNA:PUT /who/meepo/*/@@", "map", "who", "meepo", false,"/* RNA:PUT /who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT raw/who/meepo/*/@@", "raw", "who", "meepo", false,"/* RNA:PUT raw/who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT /who/meepo/*/@@\n", "map", "who", "meepo", false,"/* RNA:PUT /who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT raw/who/meepo/*/@@\n", "raw", "who", "meepo", false,"/* RNA:PUT raw/who/meepo/*/");

        checkRnaPut(level5, "/* RNA:PUT who/meepo/*/", null, null, null, false,"/* RNA:PUT who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT /who/meepo*/", null, null, null, false,"/* RNA:PUT /who/meepo*/");
    }
}