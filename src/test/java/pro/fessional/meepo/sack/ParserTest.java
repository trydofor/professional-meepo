package pro.fessional.meepo.sack;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.dna.DnaEnd;
import pro.fessional.meepo.bind.dna.DnaSet;
import pro.fessional.meepo.bind.rna.RnaDone;
import pro.fessional.meepo.bind.rna.RnaEach;
import pro.fessional.meepo.bind.rna.RnaElse;
import pro.fessional.meepo.bind.rna.RnaPut;
import pro.fessional.meepo.bind.rna.RnaRun;
import pro.fessional.meepo.bind.rna.RnaUse;
import pro.fessional.meepo.bind.rna.RnaWhen;
import pro.fessional.meepo.bind.txt.HiMeepo;
import pro.fessional.meepo.bind.txt.TxtSimple;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Tick;

import java.io.CharArrayWriter;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static pro.fessional.meepo.bind.Const.TKN$DNA_;
import static pro.fessional.meepo.bind.Const.TKN$RNA_;

/**
 * @author trydofor
 * @since 2020-10-18
 */
public class ParserTest extends TraceTest {

    private final Logger logger = LoggerFactory.getLogger(ParserTest.class);

    private static class TestCtx extends Parser.Ctx {
        public TestCtx(String txt) {
            super(txt, true);
            edge0 = 0;
            edge1 = txt.length();
        }

        public TestCtx(String txt, HiMeepo mp) {
            super(txt, true);
            edge0 = 0;
            edge1 = txt.length();
            meepo = mp;
        }
    }

    private final HiMeepo level5 = new HiMeepo("/*HI-MEEPO*/", new Clop(0, 11), new Clop(2, 9), "/*", "*/", false);
    private final HiMeepo single = new HiMeepo("//HI-MEEPO", new Clop(0, 9), new Clop(2, 9), "//", "\n", false);

    private Acid newAcid(Parser.Ctx ctx) {
        return new Acid(new HashMap<>(), ctx.rngs.getCheckedEngine());
    }

    private void checkHiMeepo(String txt, String pre, String suf, String edge, String main) {
        TestCtx ctx = new TestCtx(txt);
        Parser.markHiMeepo(ctx);
        HiMeepo meepo = ctx.meepo;
        List<Exon> gene = ctx.getGene();
        Exon exon = gene.get(gene.size() - 1);

        CharArrayWriter buf = new CharArrayWriter();
        if (pre == null) {
            assertNull(meepo);
            Assert.assertTrue(exon instanceof TxtSimple);
            exon.build(buf);
            Assert.assertEquals(txt, buf.toString());
        } else {
            Assert.assertNotNull(meepo);
            Assert.assertEquals(pre, meepo.head);
            Assert.assertEquals(suf, meepo.tail);
            Assert.assertEquals(edge, txt.substring(meepo.edge.start, meepo.edge.until));
            exon.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(edge, buf.toString());
        }
    }

    @Test
    public void dealHiMeepo() {
        checkHiMeepo("//HI-MEEPO", "//", "\n", "//HI-MEEPO", "HI-MEEPO");
        checkHiMeepo(" //HI-MEEPO!", "//", "\n", "//HI-MEEPO!", "HI-MEEPO!");
        checkHiMeepo(" // HI-MEEPO", "//", "\n", " // HI-MEEPO", "HI-MEEPO");
        checkHiMeepo(" // HI-MEEPO ", "//", "\n", " // HI-MEEPO ", "HI-MEEPO");
        checkHiMeepo(" // HI-MEEPO \n", "//", "\n", " // HI-MEEPO \n", "HI-MEEPO");
        checkHiMeepo(" // HI-MEEPO \nhaha", "//", "\n", " // HI-MEEPO \n", "HI-MEEPO");

        checkHiMeepo("/*HI-MEEPO*/", "/*", "*/", "/*HI-MEEPO*/", "HI-MEEPO");
        checkHiMeepo("/* HI-MEEPO*/", "/*", "*/", "/* HI-MEEPO*/", "HI-MEEPO");
        checkHiMeepo("/* HI-MEEPO */", "/*", "*/", "/* HI-MEEPO */", "HI-MEEPO");
        checkHiMeepo(" /* HI-MEEPO */", "/*", "*/", " /* HI-MEEPO */", "HI-MEEPO");
        checkHiMeepo(" /* HI-MEEPO */ ", "/*", "*/", " /* HI-MEEPO */ ", "HI-MEEPO");
        checkHiMeepo(" /* HI-MEEPO! */ \n", "/*", "*/", "/* HI-MEEPO! */", "HI-MEEPO!");
        checkHiMeepo(" /* HI-MEEPO! */ \nhaha", "/*", "*/", "/* HI-MEEPO! */", "HI-MEEPO!");
        checkHiMeepo(" /* HI-MEEPO */ \n", "/*", "*/", " /* HI-MEEPO */ \n", "HI-MEEPO");
        checkHiMeepo(" /* HI-MEEPO */ \nhaha", "/*", "*/", " /* HI-MEEPO */ \n", "HI-MEEPO");

        checkHiMeepo(" HI-MEEPO */ \nhaha", null, null, " HI-MEEPO */ \nhaha", null);
        checkHiMeepo(" HI-MEEPO ", null, null, " HI-MEEPO ", null);
        checkHiMeepo("HI-MEEPO", null, null, "HI-MEEPO", null);
        checkHiMeepo("\nHI-MEEPO\n", null, null, "\nHI-MEEPO\n", null);
    }

    private void checkDnaRaw(HiMeepo meepo, String txt, String merge, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$DNA_);

        Exon exon = Parser.dealDnaRaw(ctx);
        Assert.assertNotNull(exon);

        CharArrayWriter buf = new CharArrayWriter();
        exon.merge(newAcid(ctx), buf);
        assertEquals(merge, buf.toString());
        buf.reset();
        exon.build(buf);
        assertEquals(build, buf.toString());
    }

    @Test
    public void dealDnaRaw() {
        checkDnaRaw(single, "// DNA:RAW SUPER @@", "SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "@@// DNA:RAW SUPER @@", "SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "@@// DNA:RAW SUPER @@", "SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "@@// DNA:RAW SUPER @@\n", "SUPER @@", "// DNA:RAW SUPER @@\n");

        checkDnaRaw(single, "// DNA:RAW\n", "", "// DNA:RAW\n");

        checkDnaRaw(level5, "/* DNA:RAW SUPER */", "SUPER", "/* DNA:RAW SUPER */");
        checkDnaRaw(level5, "@@/* DNA:RAW SUPER */", "SUPER", "/* DNA:RAW SUPER */");
        checkDnaRaw(level5, "@@/* DNA:RAW SUPER */@@", "SUPER", "/* DNA:RAW SUPER */");
        checkDnaRaw(level5, "@@/* DNA:RAW SUPER */\n@@", "SUPER", "/* DNA:RAW SUPER */");

        checkDnaRaw(level5, "@@/* DNA:RAW*/\n@@", "", "/* DNA:RAW*/");

        checkDnaRaw(single, "/// DNA:RAW SUPER @@", "SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "//// DNA:RAW SUPER @@", "SUPER @@", "// DNA:RAW SUPER @@");
        checkDnaRaw(single, "///// DNA:RAW SUPER @@", "SUPER @@", "// DNA:RAW SUPER @@");
    }

    private void checkDnaBkb(HiMeepo meepo, String txt, String name, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$DNA_);

        Exon exon = Parser.dealDnaBkb(ctx);
        Assert.assertNotNull(exon);
        System.out.println(exon);
        if (name == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            assertEquals(name, ((Tick) exon).life.name);
            exon.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
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
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$DNA_);

        Exon exon = Parser.dealDnaEnd(ctx);
        Assert.assertNotNull(exon);
        logger.debug(exon.toString());

        if (name == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            DnaEnd dna = (DnaEnd) exon;
            String[] nms = name.split("[, ]+");
            assertEquals(nms.length, dna.name.size());
            for (String nm : nms) {
                assertTrue(dna.name.contains(nm));
            }
            dna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
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

        checkDnaEnd(level5, "@@/* DNA:END */\n", null, "/* DNA:END */\n");
        checkDnaEnd(level5, "@@/* DNA:END*/\n", null, "/* DNA:END*/\n");
    }


    private void checkDnaSet(HiMeepo meepo, String txt, Life life, String find, String repl, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$DNA_);

        Exon exon = Parser.dealDnaSet(ctx);
        Assert.assertNotNull(exon);
        logger.debug(exon.toString());

        if (life == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            DnaSet dna = (DnaSet) exon;
            Assert.assertEquals(find, dna.find.pattern());
            Assert.assertEquals(repl, dna.repl);
            Assert.assertEquals(life, dna.life);

            dna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
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
        checkDnaSet(single, "@@// DNA:SET :false:{{user.male}}: @@", one, "false", "{{user.male}}", "// DNA:SET :false:{{user.male}}: @@");
        checkDnaSet(single, "@@// DNA:SET ⑨false⑨{{user.male}}⑨ @@\n", one, "false", "{{user.male}}", "// DNA:SET ⑨false⑨{{user.male}}⑨ @@\n");
        checkDnaSet(single, "@@// DNA:SET |false|{{user.male}}|1,3-5,9 @@\n", num, "false", "{{user.male}}", "// DNA:SET |false|{{user.male}}|1,3-5,9 @@\n");
        checkDnaSet(single, "@@// DNA:SET 汉false汉{{user.male}}汉mail @@\n", name, "false", "{{user.male}}", "// DNA:SET 汉false汉{{user.male}}汉mail @@\n");

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
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$RNA_);

        Exon exon = Parser.dealRnaRun(ctx);
        Assert.assertNotNull(exon);
        logger.debug(exon.toString());

        if (type == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            RnaRun rna = (RnaRun) exon;
            Assert.assertEquals(type, rna.type);
            Assert.assertEquals(find, rna.find.pattern());
            Assert.assertEquals(expr, rna.expr);
            Assert.assertEquals(life, rna.life);
            Assert.assertEquals(quiet, rna.mute);
            rna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
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
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$RNA_);

        Exon exon = Parser.dealRnaUse(ctx);
        Assert.assertNotNull(exon);
        logger.debug(exon.toString());

        if (life == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            RnaUse rna = (RnaUse) exon;
            Assert.assertEquals(find, rna.find.pattern());
            Assert.assertEquals(para, rna.para);
            Assert.assertEquals(life, rna.life);

            Acid ctx1 = newAcid(ctx);
            ctx1.context.put("who", "meepo");
            rna.merge(ctx1, buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
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
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$RNA_);

        Exon exon = Parser.dealRnaPut(ctx);
        Assert.assertNotNull(exon);
        exon.check(ctx.errs, ctx.rngs);
        logger.debug(exon.toString());

        if (type == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            RnaPut rna = (RnaPut) exon;
            Assert.assertEquals(type, rna.type);
            Assert.assertEquals(para, rna.para);
            Assert.assertEquals(expr, rna.expr);
            Assert.assertEquals(mute, rna.mute);
            rna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
    }

    @Test
    public void dealRnaPut() {
        checkRnaPut(single, "// RNA:PUT /who/meepo/", "map", "who", "meepo", false, "// RNA:PUT /who/meepo/");
        checkRnaPut(single, "// RNA:PUT !/who/meepo/", "map", "who", "meepo", true, "// RNA:PUT !/who/meepo/");
        checkRnaPut(single, "// RNA:PUT js/how/2*(1+3)/", "js", "how", "2*(1+3)", false, "// RNA:PUT js/how/2*(1+3)/");
        checkRnaPut(single, "// RNA:PUT js!/how/2*(1+3)/", "js", "how", "2*(1+3)", true, "// RNA:PUT js!/how/2*(1+3)/");
        checkRnaPut(single, "// RNA:PUT ///", "map", "", "", false, "// RNA:PUT ///");
        checkRnaPut(single, "// RNA:PUT //meepo/", "map", "", "meepo", false, "// RNA:PUT //meepo/");
        checkRnaPut(single, "// RNA:PUT /who//", "map", "who", "", false, "// RNA:PUT /who//");
        checkRnaPut(single, "// RNA:PUT /who/meepo/ @@", "map", "who", "meepo", false, "// RNA:PUT /who/meepo/ @@");
        checkRnaPut(single, "// RNA:PUT raw/who/meepo/ @@", "raw", "who", "meepo", false, "// RNA:PUT raw/who/meepo/ @@");
        checkRnaPut(single, "// RNA:PUT /who/meepo/ @@\n", "map", "who", "meepo", false, "// RNA:PUT /who/meepo/ @@\n");
        checkRnaPut(single, "// RNA:PUT raw/who/meepo/ @@\n", "raw", "who", "meepo", false, "// RNA:PUT raw/who/meepo/ @@\n");

        checkRnaPut(single, "// RNA:PUT who/meepo/ @@\n", null, null, null, false, "// RNA:PUT who/meepo/ @@\n");
        checkRnaPut(single, "// RNA:PUT /who/meepo @@\n", null, null, null, false, "// RNA:PUT /who/meepo @@\n");

        checkRnaPut(level5, "/* RNA:PUT /who/meepo/*/", "map", "who", "meepo", false, "/* RNA:PUT /who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT js/how/2*(1+3)/*/", "js", "how", "2*(1+3)", false, "/* RNA:PUT js/how/2*(1+3)/*/");
        checkRnaPut(level5, "/* RNA:PUT js!/how/2*(1+3)/*/", "js", "how", "2*(1+3)", true, "/* RNA:PUT js!/how/2*(1+3)/*/");
        checkRnaPut(level5, "/* RNA:PUT ///*/", "map", "", "", false, "/* RNA:PUT ///*/");
        checkRnaPut(level5, "/* RNA:PUT //meepo/*/", "map", "", "meepo", false, "/* RNA:PUT //meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT /who//*/", "map", "who", "", false, "/* RNA:PUT /who//*/");
        checkRnaPut(level5, "/* RNA:PUT /who/meepo/*/@@", "map", "who", "meepo", false, "/* RNA:PUT /who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT raw/who/meepo/*/@@", "raw", "who", "meepo", false, "/* RNA:PUT raw/who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT /who/meepo/*/@@\n", "map", "who", "meepo", false, "/* RNA:PUT /who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT raw/who/meepo/*/@@\n", "raw", "who", "meepo", false, "/* RNA:PUT raw/who/meepo/*/");

        checkRnaPut(level5, "/* RNA:PUT who/meepo/*/", null, null, null, false, "/* RNA:PUT who/meepo/*/");
        checkRnaPut(level5, "/* RNA:PUT /who/meepo*/", null, null, null, false, "/* RNA:PUT /who/meepo*/");
    }

    private void checkRnaWhen(HiMeepo meepo, String txt, String tock, String type, boolean nope, String expr, boolean quiet, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$RNA_);

        Exon exon = Parser.dealRnaWhen(ctx);
        Assert.assertNotNull(exon);
        exon.check(ctx.errs, ctx.rngs);
        logger.debug(exon.toString());

        if (type == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            RnaWhen rna = (RnaWhen) exon;
            Assert.assertEquals(type, rna.type);
            Assert.assertEquals(nope, rna.nope);
            Assert.assertEquals(expr, rna.expr);
            Assert.assertEquals(tock, rna.tock);
            Assert.assertEquals(quiet, rna.mute);
            rna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
    }

    @Test
    public void dealRnaWhen() {
        checkRnaWhen(single, "// RNA:WHEN /yes/meepo/tock", "tock", "map", false, "meepo", false, "// RNA:WHEN /yes/meepo/tock");
        checkRnaWhen(single, "// RNA:WHEN !/not/meepo/tock1", "tock1", "map", true, "meepo", true, "// RNA:WHEN !/not/meepo/tock1");
        checkRnaWhen(single, "// RNA:WHEN js/yes/2*(1+3)/tock", "tock", "js", false, "2*(1+3)", false, "// RNA:WHEN js/yes/2*(1+3)/tock");
        checkRnaWhen(single, "// RNA:WHEN js!/yes/2*(1+3)/tock", "tock", "js", false, "2*(1+3)", true, "// RNA:WHEN js!/yes/2*(1+3)/tock");

        checkRnaWhen(level5, "/* RNA:WHEN /yes/meepo/tock*/", "tock", "map", false, "meepo", false, "/* RNA:WHEN /yes/meepo/tock*/");
        checkRnaWhen(level5, "/* RNA:WHEN !/not/meepo/tock*/", "tock", "map", true, "meepo", true, "/* RNA:WHEN !/not/meepo/tock*/");
        checkRnaWhen(level5, "/* RNA:WHEN js/not/2*(1+3)/tock*/", "tock", "js", true, "2*(1+3)", false, "/* RNA:WHEN js/not/2*(1+3)/tock*/");
        checkRnaWhen(level5, "/* RNA:WHEN js!/not/2*(1+3)/tock*/", "tock", "js", true, "2*(1+3)", true, "/* RNA:WHEN js!/not/2*(1+3)/tock*/");

        checkRnaWhen(level5, "/* RNA:WHEN js!/oth/2*(1+3)/tock*/", "tock", null, true, "2*(1+3)", true, "/* RNA:WHEN js!/oth/2*(1+3)/tock*/");
        checkRnaWhen(level5, "/* RNA:WHEN js!//2*(1+3)/tock*/", "tock", null, true, "2*(1+3)", true, "/* RNA:WHEN js!/oth/2*(1+3)/tock*/");
        checkRnaWhen(level5, "/* RNA:WHEN js!/not/2*(1+3)/*/", "tock", null, true, "2*(1+3)", true, "/* RNA:WHEN js!/oth/2*(1+3)/tock*/");
    }

    private void checkRnaEach(HiMeepo meepo, String txt, String tock, String type, int step, String expr, boolean quiet, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$RNA_);

        Exon exon = Parser.dealRnaEach(ctx);
        Assert.assertNotNull(exon);
        exon.check(ctx.errs, ctx.rngs);
        logger.debug(exon.toString());

        if (type == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            RnaEach rna = (RnaEach) exon;
            Assert.assertEquals(type, rna.type);
            Assert.assertEquals(step, rna.step);
            Assert.assertEquals(expr, rna.expr);
            Assert.assertEquals(tock, rna.tock);
            Assert.assertEquals(quiet, rna.mute);
            rna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
    }

    @Test
    public void dealRnaEach() {
        checkRnaEach(single, "// RNA:EACH /1/meepo/tock", "tock", "map", 1, "meepo", false, "// RNA:EACH /1/meepo/tock");
        checkRnaEach(single, "// RNA:EACH !/2/meepo/tock1", "tock1", "map", 2, "meepo", true, "// RNA:EACH !/2/meepo/tock1");
        checkRnaEach(single, "// RNA:EACH js/1/2*(1+3)/tock", "tock", "js", 1, "2*(1+3)", false, "// RNA:EACH js/1/2*(1+3)/tock");
        checkRnaEach(single, "// RNA:EACH js!/-1/2*(1+3)/tock", "tock", "js", -1, "2*(1+3)", true, "// RNA:EACH js!/-1/2*(1+3)/tock");

        checkRnaEach(level5, "/* RNA:EACH /1/meepo/tock*/", "tock", "map", 1, "meepo", false, "/* RNA:EACH /1/meepo/tock*/");
        checkRnaEach(level5, "/* RNA:EACH !/1/meepo/tock*/", "tock", "map", 1, "meepo", true, "/* RNA:EACH !/1/meepo/tock*/");
        checkRnaEach(level5, "/* RNA:EACH js/1/2*(1+3)/tock*/", "tock", "js", 1, "2*(1+3)", false, "/* RNA:EACH js/1/2*(1+3)/tock*/");
        checkRnaEach(level5, "/* RNA:EACH js!/1/2*(1+3)/tock*/", "tock", "js", 1, "2*(1+3)", true, "/* RNA:EACH js!/1/2*(1+3)/tock*/");

        checkRnaEach(level5, "/* RNA:EACH js!//2*(1+3)/tock*/", "tock", null, 0, "2*(1+3)", true, "/* RNA:EACH js!//2*(1+3)/tock*/");
        checkRnaEach(level5, "/* RNA:EACH js!/not/2*(1+3)/*/", "tock", null, 0, "2*(1+3)", true, "/* RNA:EACH js!/not/2*(1+3)/tock*/");
    }

    private void checkRnaElse(HiMeepo meepo, String txt, String tock, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$RNA_);

        Exon exon = Parser.dealRnaElse(ctx);
        Assert.assertNotNull(exon);
        logger.debug(exon.toString());

        if (tock == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            RnaElse rna = (RnaElse) exon;
            Assert.assertEquals(tock, rna.tock);
            rna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
    }

    @Test
    public void dealRnaElse() {
        checkRnaElse(single, "// RNA:ELSE tock", "tock", "// RNA:ELSE tock");
        checkRnaElse(single, "// RNA:ELSE tock1", "tock1", "// RNA:ELSE tock1");

        checkRnaElse(level5, "/* RNA:ELSE tock*/", "tock", "/* RNA:ELSE tock*/");
        checkRnaElse(level5, "/* RNA:ELSE tock1*/", "tock1", "/* RNA:ELSE tock1*/");

        checkRnaElse(level5, "/* RNA:ELSE*/", null, "/* RNA:ELSE js!/oth/2*(1+3)/tock*/");
        checkRnaElse(level5, "/* RNA:ELSE    */", null, "/* RNA:ELSE js!/oth/2*(1+3)/tock*/");
    }

    private void checkRnaDone(HiMeepo meepo, String txt, String name, String build) {
        TestCtx ctx = new TestCtx(txt, meepo);
        Parser.markHiMeepo(ctx);
        Parser.findXnaGrp(ctx, TKN$RNA_);

        Exon exon = Parser.dealRnaDone(ctx);
        Assert.assertNotNull(exon);
        logger.debug(exon.toString());

        if (name == null) {
            Assert.assertEquals(0, exon.edge.until);
        } else {
            CharArrayWriter buf = new CharArrayWriter();
            RnaDone rna = (RnaDone) exon;
            String[] nms = name.split("[, ]+");
            assertEquals(nms.length, rna.name.size());
            for (String nm : nms) {
                assertTrue(rna.name.contains(nm));
            }
            rna.merge(newAcid(ctx), buf);
            assertEquals("", buf.toString());
            buf.reset();
            exon.build(buf);
            assertEquals(build, buf.toString());
        }
    }

    @Test
    public void dealRnaDone() {
        checkRnaDone(single, "// RNA:DONE 黑皇杖 id", "黑皇杖,id", "// RNA:DONE 黑皇杖 id");
        checkRnaDone(single, "@@// RNA:DONE 黑皇杖 id @@", "黑皇杖,id,@@", "// RNA:DONE 黑皇杖 id @@");
        checkRnaDone(single, "@@// RNA:DONE 黑皇杖 id @@\n", "黑皇杖,id,@@", "// RNA:DONE 黑皇杖 id @@\n");

        checkRnaDone(single, "@@// RNA:DONE \n", null, "// RNA:DONE \n");
        checkRnaDone(single, "@@// RNA:DONE\n", null, "// RNA:DONE\n");

        checkRnaDone(level5, "/* RNA:DONE 黑皇杖 id */", "黑皇杖,id", "/* RNA:DONE 黑皇杖 id */");
        checkRnaDone(level5, "@@/* RNA:DONE 黑皇杖 id */", "黑皇杖,id", "/* RNA:DONE 黑皇杖 id */");
        checkRnaDone(level5, "@@/* RNA:DONE 黑皇杖 id */\n", "黑皇杖,id", "/* RNA:DONE 黑皇杖 id */");
        checkRnaDone(level5, "@@/* RNA:DONE 黑皇杖 id */@@", "黑皇杖,id", "/* RNA:DONE 黑皇杖 id */");
        checkRnaDone(level5, "@@/* RNA:DONE 黑皇杖 id */\n@@", "黑皇杖,id", "/* RNA:DONE 黑皇杖 id */");

        checkRnaDone(level5, "@@/* RNA:DONE */\n", null, "/* RNA:DONE */\n");
        checkRnaDone(level5, "@@/* RNA:DONE*/\n", null, "/* RNA:DONE*/\n");
    }
}