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
 * static thread safe parser, if logger and you safe
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
     * 解析文本，严格模式。
     *
     * @param txt 文本
     * @return 基因
     */
    @Contract("null->null;!null->!null")
    public static Gene parse(String txt) {
        return parse(txt, false);
    }

    /**
     * 解析文本。发现指令的语法错误时，有放松模式（日志warn，并视为普通文本）和严格模式（抛异常）
     *
     * @param txt 文本
     * @param lax 放松模式
     * @return 基因
     */
    @Contract("null,_->null;!null,_->!null")
    public static Gene parse(String txt, boolean lax) {
        return parse(txt, null, lax);
    }

    /**
     * 解析文本，严格模式。设定模板路径，可以以相对路径include。
     *
     * @param txt 文本
     * @param pwd 模板位置
     * @return 基因
     */
    @Contract("null, _->null;!null, _->!null")
    public static Gene parse(String txt, String pwd) {
        return parse(txt, pwd, false);
    }

    /**
     * 解析文本。发现指令的语法错误时，有放松模式（日志warn，并视为普通文本）和严格模式（抛异常）
     * 设定模板路径，可以以相对路径include。
     *
     * @param txt 文本
     * @param pwd 模板位置
     * @param lax 放松模式
     * @return 基因
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

            // 标记米波，文本分段，找到edge0
            if (markHiMeepo(ctx)) {
                continue;
            }

            Exon exon = SkipThis;
            // 查找`DNA:`
            if (findXnaGrp(ctx, TKN$DNA_)) {
                exon = dealDnaGroup(ctx);
            }

            // 查找`RNA:`
            else if (findXnaGrp(ctx, TKN$RNA_)) {
                exon = dealRnaGroup(ctx);
            }

            dealTxtPlain(ctx, ctx.edge0, exon); // 循环处理
        }

        dealTxtPlain(ctx, ctx.end, DealText); // 处理最后
    }

    /**
     * 处理 DNA 组
     *
     * @param ctx 上下文
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
     * 处理 RNA 组
     *
     * @param ctx 上下文
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
     * 查找并处理meepo，是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理
     */
    protected static boolean markHiMeepo(Ctx ctx) {
        final String txt = ctx.txt;
        final int edg0 = ctx.edge0;
        final int end1 = ctx.end;
        HiMeepo meepo = ctx.meepo;
        if (meepo == null) { // 首次查找
            meepo = bornHiMeepo(ctx, edg0, end1);
            if (meepo == null) { // 不是米波模板
                logger.trace("[👹Parse:Parse:markHiMeepo] no meepo found");
                dealTxtPlain(ctx, end1, null); // 非米波模板
                ctx.edge1 = end1;
                return true;
            }
            else {
                logger.trace("[👹Parse:Parse:markHiMeepo] find first meepo at edge={}", meepo.edge);
            }
        }
        else {
            // 标记起点
            String head = meepo.head;
            int head0 = Seek.seekToken(txt, edg0, end1, head, meepo.echo);

            meepo = null;
            if (ctx.notBkb()) { // 米波-标记起点
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
                    dealTxtPlain(ctx, end1, DealText); // 无米波头
                    ctx.edge1 = end1;
                    logger.trace("[👹Parse:markHiMeepo] no meepo head found");
                    return true;
                }
            }
            else {
                logger.trace("[👹Parse:markHiMeepo] find other-meepo at edge={}", meepo.edge);
            }
        }

        //发现新米波
        int edge0 = meepo.edge.start;
        int edge1 = meepo.edge.until;

        logger.trace("[👹markHiMeepo] deal text at done1={}, edge0={}", ctx.done1, edge0);
        dealTxtPlain(ctx, edge0, meepo); // 米波前文字
        ctx.meepo = meepo;
        ctx.done1 = edge1;
        ctx.edge0 = edge0;
        ctx.edge1 = edge1;
        return true;
    }

    /**
     * 处理普通文本
     *
     * @param ctx   上下文
     * @param edge0 左边缘
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
            if (ctx.notBkb()) { // 处理文本
                // 应用指令
                ctx.procText(done1, edge0);
                // 增加指令
                ctx.procExon(exon);
                ctx.done1 = exon.edge.until;
            }
            else {
                if (ctx.endBkb(exon)) {
                    // BKB文本
                    TxtSimple txt = new TxtSimple(ctx.txt, ctx.newClop(done1, edge0));
                    ctx.procExon(txt);
                    // 增加指令
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
     * 查找`DNA:`或`RNA:`，并返回查找到的3个值
     *
     * @param ctx   上下文
     * @param token 特征
     * @return 是否找到
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
     * 处理DNA:RAW，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
     * 处理DNA:SON，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
     * 处理DNA:BKB，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            if (ctx.lax) {
                dna = SkipThis; // DNA:BKB
                logger.warn("[👹Parse:dealDnaBkb] skip bad DNA:BKB without name");
            }
            else {
                throw new IllegalStateException("DNA:BKB [" + ctx.main0 + "," + ctx.main1 + "], without name");
            }
        }
        return dna;
    }

    /**
     * 处理DNA:END，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    protected static Exon dealDnaEnd(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$DNA_END);
        if (tkn0 < 0) return null;

        final List<String> names = parseName(ctx, tkn0, TKN$DNA_END);

        final Exon dna;
        if (names.isEmpty()) {
            if (ctx.lax) {
                dna = SkipThis; // DNA:END
                logger.warn("[👹Parse:dealDnaEnd] skip bad DNA:END without name");
            }
            else {
                throw new IllegalStateException("DNA:END [" + ctx.main0 + "," + ctx.main1 + "], without name");
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
     * 处理DNA:SET，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            note = parseSplit(txt, pos3, ctx.main1, "DNA:SET");
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
            if (ctx.lax) {
                dna = SkipThis; // DNA:SET
                logger.warn("[👹Parse:dealDnaSet] skip bad DNA:SET {}", note);
            }
            else {
                throw new IllegalStateException("DNA:SET [" + ctx.main0 + "," + ctx.main1 + "], " + note);
            }
        }
        return dna;
    }

    /**
     * 处理RNA:RUN，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            note = parseSplit(txt, pos3, ctx.main1, "RNA:RUN");
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
            if (ctx.lax) {
                rna = SkipThis; // RNA:RUN
                logger.warn("[👹Parse:dealRnaRun] skip bad RNA:RUN {}", note);
            }
            else {
                throw new IllegalStateException("DNA:RUN [" + ctx.main0 + "," + ctx.main1 + "], " + note);
            }
        }
        return rna;
    }

    /**
     * 处理RNA:USE，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            note = parseSplit(txt, pos3, ctx.main1, "RNA:USE");
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
            if (ctx.lax) {
                rna = SkipThis; // RNA:USE
                logger.warn("[👹Parse:dealRnaUse] skip bad RNA:USE {}", note);
            }
            else {
                throw new IllegalStateException("RNA:USE [" + ctx.main0 + "," + ctx.main1 + "], " + note);
            }
        }
        return rna;
    }

    /**
     * 处理RNA:PUT，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            note = parseSplit(txt, pos3, ctx.main1, "RNA:PUT");
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
            if (ctx.lax) {
                rna = SkipThis; // RNA:PUT
                logger.warn("[👹Parse:dealRnaPut] skip bad RNA:PUT {}", note);
            }
            else {
                throw new IllegalStateException("RNA:PUT [" + ctx.main0 + "," + ctx.main1 + "], " + note);
            }
        }
        return rna;
    }

    /**
     * 处理RNA:WHEN，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            note = parseSplit(txt, pos3, ctx.main1, "RNA:WHEN");
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
            if (ctx.lax) {
                rna = SkipThis; // RNA:WHEN
                logger.warn("[👹Parse:dealRnaWhen] skip bad RNA:WHEN {}", note);
            }
            else {
                throw new IllegalStateException("RNA:WHEN [" + ctx.main0 + "," + ctx.main1 + "], " + note);
            }
        }
        return rna;
    }

    /**
     * 处理RNA:EACH，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            note = parseSplit(txt, pos3, ctx.main1, "RNA:EACH");
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
            if (ctx.lax) {
                rna = SkipThis; // RNA:EACH
                logger.warn("[👹Parse:dealRnaWhen] skip bad RNA:EACH {}", note);
            }
            else {
                throw new IllegalStateException("bad RNA:EACH [" + ctx.main0 + "," + ctx.main1 + "], " + note);
            }
        }
        return rna;
    }

    /**
     * 处理RNA:ELSE，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
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
            if (ctx.lax) {
                dna = SkipThis; // RNA:ELSE
                logger.warn("[👹Parse:dealRnaElse] skip bad RNA:ELSE without name");
            }
            else {
                throw new IllegalStateException("RNA:ELSE [" + ctx.main0 + "," + ctx.main1 + "], without name");
            }
        }
        return dna;
    }

    /**
     * 处理RNA:DONE，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    protected static Exon dealRnaDone(Ctx ctx) {
        final String txt = ctx.txt;
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN$RNA_DONE);
        if (tkn0 < 0) return null;

        final List<String> names = parseName(ctx, tkn0, TKN$RNA_DONE);

        final Exon dna;
        if (names.isEmpty()) {
            if (ctx.lax) {
                dna = SkipThis; // RNA:DONE
                logger.warn("[👹Parse:dealRnaDone] skip bad RNA:DONE without name");
            }
            else {
                throw new IllegalStateException("RNA:DONE [" + ctx.main0 + "," + ctx.main1 + "], without name");
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
            return null; // 不是边界
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
            return null; // 不是边界
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

    private static String parseSplit(String txt, int[] pos3, int main1, String xna) {
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
     * 命名约定：0 - 表示起点，包含；1 - 表示终点，不含；
     */
    protected static class Ctx {
        protected final String txt; // 模板原始文本
        protected final String pwd; // 模板路径文本
        protected final boolean lax; // 宽松模式
        protected final int end; // 文本的length（不含）
        protected int done1 = 0; // 已解析完毕的位置（不含）
        protected int edge0 = 0; // 待解析行块开始位置（包含）
        protected int main0 = -1; // 待解析指令开始位置（包含）
        protected int grpx1 = -1; // XNAGroup结束位置（不含）
        protected int main1 = -1; // 待解析指令结束位置（不含）
        protected int edge1 = -1; // 待解析行块结束位置（不含）
        protected HiMeepo meepo = null; // 当前作用的米波

        protected final ArrayDeque<G> tree = new ArrayDeque<>();

        protected final HashSet<String> bkbs = new HashSet<>();
        protected final ArrayList<Exon> proc = new ArrayList<>();
        protected final HashMap<String, Life> life = new HashMap<>();
        protected final TreeMap<Integer, Integer> line = new TreeMap<>();
        protected final StringBuilder errs = new StringBuilder();
        protected final RngChecker rngs = new RngChecker();

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

            // 调整gene栈 - 开始
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
                // 相同tock则替换，否则追加
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
            // 调整gene栈 - 结束
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
                        throw new IllegalStateException(err);
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
                    throw new IllegalStateException(errs.toString());
                }
            }
            if (tree.size() != 1) {
                StringBuilder sb = new StringBuilder("find UN-DONE RNA's Tock=");
                for (G g : tree) {
                    sb.append(g.tock);
                    sb.append(',');
                }
                throw new IllegalStateException(sb.toString());
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
