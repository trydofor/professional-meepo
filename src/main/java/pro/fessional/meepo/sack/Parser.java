package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;
import pro.fessional.meepo.bind.dna.DnaBkb;
import pro.fessional.meepo.bind.dna.DnaEnd;
import pro.fessional.meepo.bind.dna.DnaRaw;
import pro.fessional.meepo.bind.dna.DnaSet;
import pro.fessional.meepo.bind.rna.RnaPut;
import pro.fessional.meepo.bind.rna.RnaRun;
import pro.fessional.meepo.bind.rna.RnaUse;
import pro.fessional.meepo.bind.txt.HiMeepo;
import pro.fessional.meepo.bind.txt.TxtPlain;
import pro.fessional.meepo.poof.RnaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
 * static thread safe parser
 * 0 - 表示起点，包含；
 * 1 - 表示终点，不含；
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class Parser {

    public static class Ctx {
        protected final String txt;
        protected final int end;
        protected final ArrayList<Exon> gene = new ArrayList<>();
        protected final ArrayList<Exon> proc = new ArrayList<>();
        protected final Map<String, Life> life = new HashMap<>();

        protected int done1 = 0; // 已解析完毕的位置（不含）
        protected int edge0 = 0; // 待解析行块开始位置（包含）
        protected int main0 = -1; // 待解析指令开始位置（包含）
        protected int grpx1 = -1; // XNAGroup结束位置（不含）
        protected int main1 = -1; // 待解析指令结束位置（不含）
        protected int edge1 = -1; // 待解析行块结束位置（不含）

        protected HiMeepo meepo = null;
        protected HashSet<String> bkb = new HashSet<>();

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
    }

    public static Gene parse(String txt) {
        if (txt == null) return null;
        final Ctx ctx = new Ctx(txt);

        for (ctx.edge0 = 0; ctx.edge0 < ctx.end; ctx.edge0 = ctx.edge1) {
            // 处理文本，done1 - edge0 之间
            dealTxtPlain(ctx);

            // 标记米波，文本分段，找到edge0
            if (markHiMeepo(ctx)) {
                continue;
            }

            // 查找`DNA:`
            if (findXnaGrp(ctx, TKN_DNA$)) {
                dealDnaGroup(ctx);
            }

            // 查找`RNA:`
            else if (findXnaGrp(ctx, TKN_RNA$)) {
                dealRnaGroup(ctx);
            }
        }

        return null;
    }

    /**
     * 查找并处理meepo，是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理
     */
    public static boolean markHiMeepo(Ctx ctx) {
        final String txt = ctx.txt;
        HiMeepo meepo = ctx.meepo;
        if (meepo == null) { // 首次查找
            meepo = bornHiMeepo(txt, ctx.edge0, ctx.end);
            if (meepo == null) { // 不是米波模板
                ctx.gene.add(new TxtPlain(txt, 0, ctx.end));
                ctx.edge1 = ctx.end;
                return true;
            }
        } else {
            // 标记起点
            int edge0 = Seek.seekToken(txt, ctx.edge0, ctx.end, meepo.head, meepo.echo);
            meepo = bornHiMeepo(txt, ctx.edge0, edge0);
            if (meepo == null) {
                dealTxtPlain(ctx);
                ctx.done1 = edge0;
                ctx.edge0 = edge0;
                return false;
            }
        }

        //发现新标记
        int edge0 = meepo.edge.start;
        int edge1 = meepo.edge.until;

        dealTxtPlain(ctx);

        ctx.gene.add(meepo);
        ctx.meepo = meepo;
        ctx.done1 = edge1;
        ctx.edge0 = edge0;
        ctx.edge1 = edge1;
        return true;
    }

    public static HiMeepo bornHiMeepo(String txt, int edge0, int edge1) {
        int tkn0 = Seek.seekToken(txt, edge0, edge1, TKN_HIMEEPO, false);
        if (tkn0 <= 0) return null;

        int[] hd = Seek.seekPrevWords(txt, edge0, tkn0);
        if (hd[0] == hd[1] || !Seek.isPrevEdge(txt, hd[0] - 1)) {
            return null; // 不是边界
        }
        String head = txt.substring(hd[0], hd[1]);
        int tk1 = tkn0 + TKN_HIMEEPO.length();
        int[] tl = Seek.seekNextWords(txt, tk1, edge1);
        if (!Seek.isNextEdge(txt, tl[1])) {
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
        return new HiMeepo(txt, new Clop(hd[0], tl[1]), new Clop(tkn0, tk1), head, tail);
    }

    /**
     * 处理普通文本
     *
     * @param ctx 上下文
     */
    public static void dealTxtPlain(Ctx ctx) {
        if (ctx.done1 >= ctx.edge1) return;

        if (ctx.done1 < ctx.edge0) {
            ctx.gene.add(new TxtPlain(ctx.txt, ctx.done1, ctx.edge0));
        }
        for (Iterator<Exon> it = ctx.proc.iterator(); it.hasNext(); ) {
            Exon en = it.next();
            Life.State st = en.life.state();
            if (st == Life.State.Dead) {
                it.remove();
            } else {
                // TODO dealTxtPlain(ctx);
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
    public static boolean findXnaGrp(Ctx ctx, String token) {
        HiMeepo meepo = ctx.meepo;
        int off = ctx.edge0 + meepo.head.length();
        int main0 = Seek.seekFollow(ctx.txt, off, ctx.end, token);
        if (main0 < 0) {
            return false;
        }
        int main1 = Seek.seekToken(ctx.txt, main0, ctx.end, meepo.tail, false);
        int edge1 = main1 + meepo.tail.length();
        if (main1 < 0) {
            if (meepo.islf) {
                main1 = ctx.end;
                edge1 = main1;
            } else {
                return false;
            }
        }
        ctx.main0 = main0;
        ctx.grpx1 = main0 + token.length();
        ctx.main1 = main1;
        ctx.edge1 = edge1;

        return true;
    }

    /**
     * 处理 DNA 组
     *
     * @param ctx 上下文
     */
    public static void dealDnaGroup(Ctx ctx) {
        if (dealDnaEnd(ctx)) return;
        if (dealDnaBkb(ctx)) return;
        if (dealDnaSet(ctx)) return;
        dealDnaRaw(ctx);
    }

    /**
     * 处理DNA:RAW，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    public static boolean dealDnaRaw(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$RAW);
        if (tkn0 < 0) return false;

        int raw0 = tkn0 + TKN_DNA$RAW.length();
        DnaRaw dna = new DnaRaw(txt, ctx.toEdge(), ctx.toMain(), raw0);
        ctx.gene.add(dna);
        ctx.done1 = ctx.edge1;

        return true;
    }

    /**
     * 处理DNA:BKB，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    public static boolean dealDnaBkb(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$BKB);
        if (tkn0 < 0) return false;

        int[] pos = Seek.seekNextWords(txt, tkn0 + TKN_DNA$BKB.length(), ctx.main1);
        if (pos[1] <= pos[0]) {
            TxtPlain dna = new TxtPlain(txt, ctx.edge0, ctx.edge1, "Bad-Syntax DNA:BKB, without name");
            ctx.gene.add(dna);
        } else {
            String name = txt.substring(pos[0], pos[1]);
            DnaBkb dna = new DnaBkb(txt, ctx.toEdge(), ctx.toMain(), name);

            ctx.gene.add(dna);
            ctx.bkb.add(name);
        }
        ctx.done1 = ctx.edge1;
        return true;
    }

    /**
     * 处理DNA:END，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    public static boolean dealDnaEnd(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$END);
        if (tkn0 < 0) return false;

        List<String> names = new ArrayList<>(8);
        String head = ctx.meepo.head;
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
        if (names.isEmpty()) {
            TxtPlain dna = new TxtPlain(txt, ctx.edge0, ctx.edge1, "Bad-Syntax DNA:END, without any name");
            ctx.gene.add(dna);
        } else {
            for (String name : names) {
                ctx.bkb.remove(name);
                Life life = ctx.life.remove(name);
                if (life != null) {
                    life.close();
                }
            }
            DnaEnd dna = new DnaEnd(txt, ctx.toEdge(), ctx.toMain(), names);
            ctx.gene.add(dna);
        }
        ctx.done1 = ctx.edge1;
        return true;
    }

    /**
     * 处理DNA:SET，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    public static boolean dealDnaSet(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_DNA$SET);
        if (tkn0 < 0) return false;

        int off = tkn0 + TKN_DNA$SET.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String error;
        if (spl0 < 0) {
            error = "Bad-Syntax DNA:SET, without split char";
        } else {
            pos3 = new int[]{spl0, -1, -1};
            error = parseSplit(txt, pos3, ctx.main1, "DNA:SET");
        }

        if (error == null) {
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]));
            String repl = txt.substring(pos3[1] + 1, pos3[2]);

            Life life = parseLife(txt, pos3[2], ctx.main1);
            DnaSet dna = new DnaSet(txt, life, ctx.toEdge(), ctx.toMain(), find, repl);

            ctx.gene.add(dna);
        } else {
            TxtPlain dna = new TxtPlain(txt, ctx.edge0, ctx.edge1, error);
            ctx.gene.add(dna);
        }
        ctx.done1 = ctx.edge1;
        return true;
    }

    /**
     * 处理 RNA 组
     *
     * @param ctx 上下文
     */
    public static void dealRnaGroup(Ctx ctx) {
        if (dealRnaPut(ctx)) return;
        if (dealRnaUse(ctx)) return;
        dealRnaRun(ctx);
    }

    /**
     * 处理RNA:RUN，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    public static boolean dealRnaRun(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_RNA$RUN);
        if (tkn0 < 0) return false;

        int off = tkn0 + TKN_RNA$RUN.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String error;
        if (spl0 < 0) {
            error = "Bad-Syntax RNA:RUN, without split char";
        } else {
            int[] typ2 = Seek.seekNextAlnum(txt, off, spl0);
            mute = parseMute(txt, typ2);
            type = parseType(txt, typ2);
            pos3 = new int[]{spl0, -1, -1};
            error = parseSplit(txt, pos3, ctx.main1, "RNA:RUN");
        }

        if (error == null) {
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]));
            String expr = txt.substring(pos3[1] + 1, pos3[2]);

            Life life = parseLife(txt, pos3[2], ctx.main1);
            RnaRun rna = new RnaRun(txt, life, ctx.toEdge(), ctx.toMain(), type, find, expr, mute);
            ctx.gene.add(rna);
        } else {
            TxtPlain dna = new TxtPlain(txt, ctx.edge0, ctx.edge1, error);
            ctx.gene.add(dna);
        }
        ctx.done1 = ctx.edge1;
        return true;
    }

    /**
     * 处理RNA:USE，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    public static boolean dealRnaUse(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_RNA$USE);
        if (tkn0 < 0) return false;

        int off = tkn0 + TKN_RNA$USE.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String error;
        if (spl0 < 0) {
            error = "Bad-Syntax RNA:USE, without split char";
        } else {
            pos3 = new int[]{spl0, -1, -1};
            error = parseSplit(txt, pos3, ctx.main1, "RNA:USE");
        }

        if (error == null) {
            Pattern find = Pattern.compile(txt.substring(pos3[0] + 1, pos3[1]));
            String para = txt.substring(pos3[1] + 1, pos3[2]);

            Life life = parseLife(txt, pos3[2], ctx.main1);
            RnaUse dna = new RnaUse(txt, life, ctx.toEdge(), ctx.toMain(), find, para);

            ctx.gene.add(dna);
        } else {
            TxtPlain dna = new TxtPlain(txt, ctx.edge0, ctx.edge1, error);
            ctx.gene.add(dna);
        }
        ctx.done1 = ctx.edge1;
        return true;
    }

    /**
     * 处理RNA:PUT，并决定是否停止后续处理
     *
     * @param ctx 上下文
     * @return 是否停止后续处理。
     */
    public static boolean dealRnaPut(Ctx ctx) {
        String txt = ctx.txt;
        int tkn0 = Seek.seekFollow(txt, ctx.grpx1, ctx.main1, TKN_RNA$PUT);
        if (tkn0 < 0) return false;

        int off = tkn0 + TKN_RNA$PUT.length();
        int spl0 = Seek.seekNextSplit(txt, off, ctx.main1);
        int[] pos3 = null;
        String type = null;
        boolean mute = false;
        String error;
        if (spl0 < 0) {
            error = "Bad-Syntax RNA:PUT, without split char";
        } else {
            int[] typ2 = Seek.seekNextAlnum(txt, off, spl0);
            mute = parseMute(txt, typ2);
            type = parseType(txt, typ2);
            pos3 = new int[]{spl0, -1, -1};
            error = parseSplit(txt, pos3, ctx.main1, "RNA:PUT");
        }

        if (error == null) {
            String para = txt.substring(pos3[0] + 1, pos3[1]);
            String expr = txt.substring(pos3[1] + 1, pos3[2]);

            RnaPut dna = new RnaPut(txt, ctx.toEdge(), ctx.toMain(), type, para, expr, mute);
            ctx.gene.add(dna);
        } else {
            TxtPlain dna = new TxtPlain(txt, ctx.edge0, ctx.edge1, error);
            ctx.gene.add(dna);
        }
        ctx.done1 = ctx.edge1;
        return true;
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
