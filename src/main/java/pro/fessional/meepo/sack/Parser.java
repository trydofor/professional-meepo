package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.dna.DnaBkb;
import pro.fessional.meepo.bind.dna.DnaEnd;
import pro.fessional.meepo.bind.dna.DnaRaw;
import pro.fessional.meepo.bind.dna.DnaSet;
import pro.fessional.meepo.bind.rna.RnaPut;
import pro.fessional.meepo.bind.rna.RnaRun;
import pro.fessional.meepo.bind.rna.RnaUse;
import pro.fessional.meepo.bind.txt.HiMeepo;
import pro.fessional.meepo.bind.txt.TxtSimple;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Live;
import pro.fessional.meepo.poof.RnaManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static pro.fessional.meepo.bind.Const.TKN_DNA$;
import static pro.fessional.meepo.bind.Const.TKN_DNA$BKB;
import static pro.fessional.meepo.bind.Const.TKN_DNA$END;
import static pro.fessional.meepo.bind.Const.TKN_DNA$RAW;
import static pro.fessional.meepo.bind.Const.TKN_DNA$SET;
import static pro.fessional.meepo.bind.Const.TKN_HIMEEPO;
import static pro.fessional.meepo.bind.Const.TKN_RNA$;
import static pro.fessional.meepo.bind.Const.TKN_RNA$PUT;
import static pro.fessional.meepo.bind.Const.TKN_RNA$RUN;
import static pro.fessional.meepo.bind.Const.TKN_RNA$USE;

/**
 * static thread safe parser, if logger and you safe
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class Parser {

    protected static final Logger logger = LoggerFactory.getLogger(Parser.class);
    protected static final Exon SkipThis = new Exon("", new Clop(0, 0));
    protected static final Exon DealText = new Exon("", new Clop(0, 0));
    protected static final int RegxFlag = Pattern.UNIX_LINES | Pattern.MULTILINE;

    /**
     * 命名约定：0 - 表示起点，包含；1 - 表示终点，不含；
     */
    protected static class Ctx {
        protected final String txt; // 模板原始文本
        protected final int end; // 文本的length（不含）
        protected int done1 = 0; // 已解析完毕的位置（不含）
        protected int edge0 = 0; // 待解析行块开始位置（包含）
        protected int main0 = -1; // 待解析指令开始位置（包含）
        protected int grpx1 = -1; // XNAGroup结束位置（不含）
        protected int main1 = -1; // 待解析指令结束位置（不含）
        protected int edge1 = -1; // 待解析行块结束位置（不含）
        protected HiMeepo meepo = null; // 当前作用的米波

        protected final ArrayList<Exon> gene = new ArrayList<>();
        protected final HashSet<String> bkb = new HashSet<>();
        protected final ArrayList<Exon> proc = new ArrayList<>();
        protected final Map<String, Life> life = new HashMap<>();

        public Ctx(String txt) {
            this.txt = txt;
            this.end = txt.length();
        }

        public Clop toEdge() {
            return new Clop(edge0, edge1);
        }

        public Clop toMain() {
            return new Clop(main0, main1);
        }

        public boolean notBkb() {
            return bkb.isEmpty();
        }

        public boolean endBkb(Exon exon) {
            if (exon instanceof DnaEnd) {
                bkb.removeAll(((DnaEnd) exon).name);
            }
            return bkb.isEmpty();
        }

        public void addExon(Exon exon) {
            if (exon instanceof DnaBkb) {
                DnaBkb dna = (DnaBkb) exon;
                bkb.add(dna.name);
            } else if (exon instanceof DnaEnd) {
                Set<String> name = ((DnaEnd) exon).name;
                for (String k : name) {
                    Life lf = life.remove(k);
                    if (lf != null) lf.close();
                }
            }

            if (exon instanceof Live) {
                Life lf = ((Live) exon).life;
                String nm = lf.name;
                if (nm.length() > 0) {
                    this.life.put(nm, lf);
                }
            }

            gene.add(exon);

            if (exon instanceof DnaSet ||
                    exon instanceof RnaUse ||
                    exon instanceof RnaRun
            ) {
                proc.add(exon);
            }
        }

        public void procText(final int done1, final int edge1) {
            if (done1 >= edge1) return;

            if (proc.isEmpty()) {
                TxtSimple txt = new TxtSimple(this.txt, done1, edge1);
                gene.add(txt);
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
                TxtSimple txt = new TxtSimple(this.txt, done1, edge1);
                gene.add(txt);
                return;
            }

            for (Exon.N n1 : rst) {
                for (Exon.N n2 : rst) {
                    if (n1 != n2 && n1.pos.cross(n2.pos)) {
                        throw new IllegalStateException("cross range, n1=" + n1.pos + ", n2=" + n2.pos + ", text=" + text);
                    }
                }
            }

            Collections.sort(rst);
            int off = 0;
            for (Exon.N n : rst) {
                Clop pos = n.pos;
                if (off < pos.start) {
                    gene.add(new TxtSimple(txt, off + done1, pos.start + done1));
                }
                n.xna.apply(gene, pos.shift(done1), txt);
                off = pos.until;
            }
            int len = text.length();
            if (off < len) {
                gene.add(new TxtSimple(txt, off + done1, len + done1));
            }
        }
    }

    public static Gene parse(String txt) {
        if (txt == null) return null;
        final Ctx ctx = new Ctx(txt);

        for (ctx.edge0 = 0; ctx.edge0 < ctx.end; ctx.edge0 = ctx.edge1) {

            // 标记米波，文本分段，找到edge0
            if (markHiMeepo(ctx)) {
                continue;
            }

            Exon exon = SkipThis;
            // 查找`DNA:`
            if (findXnaGrp(ctx, TKN_DNA$)) {
                exon = dealDnaGroup(ctx);
            }

            // 查找`RNA:`
            else if (findXnaGrp(ctx, TKN_RNA$)) {
                exon = dealRnaGroup(ctx);
            }

            dealTxtPlain(ctx, ctx.edge0, exon); // 循环处理
        }

        dealTxtPlain(ctx, ctx.end, DealText); // 处理最后

        return new Gene(ctx.gene, txt);
    }


    /**
     * 处理 DNA 组
     *
     * @param ctx 上下文
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
        }
        return exon == null ? SkipThis : exon;  // DNA:GROUP
    }

    /**
     * 处理 RNA 组
     *
     * @param ctx 上下文
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
            meepo = bornHiMeepo(txt, edg0, end1);
            if (meepo == null) { // 不是米波模板
                logger.trace("[👹markHiMeepo] no meepo found");
                dealTxtPlain(ctx, end1, null); // 非米波模板
                ctx.edge1 = end1;
                return true;
            } else {
                logger.trace("[👹markHiMeepo] find first meepo at edge0={}", meepo.edge.start);
            }
        } else {
            // 标记起点
            String head = meepo.head;
            int head0 = Seek.seekToken(txt, edg0, end1, head, meepo.echo);

            meepo = null;
            if (ctx.notBkb()) { // 米波-标记起点
                meepo = bornHiMeepo(txt, edg0, head0 > edg0 ? head0 : end1);
            } else {
                logger.trace("[👹markHiMeepo] skip born-meepo in bkb");
            }

            if (meepo == null) {
                if (head0 >= edg0) {
                    ctx.edge0 = head0;
                    ctx.edge1 = head0 + head.length();
                    logger.trace("[👹markHiMeepo] find meepo head at edge0={}", head0);
                    return false;
                } else {
                    dealTxtPlain(ctx, end1, DealText); // 无米波头
                    ctx.edge1 = end1;
                    logger.trace("[👹markHiMeepo] no meepo head found");
                    return true;
                }
            } else {
                logger.trace("[👹markHiMeepo] find other-meepo at edge0={}", meepo.edge.start);
            }
        }

        //发现新米波
        int edge0 = meepo.edge.start;
        int edge1 = meepo.edge.until;

        logger.trace("[markHiMeepo] deal text at done1={}, edge0={}", ctx.done1, edge0);
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
     * @param ctx 上下文
     */
    protected static void dealTxtPlain(Ctx ctx, int edge0, Exon exon) {
        final int done1 = ctx.done1;

        if (done1 >= edge0) {
            if (exon != null && exon != SkipThis && exon != DealText) { // append gene if done
                ctx.addExon(exon);
                ctx.done1 = exon.edge.until;
            }
            return;
        }

        if (exon == null) {
            logger.trace("deal whole text at done1={}, edge0={}", done1, edge0);
            TxtSimple dna = new TxtSimple(ctx.txt, done1, edge0);
            ctx.addExon(dna);
            ctx.done1 = edge0;
        } else if (exon == SkipThis) {  // skip for next
            logger.trace("skip for next at done1={}, edge0={}", done1, edge0);
        } else if (exon == DealText) {
            ctx.procText(done1, edge0);
            ctx.done1 = edge0;
            logger.trace("deal next at done1={}, edge0={}", done1, edge0);
        } else {
            if (ctx.notBkb()) { // 处理文本
                // 应用指令
                ctx.procText(done1, edge0);
                // 增加指令
                ctx.addExon(exon);
                ctx.done1 = exon.edge.until;
            } else {
                if (ctx.endBkb(exon)) {
                    // BKB文本
                    TxtSimple txt = new TxtSimple(ctx.txt, done1, edge0);
                    ctx.addExon(txt);
                    // 增加指令
                    ctx.addExon(exon);
                    ctx.done1 = exon.edge.until;
                } else {
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
        if (main0 < 0) {
            logger.trace("[👹findXnaGrp] skip {} un-follow meepo-head", token);
            return false;
        }

        int main1 = Seek.seekToken(text, main0, ctx.end, meepo.tail, false);
        int edge1;
        if (main1 < main0) {
            if (meepo.crlf) {
                main1 = ctx.end;
                edge1 = main1;
                logger.trace("[👹findXnaGrp] use the end as meepo-tail when CRLF");
            } else {
                logger.trace("[👹findXnaGrp] skip xna group without meepo-tail");
                return false;
            }
        } else {
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
        logger.trace("[👹findXnaGrp] find {} at main0={}", token, main0);
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
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$RAW);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN_DNA$RAW.length();
        int raw0 = Seek.seekNextGrace(txt, off, ctx.main1);
        DnaRaw dna = new DnaRaw(txt, ctx.toEdge(), ctx.toMain(), Math.max(raw0, off));
        logger.trace("[👹dealDnaRaw] find DNA:RAW at token0={}", tkn0);
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
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$BKB);
        if (tkn0 < 0) return null;

        int[] pos = Seek.seekNextWords(txt, tkn0 + TKN_DNA$BKB.length(), ctx.main1);
        final Exon dna;
        if (pos[1] > pos[0]) {
            trimEdge(ctx); // DNA:BKB
            String name = txt.substring(pos[0], pos[1]);
            dna = new DnaBkb(txt, ctx.toEdge(), ctx.toMain(), name);
            logger.trace("[👹dealDnaBkb] find DNA:BKB at token0={}", tkn0);
        } else {
            dna = SkipThis; // DNA:BKB
            logger.trace("[👹dealDnaBkb] skip bad DNA:BKB without name");
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
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$END);
        if (tkn0 < 0) return null;

        final List<String> names = new ArrayList<>(8);
        final String head = ctx.meepo.head;
        int off = tkn0 + TKN_DNA$END.length();
        while (true) {
            int[] name = Seek.seekNextWords(txt, off, ctx.main1);
            if (name[1] > name[0]) {
                String nm = txt.substring(name[0], name[1]);
                if (nm.contains(head)) {
                    break;
                } else {
                    names.add(nm);
                    off = name[1];
                }
            } else {
                break;
            }
        }

        final Exon dna;
        if (names.isEmpty()) {
            dna = SkipThis; // DNA:END
            logger.trace("[👹dealDnaEnd] skip bad DNA:END without name");
        } else {
            trimEdge(ctx); // DNA:END
            dna = new DnaEnd(txt, ctx.toEdge(), ctx.toMain(), names);
            logger.trace("[👹dealDnaEnd] find DNA:END at token0={}", tkn0);
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
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$SET);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN_DNA$SET.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax DNA:SET, without split char";
        } else {
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1, "DNA:SET");
        }

        final Exon dna;
        if (note == null) {
            trimEdge(ctx); // DNA:SET
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]), RegxFlag);
            String repl = txt.substring(pos3[1] + 1, pos3[2]);

            Life life = parseLife(txt, pos3[2], ctx.main1);
            dna = new DnaSet(txt, life, ctx.toEdge(), ctx.toMain(), find, repl);
            logger.trace("[👹dealDnaSet] find DNA:SET at token0={}", tkn0);
        } else {
            dna = SkipThis; // DNA:SET
            logger.trace("[👹dealDnaSet] skip bad DNA:SET {}", note);
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
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_RNA$RUN);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN_RNA$RUN.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:RUN, without split char";
        } else {
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
            rna = new RnaRun(txt, life, ctx.toEdge(), ctx.toMain(), type, find, expr, mute);
            logger.trace("[👹dealRnaRun] find RNA:RUN at token0={}", tkn0);
        } else {
            rna = SkipThis; // RNA:RUN
            logger.trace("[👹dealRnaRun] skip bad RNA:RUN {}", note);
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
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_RNA$USE);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN_RNA$USE.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:USE, without split char";
        } else {
            pos3 = new int[]{spl0, -1, -1};
            note = parseSplit(txt, pos3, ctx.main1, "RNA:USE");
        }

        final Exon rna;
        if (note == null) {
            trimEdge(ctx); // RNA:USE
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]), RegxFlag);
            String para = txt.substring(pos3[1] + 1, pos3[2]);
            Life life = parseLife(txt, pos3[2], ctx.main1);
            rna = new RnaUse(txt, life, ctx.toEdge(), ctx.toMain(), find, para);
            logger.trace("[👹dealRnaUse] find RNA:USE at token0={}", tkn0);
        } else {
            rna = SkipThis; // RNA:USE
            logger.trace("[👹dealRnaUse] skip bad RNA:USE {}", note);
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
        final int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_RNA$PUT);
        if (tkn0 < 0) return null;

        int off = tkn0 + TKN_RNA$PUT.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String note;
        if (spl0 < 0) {
            note = "Bad-Syntax RNA:PUT, without split char";
        } else {
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
            rna = new RnaPut(txt, ctx.toEdge(), ctx.toMain(), type, para, expr, mute);
            logger.trace("[👹dealRnaPut] find RNA:PUT at token0={}", tkn0);
        } else {
            rna = SkipThis; // RNA:PUT
            logger.trace("[👹dealRnaPut] skip bad RNA:PUT {}", note);
        }
        return rna;
    }

    // ////////////

    private static HiMeepo bornHiMeepo(String txt, int edge0, int edge1) {
        int tkn0 = Seek.seekToken(txt, edge0, edge1, TKN_HIMEEPO, false);
        if (tkn0 <= 0) return null;

        int[] hd = Seek.seekPrevWords(txt, edge0, tkn0);
        int hd0 = Seek.seekPrevEdge(txt, hd[0]);
        if (hd[0] == hd[1] || hd0 < 0) {
            logger.trace("[👹bornHiMeepo] skip HI-MEEPO without prefix");
            return null; // 不是边界
        }
        String head = txt.substring(hd[0], hd[1]);
        int tk1 = tkn0 + TKN_HIMEEPO.length();
        boolean trim = true;
        if (tk1 < edge1 && txt.charAt(tk1) == '!') {
            tk1++;
            trim = false;
        }
        int[] tl = Seek.seekNextWords(txt, tk1, edge1);
        int tl1 = Seek.seekNextEdge(txt, tl[1]);
        if (tl1 < 0) {
            logger.trace("[👹bornHiMeepo] skip HI-MEEPO without suffix");
            return null; // 不是边界
        }

        String tail;
        if (tl[1] > tl[0]) {
            tail = txt.substring(tl[0], tl[1]);
        } else {
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
        return new HiMeepo(txt, new Clop(hd[0], tl[1]), new Clop(tkn0, tk1), head, tail, trim);
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
        } else {
            return txt.charAt(typ2[1]) == '!';
        }
    }

    @NotNull
    private static String parseType(String txt, int[] typ2) {
        return typ2[1] <= typ2[0] ? RnaManager.getDefaultEngine().type()[0] : txt.substring(typ2[0], typ2[1]);
    }

    @NotNull
    private static Life parseLife(String txt, int spl2, int main1) {
        int lf0 = spl2 + 1;
        int[] pw = Seek.seekNextWords(txt, lf0, main1);
        Life life;
        if (pw[0] == lf0 && pw[1] > pw[0]) {
            life = Life.parse(txt.substring(pw[0], pw[1]));
        } else {
            life = Life.nobodyOne();
        }
        return life;
    }

    private static String parseSplit(String txt, int[] pos3, int main1, String xna) {
        char splt = txt.charAt(pos3[0]);
        for (int i = 1; i < pos3.length; i++) {
            int o1 = pos3[i - 1] + 1;
            int p = txt.indexOf(splt, o1);
            if (p < o1 || p >= main1) {
                return "Bad-Syntax " + xna + ", need 3 parts";
            }
            pos3[i] = p;
        }
        return null;
    }
}
