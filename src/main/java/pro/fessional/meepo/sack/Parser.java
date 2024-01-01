package pro.fessional.meepo.sack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.dna.DnaBkb;
import pro.fessional.meepo.bind.dna.DnaEnd;
import pro.fessional.meepo.bind.dna.DnaRaw;
import pro.fessional.meepo.bind.dna.DnaSet;
import pro.fessional.meepo.bind.dna.DnaSon;
import pro.fessional.meepo.bind.kin.Bar;
import pro.fessional.meepo.bind.kin.Prc;
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
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.poof.RngChecker;
import pro.fessional.meepo.util.Read;
import pro.fessional.meepo.util.Seek;

import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static pro.fessional.meepo.bind.Const.ENGINE$MAP;
import static pro.fessional.meepo.bind.Const.TKN$DNA_;
import static pro.fessional.meepo.bind.Const.TKN$DNA_BKB;
import static pro.fessional.meepo.bind.Const.TKN$DNA_END;
import static pro.fessional.meepo.bind.Const.TKN$DNA_RAW;
import static pro.fessional.meepo.bind.Const.TKN$DNA_SET;
import static pro.fessional.meepo.bind.Const.TKN$DNA_SON;
import static pro.fessional.meepo.bind.Const.TKN$HIMEEPO;
import static pro.fessional.meepo.bind.Const.TKN$RNA_;
import static pro.fessional.meepo.bind.Const.TKN$RNA_DONE;
import static pro.fessional.meepo.bind.Const.TKN$RNA_EACH;
import static pro.fessional.meepo.bind.Const.TKN$RNA_ELSE;
import static pro.fessional.meepo.bind.Const.TKN$RNA_PUT;
import static pro.fessional.meepo.bind.Const.TKN$RNA_RUN;
import static pro.fessional.meepo.bind.Const.TKN$RNA_USE;
import static pro.fessional.meepo.bind.Const.TKN$RNA_WHEN;
import static pro.fessional.meepo.bind.Const.TKN$WHEN_NOT;
import static pro.fessional.meepo.bind.Const.TKN$WHEN_YES;

/**
 * <pre>
 * Meepo template parser, thead safe.
 *
 * When syntax errors are found in the directive,
 * `lax` mode to log as warn and treat as plain text,
 * `strict` mode to throw exceptions
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class Parser {

    protected static final Logger logger = LoggerFactory.getLogger(Parser.class);
    protected static final Exon SkipThis = new Exon("", new Clop(0, 0, 1, 1));
    protected static final Exon DealText = new Exon("", new Clop(0, 0, 1, 1));
    protected static final int RegxFlag = Pattern.UNIX_LINES | Pattern.MULTILINE;

    /**
     * Parse template, strict mode.
     *
     * @param txt text to parse
     * @return Gene
     */
    @Contract("null->null;!null->!null")
    public static Gene parse(String txt) {
        return parse(txt, false);
    }

    /**
     * Parse template.
     *
     * @param lax lax mode (not strict)
     * @param txt text to parse
     * @return Gene
     */
    @Contract("null,_->null;!null,_->!null")
    public static Gene parse(String txt, boolean lax) {
        return parse(txt, null, lax);
    }

    /**
     * Parse template, strict mode.
     * Set the template path, then others can be `include`  by relative path.
     *
     * @param txt text to parse
     * @param pwd template path
     * @return Gene
     */
    @Contract("null, _->null;!null, _->!null")
    public static Gene parse(String txt, String pwd) {
        return parse(txt, pwd, false);
    }

    /**
     * Parse template, strict mode.
     * Set the template path, then others can be `include`  by relative path.
     *
     * @param txt text to parse
     * @param pwd template path
     * @param lax lax mode (not strict)
     * @return Gene
     */
    @Contract("null,_,_->null;!null,_,_->!null")
    public static Gene parse(String txt, String pwd, boolean lax) {
        if (txt == null) return null;
        final Ctx ctx = new Ctx(txt, pwd, lax);

        parse(ctx);

        return ctx.toGene();
    }

    protected static void parse(Ctx ctx) {
        for (ctx.edge0 = 0; ctx.edge0 < ctx.end; ctx.edge0 = ctx.edge1) {

            // Mark Meepo, segment text, find edge0
            if (markHiMeepo(ctx)) {
                continue;
            }

            Exon exon = SkipThis;
            // find `DNA:`
            if (findXnaGrp(ctx, TKN$DNA_)) {
                exon = dealDnaGroup(ctx);
            }

            // find `RNA:`
            else if (findXnaGrp(ctx, TKN$RNA_)) {
                exon = dealRnaGroup(ctx);
            }

            dealTxtPlain(ctx, ctx.edge0, exon); // looping
        }

        dealTxtPlain(ctx, ctx.end, DealText); // last
    }

    /**
     * deal DNA group
     *
     * @param ctx the context
     * @return Exon
     */
    @NotNull
    protected static Exon dealDnaGroup(Ctx ctx) {
        Exon exon = dealDnaEnd(ctx);
        if (ctx.notBkb()) { // DNA:GROUP
            if (exon == null) {
                exon = dealDnaBkb(ctx);
            }
            if (exon == null) {
                exon = dealDnaSet(ctx);
            }
            if (exon == null) {
                exon = dealDnaRaw(ctx);
            }
            if (exon == null) {
                exon = dealDnaSon(ctx);
            }
        }
        return exon == null ? SkipThis : exon;  // DNA:GROUP
    }

    /**
     * deal RNA group
     *
     * @param ctx the context
     * @return Exon
     */
    @NotNull
    protected static Exon dealRnaGroup(Ctx ctx) {
        Exon exon = null;
        if (ctx.notBkb()) { // RNA:GROUP
            exon = dealRnaPut(ctx);
            if (exon == null) {
                exon = dealRnaUse(ctx);
            }
            if (exon == null) {
                exon = dealRnaRun(ctx);
            }
            if (exon == null) {
                exon = dealRnaWhen(ctx);
            }
            if (exon == null) {
                exon = dealRnaEach(ctx);
            }
            if (exon == null) {
                exon = dealRnaElse(ctx);
            }
            if (exon == null) {
                exon = dealRnaDone(ctx);
            }
        }
        return exon == null ? SkipThis : exon; // RNA:GROUP
    }

    /**
     * find and handle Meepo, and decide whether to stop follow-up processing
     *
     * @param ctx the context
     * @return Whether to stop
     */
    protected static boolean markHiMeepo(Ctx ctx) {
        final String txt = ctx.txt;
        final int edg0 = ctx.edge0;
        final int end1 = ctx.end;
        HiMeepo meepo = ctx.meepo;
        if (meepo == null) { // 1st find
            meepo = bornHiMeepo(ctx, edg0, end1);
            if (meepo == null) { // Not meepo
                logger.trace("[👹Parse:Parse:markHiMeepo] no meepo found");
                dealTxtPlain(ctx, end1, null);
                ctx.edge1 = end1;
                return true;
            }
            else {
                logger.trace("[👹Parse:Parse:markHiMeepo] find first meepo at edge={}", meepo.edge);
            }
        }
        else {
            // mark start point
            String head = meepo.head;
            int head0 = Seek.seekToken(txt, edg0, end1, head, meepo.echo);

            meepo = null;
            if (ctx.notBkb()) { // Meepo-start point
                meepo = bornHiMeepo(ctx, edg0, head0 > edg0 ? head0 : end1);
            }
            else {
                logger.trace("[👹Parse:markHiMeepo] skip born-meepo in bkb");
            }

            if (meepo == null) {
                if (head0 >= edg0) {
                    ctx.edge0 = head0;
                    ctx.edge1 = head0 + head.length();
                    logger.trace("[👹Parse:markHiMeepo] find meepo head at edge0={}", head0);
                    return false;
                }
                else {
                    dealTxtPlain(ctx, end1, DealText); // headless Meepo
                    ctx.edge1 = end1;
                    logger.trace("[👹Parse:markHiMeepo] no meepo head found");
                    return true;
                }
            }
            else {
                logger.trace("[👹Parse:markHiMeepo] find other-meepo at edge={}", meepo.edge);
            }
        }

        // new Meepo
        int edge0 = meepo.edge.start;
        int edge1 = meepo.edge.until;

        logger.trace("[👹markHiMeepo] deal text at done1={}, edge0={}", ctx.done1, edge0);
        dealTxtPlain(ctx, edge0, meepo); // before Meepo
        ctx.meepo = meepo;
        ctx.done1 = edge1;
        ctx.edge0 = edge0;
        ctx.edge1 = edge1;
        return true;
    }

    /**
     * handle plain text
     *
     * @param ctx   the context
     * @param edge0 the left edge
     * @param exon  exon
     */
    protected static void dealTxtPlain(Ctx ctx, int edge0, Exon exon) {
        final int done1 = ctx.done1;

        if (done1 >= edge0) {
            if (exon != null && exon != SkipThis && exon != DealText) { // append gene if done
                ctx.procExon(exon);
                ctx.done1 = exon.edge.until;
            }
            return;
        }

        if (exon == null) {
            logger.trace("deal whole text at done1={}, edge0={}", done1, edge0);
            TxtSimple dna = new TxtSimple(ctx.txt, ctx.newClop(done1, edge0));
            ctx.procExon(dna);
            ctx.done1 = edge0;
        }
        else if (exon == SkipThis) {  // skip for next
            logger.trace("skip for next at done1={}, edge0={}", done1, edge0);
        }
        else if (exon == DealText) {
            ctx.procText(done1, edge0);
            ctx.done1 = edge0;
            logger.trace("deal next at done1={}, edge0={}", done1, edge0);
        }
        else {
            if (ctx.notBkb()) { // handle text
                // apply directive
                ctx.procText(done1, edge0);
                // append directive
                ctx.procExon(exon);
                ctx.done1 = exon.edge.until;
            }
            else {
                if (ctx.endBkb(exon)) {
                    // BKB text
                    TxtSimple txt = new TxtSimple(ctx.txt, ctx.newClop(done1, edge0));
                    ctx.procExon(txt);
                    // append directive
                    ctx.procExon(exon);
                    ctx.done1 = exon.edge.until;
                }
                else {
                    logger.trace("skip for bkb at done1={}, edge0={}", done1, edge0);
                }
            }
        }
    }

    /**
     * find `DNA:` or `RNA:`
     *
     * @param ctx   the context
     * @param token token
     * @return Whether found
     */
    protected static boolean findXnaGrp(Ctx ctx, String token) {
        HiMeepo meepo = ctx.meepo;
        int edge0 = ctx.edge0;
        int off = edge0 + meepo.head.length();
        String text = ctx.txt;
        int main0 = Seek.seekFollow(text, off, ctx.end, token);
        if (main0 < 0) return false;

        int main1 = Seek.seekToken(text, main0, ctx.end, meepo.tail, false);
        int edge1;
        if (main1 < main0) {
            if (meepo.crlf) {
                main1 = ctx.end;
                edge1 = main1;
                logger.trace("[👹Parse:findXnaGrp] use the end as meepo-tail when CRLF");
            }
            else {
                logger.trace("[👹Parse:findXnaGrp] skip xna group without meepo-tail");
                return false;
            }
        }
        else {
            edge1 = main1 + meepo.tail.length();
            int grace = Seek.seekPrevGrace(text, main0, main1);
            if (grace > main0 && grace < main1) {
                main1 = grace + 1;
            }
        }

        ctx.main0 = main0;
        ctx.grpx1 = main0 + token.length();
        ctx.main1 = main1;
        ctx.edge1 = edge1;
        logger.trace("[👹Parse:findXnaGrp] find {} at main0={}", token, main0);
        return true;
    }

    /**
     * handle `DNA:RAW`
     */
    protected static Exon dealDnaRaw(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$DNA_RAW);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$DNA_RAW.length();
        int raw0 = Seek.seekNextGrace(txt, off, ctx.main1);
        DnaRaw dna = new DnaRaw(txt, ctx.toEdge(), Math.max(raw0, off), ctx.main1);
        logger.trace("[👹Parse:dealDnaRaw] find DNA:RAW at token0={}", tkn0);
        return dna;
    }

    /**
     * handle `DNA:SON`
     */
    protected static Exon dealDnaSon(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$DNA_SON);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$DNA_SON.length();
        int raw0 = Seek.seekNextGrace(txt, off, ctx.main1);
        String path = txt.substring(Math.max(raw0, off), ctx.main1);
        DnaSon dna = new DnaSon(txt, ctx.toEdge(), path);
        logger.trace("[👹Parse:dealDnaSon] find DNA:SON at token0={}", tkn0);
        return dna;
    }

    /**
     * handle `DNA:BKB`
     */
    protected static Exon dealDnaBkb(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$DNA_BKB);
        if (tkn0 < 0) return null;

        int[] pos = Seek.seekNextWords(txt, tkn0 + TKN$DNA_BKB.length(), ctx.main1);
        final Exon dna;
        if (pos[1] > pos[0]) {
            trimEdge(ctx); // DNA:BKB
            String name = txt.substring(pos[0], pos[1]);
            dna = new DnaBkb(txt, ctx.toEdge(), name);
            logger.trace("[👹Parse:dealDnaBkb] find DNA:BKB at token0={}", tkn0);
        }
        else {
            dna = SkipThis; // DNA:BKB
            if (ctx.lax) {
                logger.warn("[👹Parse:dealDnaBkb] skip bad DNA:BKB without name");
            }
            else {
                ctx.fail.add(new IllegalStateException("DNA:BKB [" + ctx.main0 + "," + ctx.main1 + "], without name"));
            }
        }
        return dna;
    }

    /**
     * handle `DNA:END`
     */
    protected static Exon dealDnaEnd(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$DNA_END);
        if (tkn0 < 0) return null;

        final List<String> names = parseName(ctx, tkn0, TKN$DNA_END);

        final Exon dna;
        if (names.isEmpty()) {
            dna = SkipThis; // DNA:END
            if (ctx.lax) {
                logger.warn("[👹Parse:dealDnaEnd] skip bad DNA:END without name");
            }
            else {
                ctx.fail.add(new IllegalStateException("DNA:END [" + ctx.main0 + "," + ctx.main1 + "], without name"));
            }
        }
        else {
            trimEdge(ctx); // DNA:END
            dna = new DnaEnd(txt, ctx.toEdge(), names);
            logger.trace("[👹Parse:dealDnaEnd] find DNA:END at token0={}", tkn0);
        }
        return dna;
    }

    /**
     * handle `DNA:SET`
     */
    protected static Exon dealDnaSet(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$DNA_SET);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$DNA_SET.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String note;
        if (spl0 < 0) {
            note = "without split-bar";
        }
        else {
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1);
        }

        final Exon dna;
        if (note == null) {
            trimEdge(ctx); // DNA:SET
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]), RegxFlag);
            String repl = txt.substring(pos3[1] + 1, pos3[2]);

            Life life = parseLife(txt, pos3[2], ctx.main1);
            dna = new DnaSet(txt, ctx.toEdge(), life, find, repl);
            logger.trace("[👹Parse:dealDnaSet] find DNA:SET at token0={}", tkn0);
        }
        else {
            dna = SkipThis; // DNA:SET
            if (ctx.lax) {
                logger.warn("[👹Parse:dealDnaSet] skip bad DNA:SET {}", note);
            }
            else {
                ctx.fail.add(new IllegalStateException("DNA:SET [" + ctx.main0 + "," + ctx.main1 + "], " + note));
            }
        }
        return dna;
    }

    /**
     * handle `DNA:RUN`
     */
    protected static Exon dealRnaRun(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_RUN);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$RNA_RUN.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:RUN, without split-bar";
        }
        else {
            int[] typ2 = Seek.seekNextAlnum(txt, off, spl0);
            mute = parseMute(txt, typ2);
            type = parseType(txt, typ2);
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1);
        }

        final Exon rna;
        if (note == null) {
            trimEdge(ctx); // RNA:RUN
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]), RegxFlag);
            String expr = txt.substring(pos3[1] + 1, pos3[2]);
            Life life = parseLife(txt, pos3[2], ctx.main1);
            rna = new RnaRun(txt, ctx.toEdge(), life, type, find, expr, mute);
            logger.trace("[👹Parse:dealRnaRun] find RNA:RUN at token0={}", tkn0);
        }
        else {
            rna = SkipThis; // RNA:RUN
            if (ctx.lax) {
                logger.warn("[👹Parse:dealRnaRun] skip bad RNA:RUN {}", note);
            }
            else {
                ctx.fail.add(new IllegalStateException("DNA:RUN [" + ctx.main0 + "," + ctx.main1 + "], " + note));
            }
        }
        return rna;
    }

    /**
     * handle `DNA:USE`
     */
    protected static Exon dealRnaUse(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_USE);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$RNA_USE.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:USE, without split-bar";
        }
        else {
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1);
        }

        final Exon rna;
        if (note == null) {
            trimEdge(ctx); // RNA:USE
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]), RegxFlag);
            String para = txt.substring(pos3[1] + 1, pos3[2]);
            Life life = parseLife(txt, pos3[2], ctx.main1);
            rna = new RnaUse(txt, ctx.toEdge(), life, find, para);
            logger.trace("[👹Parse:dealRnaUse] find RNA:USE at token0={}", tkn0);
        }
        else {
            rna = SkipThis; // RNA:USE
            if (ctx.lax) {
                logger.warn("[👹Parse:dealRnaUse] skip bad RNA:USE {}", note);
            }
            else {
                ctx.fail.add(new IllegalStateException("RNA:USE [" + ctx.main0 + "," + ctx.main1 + "], " + note));
            }
        }
        return rna;
    }

    /**
     * handle `DNA:PUT`
     */
    protected static Exon dealRnaPut(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_PUT);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$RNA_PUT.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:PUT, without split-bar";
        }
        else {
            int[] typ2 = Seek.seekNextAlnum(txt, off, spl0);
            mute = parseMute(txt, typ2);
            type = parseType(txt, typ2);
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1);
        }

        final Exon rna;
        if (note == null) {
            trimEdge(ctx); // RNA:PUT
            String para = txt.substring(pos3[0] + 1, pos3[1]);
            String expr = txt.substring(pos3[1] + 1, pos3[2]);
            rna = new RnaPut(txt, ctx.toEdge(), type, para, expr, mute);
            logger.trace("[👹Parse:dealRnaPut] find RNA:PUT at token0={}", tkn0);
        }
        else {
            rna = SkipThis; // RNA:PUT
            if (ctx.lax) {
                logger.warn("[👹Parse:dealRnaPut] skip bad RNA:PUT {}", note);
            }
            else {
                ctx.fail.add(new IllegalStateException("RNA:PUT [" + ctx.main0 + "," + ctx.main1 + "], " + note));
            }
        }
        return rna;
    }

    /**
     * handle `DNA:WHEN`
     */
    protected static Exon dealRnaWhen(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_WHEN);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$RNA_WHEN.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:WHEN, without split-bar";
        }
        else {
            int[] typ2 = Seek.seekNextAlnum(txt, off, spl0);
            mute = parseMute(txt, typ2);
            type = parseType(txt, typ2);
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1);
        }

        boolean nope = false;
        if (note == null) {
            String yn = txt.substring(pos3[0] + 1, pos3[1]);
            if (yn.isEmpty()) {
                note = "control is empty, need " + TKN$WHEN_YES + "|" + TKN$WHEN_NOT;
            }
            else if (TKN$WHEN_YES.contains(yn)) {
                // IGNORE nope = false;
            }
            else if (TKN$WHEN_NOT.contains(yn)) {
                nope = true;
            }
            else {
                note = "control NOT in (" + TKN$WHEN_YES + "," + TKN$WHEN_NOT + ")";
            }
        }

        String tock = null;
        if (note == null) {
            tock = parseTock(txt, pos3[2], ctx.main1);
            if (tock.isEmpty()) {
                note = "need tock name";
            }
        }

        final Exon rna;
        if (note == null) {
            trimEdge(ctx); // RNA:WHEN
            String expr = txt.substring(pos3[1] + 1, pos3[2]);
            rna = new RnaWhen(txt, ctx.toEdge(), tock, type, nope, expr, mute);
            logger.trace("[👹Parse:dealRnaWhen] find RNA:WHEN at token0={}", tkn0);
        }
        else {
            rna = SkipThis; // RNA:WHEN
            if (ctx.lax) {
                logger.warn("[👹Parse:dealRnaWhen] skip bad RNA:WHEN {}", note);
            }
            else {
                ctx.fail.add(new IllegalStateException("RNA:WHEN [" + ctx.main0 + "," + ctx.main1 + "], " + note));
            }
        }
        return rna;
    }

    /**
     * handle `DNA:EACH`
     */
    protected static Exon dealRnaEach(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_EACH);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN$RNA_EACH.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:EACH, without split-bar";
        }
        else {
            int[] typ2 = Seek.seekNextAlnum(txt, off, spl0);
            mute = parseMute(txt, typ2);
            type = parseType(txt, typ2);
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1);
        }

        int step = 1;
        if (note == null) {
            String x = txt.substring(pos3[0] + 1, pos3[1]);
            try {
                step = Integer.parseInt(x);
            }
            catch (NumberFormatException e) {
                step = 0;
            }
            if (step == 0) {
                note = "need non-zero number step";
            }
        }

        String tock = null;
        if (note == null) {
            tock = parseTock(txt, pos3[2], ctx.main1);
            if (tock.isEmpty()) {
                note = "need tock name";
            }
        }

        final Exon rna;
        if (note == null) {
            trimEdge(ctx); // RNA:EACH
            String expr = txt.substring(pos3[1] + 1, pos3[2]);
            rna = new RnaEach(txt, ctx.toEdge(), tock, type, step, expr, mute);
            logger.trace("[👹Parse:dealRnaWhen] find RNA:EACH at token0={}", tkn0);
        }
        else {
            rna = SkipThis; // RNA:EACH
            if (ctx.lax) {
                logger.warn("[👹Parse:dealRnaWhen] skip bad RNA:EACH {}", note);
            }
            else {
                ctx.fail.add(new IllegalStateException("bad RNA:EACH [" + ctx.main0 + "," + ctx.main1 + "], " + note));
            }
        }
        return rna;
    }

    /**
     * handle `DNA:ELSE`
     */
    protected static Exon dealRnaElse(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_ELSE);
        if (tkn0 < 0) return null;

        int[] pos = Seek.seekNextWords(txt, tkn0 + TKN$RNA_ELSE.length(), ctx.main1);
        final Exon dna;
        if (pos[1] > pos[0]) {
            trimEdge(ctx); // RNA:ELSE
            String name = txt.substring(pos[0], pos[1]);
            dna = new RnaElse(txt, ctx.toEdge(), name);
            logger.trace("[👹Parse:dealRnaElse] find RNA:ELSE at token0={}", tkn0);
        }
        else {
            dna = SkipThis; // RNA:ELSE
            if (ctx.lax) {
                logger.warn("[👹Parse:dealRnaElse] skip bad RNA:ELSE without name");
            }
            else {
                ctx.fail.add(new IllegalStateException("RNA:ELSE [" + ctx.main0 + "," + ctx.main1 + "], without name"));
            }
        }
        return dna;
    }

    /**
     * handle `DNA:DONE`
     */
    protected static Exon dealRnaDone(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_DONE);
        if (tkn0 < 0) return null;

        final List<String> names = parseName(ctx, tkn0, TKN$RNA_DONE);

        final Exon dna;
        if (names.isEmpty()) {
            dna = SkipThis; // RNA:DONE
            if (ctx.lax) {
                logger.warn("[👹Parse:dealRnaDone] skip bad RNA:DONE without name");
            }
            else {
                ctx.fail.add(new IllegalStateException("RNA:DONE [" + ctx.main0 + "," + ctx.main1 + "], without name"));
            }
        }
        else {
            trimEdge(ctx); // RNA:DONE
            dna = new RnaDone(txt, ctx.toEdge(), names);
            logger.trace("[👹Parse:dealRnaDone] find RNA:DONE at token0={}", tkn0);
        }
        return dna;
    }

    // ////////////

    private static HiMeepo bornHiMeepo(Ctx ctx, int edge0, int edge1) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekToken(txt, edge0, edge1, TKN$HIMEEPO, false);
        if (tkn0 <= 0) return null;

        int[] hd = Seek.seekPrevWords(txt, edge0, tkn0);
        int hd0 = Seek.seekPrevEdge(txt, hd[0]);
        if (hd[0] == hd[1] || hd0 < 0) {
            logger.trace("[👹Parse:bornHiMeepo] skip HI-MEEPO without prefix");
            return null;
        }
        String head = txt.substring(hd[0], hd[1]);
        int tk1 = tkn0 + TKN$HIMEEPO.length();
        boolean trim = true;
        if (tk1 < edge1 && txt.charAt(tk1) == '!') {
            tk1++;
            trim = false;
        }
        int[] tl = Seek.seekNextWords(txt, tk1, edge1);
        int tl1 = Seek.seekNextEdge(txt, tl[1]);
        if (tl1 < 0) {
            logger.trace("[👹Parse:bornHiMeepo] skip HI-MEEPO without suffix");
            return null;
        }

        String tail;
        if (tl[1] > tl[0]) {
            tail = txt.substring(tl[0], tl[1]);
        }
        else {
            tail = "\n";
            if (tl[1] < txt.length() && txt.charAt(tl[1]) == '\n') {
                tl[1] = tl[1] + 1;
            }
        }

        if (trim) {
            if (hd0 < hd[0]) {
                hd[0] = hd0;
            }
            if (tl1 > tl[1]) {
                tl[1] = tl1;
            }
        }

        return new HiMeepo(txt, ctx.newClop(hd[0], tl[1]), head, tail, trim);
    }

    private static void trimEdge(Ctx ctx) {
        HiMeepo meepo = ctx.meepo;
        if (meepo.trim) {
            String text = ctx.txt;
            int edge0 = ctx.edge0;
            int edge1 = ctx.edge1;
            int eg0 = Seek.seekPrevEdge(text, edge0);
            if (eg0 < edge0 && eg0 >= 0) {
                ctx.edge0 = eg0;
            }
            int eg1 = Seek.seekNextEdge(text, edge1);
            if (eg1 > edge1) {
                ctx.edge1 = eg1;
            }
        }
    }

    private static boolean parseMute(String txt, int[] typ2) {
        if (typ2[0] == typ2[1]) {
            return txt.charAt(typ2[1] - 1) == '!';
        }
        else {
            return txt.charAt(typ2[1]) == '!';
        }
    }

    @NotNull
    private static String parseType(String txt, int[] typ2) {
        return typ2[1] <= typ2[0] ? ENGINE$MAP : txt.substring(typ2[0], typ2[1]);
    }

    @NotNull
    private static Life parseLife(String txt, int spl2, int main1) {
        int lf0 = spl2 + 1;
        int[] pw = Seek.seekNextWords(txt, lf0, main1);
        Life life;
        if (pw[0] == lf0 && pw[1] > pw[0]) {
            life = Life.parse(txt.substring(pw[0], pw[1]));
        }
        else {
            life = Life.nobodyOne();
        }
        return life;
    }

    @NotNull
    private static String parseTock(String txt, int spl2, int main1) {
        int lf0 = spl2 + 1;
        int[] pw = Seek.seekNextWords(txt, lf0, main1);
        String tock;
        if (pw[0] == lf0 && pw[1] > pw[0]) {
            tock = txt.substring(pw[0], pw[1]);
        }
        else {
            tock = Const.TXT$EMPTY;
        }
        return tock;
    }

    private static String parseSplit(String txt, int[] pos3, int main1) {
        char splt = txt.charAt(pos3[0]);
        for (int i = 1; i < pos3.length; i++) {
            int o1 = pos3[i - 1] + 1;
            int p = txt.indexOf(splt, o1);
            if (p < o1 || p >= main1) {
                return "need 3 parts in split-bar";
            }
            pos3[i] = p;
        }
        return null;
    }

    @NotNull
    private static List<String> parseName(Ctx ctx, int tkn0, String token) {
        String txt = ctx.txt;
        final List<String> names = new ArrayList<>(8);
        final String head = ctx.meepo.head;
        int off = tkn0 + token.length();
        while (true) {
            int[] name = Seek.seekNextWords(txt, off, ctx.main1);
            if (name[1] > name[0]) {
                String nm = txt.substring(name[0], name[1]);
                if (nm.contains(head)) {
                    break;
                }
                else {
                    names.add(nm);
                    off = name[1];
                }
            }
            else {
                break;
            }
        }
        return names;
    }

    ////////////

    protected static class G {
        protected final String type;
        protected final String tock;
        protected final ArrayList<Exon> gene;

        public G(String type, String tock, ArrayList<Exon> gene) {
            this.type = type;
            this.tock = tock;
            this.gene = gene;
        }

        @Override
        public String toString() {
            return "G{" +
                   "type='" + type + '\'' +
                   ", tock='" + tock + '\'' +
                   ", gene=" + gene +
                   '}';
        }
    }

    /**
     * Naming convention: 0 - start point, include; 1 - end point, exclude;
     */
    protected static class Ctx {
        protected final String txt; // template raw text
        protected final String pwd; // template path
        protected final boolean lax; // lax mode
        protected final int end; // text length
        protected int done1 = 0; // parsed end point (exclude)
        protected int edge0 = 0; // to parse block start point (include)
        protected int main0 = -1; // to parse directive start point (include)
        protected int grpx1 = -1; // XNAGroup end point (exclude)
        protected int main1 = -1; // to parse directive end point (exclude)
        protected int edge1 = -1; // to parse block end point (exclude)
        protected HiMeepo meepo = null; // current scope Meepo

        protected final ArrayDeque<G> tree = new ArrayDeque<>();

        protected final HashSet<String> bkbs = new HashSet<>();
        protected final ArrayList<Exon> proc = new ArrayList<>();
        protected final HashMap<String, Life> life = new HashMap<>();
        protected final TreeMap<Integer, Integer> line = new TreeMap<>();
        protected final StringBuilder errs = new StringBuilder();
        protected final RngChecker rngs = new RngChecker();
        protected final ArrayList<RuntimeException> fail = new ArrayList<>();

        public Ctx(String txt, String pwd, boolean lax) {
            this.txt = txt;
            this.pwd = pwd;
            this.end = txt.length();
            this.lax = lax;

            tree.offerLast(new G("ROOT", Const.TXT$EMPTY, new ArrayList<>()));

            //
            int ln = 1;
            line.put(0, ln);
            for (int i = 0; i < end; i++) {
                if (txt.charAt(i) == '\n') {
                    line.put(i, ln++);
                }
            }
            line.put(end, ln);
        }

        public Clop toEdge() {
            return newClop(edge0, edge1);
        }

        public boolean notBkb() {
            return bkbs.isEmpty();
        }

        public boolean endBkb(Exon exon) {
            if (exon instanceof DnaEnd) {
                bkbs.removeAll(((DnaEnd) exon).name);
            }
            return bkbs.isEmpty();
        }

        public ArrayList<Exon> getGene() {
            G g = tree.getLast();
            return g.gene;
        }

        public void addGene(Exon exon) {
            exon.check(errs, rngs);
            G g = tree.getLast();
            g.gene.add(exon);
            logger.trace("[👹Parse:addGene] append gene {}, stack={}, edge={}", exon.getClass().getSimpleName(), tree.size(), exon.edge);
        }

        public void procExon(Exon exon) {
            if (exon instanceof DnaBkb) {
                DnaBkb dna = (DnaBkb) exon;
                bkbs.add(dna.name);
            }
            else if (exon instanceof DnaEnd) {
                Set<String> name = ((DnaEnd) exon).name;
                for (String k : name) {
                    Life lf = life.remove(k);
                    if (lf != null) lf.close();
                }
            }

            if (exon instanceof Tick) {
                Life lf = ((Tick) exon).life;
                String nm = lf.name;
                if (nm.length() > 0) {
                    this.life.put(nm, lf);
                }
            }

            // adjust gene stack - start
            if (exon instanceof RnaDone) {
                RnaDone rna = (RnaDone) exon;
                Set<String> done = rna.name;
                String tock = null;
                while (true) {
                    G g = tree.getLast();
                    String tck = g.tock;
                    if (done.contains(tck)) {
                        tree.removeLast();
                        if (tock == null || !tock.equalsIgnoreCase(tck)) {
                            addGene(rna.copy(tck));
                            tock = tck;
                        }
                        logger.trace("[👹Parse:procExon] finish RNA:DONE, tock={}, stack={}", tck, tree.size());
                    }
                    else {
                        break;
                    }
                }
                logger.trace("[👹Parse:procExon] finish all RNA:DONE, stack={}", tree.size());
            }
            else if (exon instanceof Tock) {
                String clz = exon.getClass().getSimpleName();
                Tock t = (Tock) exon;
                G g = tree.getLast();
                // replace the same tock, otherwise append
                if (g.tock.equalsIgnoreCase(t.tock)) {
                    tree.removeLast();
                    addGene(exon);
                    tree.addLast(new G(clz, t.tock, t.gene));
                    logger.trace("[👹Parse:procExon] adjust {}, tock={}, stack={}", clz, t.tock, tree.size());
                }
                else {
                    addGene(exon);
                    tree.addLast(new G(clz, t.tock, t.gene));
                    logger.trace("[👹Parse:procExon] append {}, tock={}, stack={}", clz, t.tock, tree.size());
                }
            }
            // adjust gene stack - end
            else if (exon instanceof DnaSon) {
                String clz = exon.getClass().getSimpleName();
                logger.trace("[👹Parse:procExon] append {}, and parse sons", clz);
                DnaSon ds = (DnaSon) exon;
                final String sub = Read.read(ds.path, pwd);
                Ctx son = new Ctx(sub, ds.path, lax);
                parse(son);
                son.okGene();
                List<Exon> gns = son.getGene();
                for (Exon gn : gns) {
                    gn.check(errs, rngs);
                }
                ds.gene.addAll(gns);
                addGene(ds);
            }
            else {
                addGene(exon);
            }

            if (exon instanceof Prc) {
                proc.add(exon);
            }
        }

        public void procText(final int done1, final int edge1) {
            if (done1 >= edge1) return;

            if (proc.isEmpty()) {
                TxtSimple txt = new TxtSimple(this.txt, newClop(done1, edge1));
                addGene(txt);
                return;
            }

            final List<Exon.N> rst = new ArrayList<>();
            final String text = txt.substring(done1, edge1);
            for (Iterator<Exon> it = proc.iterator(); it.hasNext(); ) {
                Exon exon = it.next();
                Life.State st = exon.match(rst, text);
                if (st == Life.State.Dead) {
                    it.remove();
                }
            }

            if (rst.isEmpty()) {
                TxtSimple txt = new TxtSimple(this.txt, newClop(done1, edge1));
                addGene(txt);
                return;
            }

            for (Exon.N n1 : rst) {
                for (Exon.N n2 : rst) {
                    if (n1 != n2 && n1.cross(n2)) {
                        String err = "may use DNA:END to fix cross range, n1=" + altClop(n1, done1) + ", n2=" + altClop(n2, done1) + ", text=" + text;
                        fail.add(new IllegalStateException(err));
                    }
                }
            }

            Collections.sort(rst);
            int off = 0;
            for (Exon.N n : rst) {
                int st0 = n.start;
                if (off < st0) {
                    addGene(new TxtSimple(txt, newClop(off + done1, st0 + done1)));
                }
                List<Exon> appl;
                if (n.xna instanceof Bar) {
                    int bar = Seek.indent(text, st0);
                    appl = n.xna.apply(altClop(n, done1), txt, bar);
                }
                else {
                    appl = n.xna.apply(altClop(n, done1), txt, 0);
                }
                for (Exon exon : appl) {
                    addGene(exon);
                }
                off = n.until;
            }
            int len = text.length();
            if (off < len) {
                addGene(new TxtSimple(txt, newClop(off + done1, len + done1)));
            }
        }

        public void okGene() {
            if (errs.length() > 0) {
                if (lax) {
                    logger.warn(errs.toString());
                }
                else {
                    fail.add(new IllegalStateException(errs.toString()));
                }
            }
            if (tree.size() != 1) {
                StringBuilder sb = new StringBuilder("find UN-DONE RNA's Tock=");
                for (G g : tree) {
                    sb.append(g.tock);
                    sb.append(',');
                }
                fail.add(new IllegalStateException(sb.toString()));
            }

            if (!fail.isEmpty()) {
                StringWriter buff = new StringWriter();
                buff.append("\n=== Template Text ===\n");
                buff.append(txt);
                for (G g : tree) {
                    buff.append("\n=== Gene Tree ===\n");
                    Gene.graph(buff, g.gene, 1, 0);
                }
                throw new IllegalStateException(buff.toString(), fail.get(0));
            }
        }

        public Gene toGene() {
            okGene();
            G g = tree.pollLast();
            assert g != null;
            return new Gene(g.gene, rngs.getCheckedEngine());
        }

        private Clop newClop(int start, int until) {
            int line0 = line.ceilingEntry(start).getValue();
            int line1 = line.ceilingEntry(until).getValue();
            return new Clop(start, until, line0, Math.max(line0, line1));
        }

        private Clop altClop(Exon.N n, int off) {
            return newClop(n.start + off, n.until + off);
        }
    }
}
