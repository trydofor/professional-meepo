package pro.fessional.meepo.sack;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.txt.TxtRnaUse;
import pro.fessional.meepo.bind.txt.TxtSimple;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RngChecker;
import pro.fessional.meepo.util.Seek;

import java.util.ArrayList;

import static java.util.Collections.emptySet;
import static pro.fessional.meepo.bind.Const.TKN$VAR_ESC;
import static pro.fessional.meepo.bind.Const.TKN$VAR_PRE;
import static pro.fessional.meepo.bind.Const.TKN$VAR_SUF;

/**
 * place holder parser thead safe
 *
 * @author trydofor
 * @since 2021-01-03
 */
public class Holder {

    protected static final Logger logger = LoggerFactory.getLogger(Holder.class);

    /**
     * 解析占位符模板，严谨模式。
     *
     * @param txt 文本
     * @return 基因
     */
    @Contract("null->null;!null->!null")
    public static Gene parse(String txt) {
        return parse(false, txt, TKN$VAR_PRE, TKN$VAR_SUF, TKN$VAR_ESC);
    }

    /**
     * 解析占位符模板。
     *
     * @param lax 放松模式
     * @param txt 文本
     * @return 基因
     */
    @Contract("_,null->null;_,!null->!null")
    public static Gene parse(boolean lax, String txt) {
        return parse(lax, txt, TKN$VAR_PRE, TKN$VAR_SUF, TKN$VAR_ESC);
    }

    /**
     * 解析占位符模板，严谨模式。
     *
     * @param txt 文本
     * @param pre 前界定符
     * @param suf 后界定符
     * @return 基因
     */
    @Contract("null,_,_->null;!null,_,_->!null")
    public static Gene parse(String txt, String pre, String suf) {
        return parse(false, txt, pre, suf, TKN$VAR_ESC);
    }

    /**
     * 解析占位符模板。
     *
     * @param lax 放松模式
     * @param txt 文本
     * @param pre 前界定符
     * @param suf 后界定符
     * @return 基因
     */
    @Contract("_,null,_,_->null;_,!null,_,_->!null")
    public static Gene parse(boolean lax, String txt, String pre, String suf) {
        return parse(lax, txt, pre, suf, TKN$VAR_ESC);
    }

    /**
     * 解析占位符模板，严谨模式。
     *
     * @param txt 文本
     * @param pre 前界定符
     * @param suf 后界定符
     * @param esc 前转义符
     * @return 基因
     */
    @Contract("null,_,_,_->null;!null,_,_,_->!null")
    public static Gene parse(String txt, String pre, String suf, String esc) {
        return parse(false, txt, pre, suf, esc);
    }

    /**
     * 解析占位符模板。
     *
     * @param lax 放松模式
     * @param txt 文本
     * @param pre 前界定符
     * @param suf 后界定符
     * @param esc 前转义符
     * @return 基因
     */
    @Contract("_,null,_,_,_->null;_,!null,_,_,_->!null")
    public static Gene parse(boolean lax, String txt, String pre, String suf, String esc) {
        if (txt == null) return null;

        // 空或异常
        final int pln = pre.length(), sln = suf.length(), len = txt.length();
        final ArrayList<Exon> exon = new ArrayList<>();
        if (pln == 0 || sln == 0) {
            logger.warn("[👹Holder:parse] prefix or suffix is empty, return plain text");
            addText(exon, txt, 0, len);
            return new Gene(exon, emptySet());
        }

        //转义和标记
        final StringBuilder buff = new StringBuilder();
        final ArrayList<int[]> edge = new ArrayList<>();
        int off = 0, ipe, hze = 0;
        while (off < len) {
            if (txt.regionMatches(off, pre, 0, pln)) {
                logger.trace("[👹Holder:parse:origin] find prefix at {}", off);
                ipe = 0;
            }
            else if (txt.regionMatches(off, suf, 0, sln)) {
                logger.trace("[👹Holder:parse:origin] find suffix at {}", off);
                ipe = 1;
            }
            else {
                buff.append(txt.charAt(off));
                off++;
                continue;
            }

            // 清理相连转义符
            int ec = Seek.countPreToken(txt, off, esc);
            if (ec > 0) {
                hze++;
                logger.trace("[👹Holder:parse:origin] find escape at {}, ec={}", off, ec);
                int sl = buff.length();
                buff.delete(sl - ((ec + 1) / 2), sl);
            }

            if (ec % 2 == 0) {
                edge.add(new int[]{buff.length(), ipe, off});
            }

            if (ipe == 0) {
                buff.append(pre);
                off += pln;
            }
            else {
                buff.append(suf);
                off += sln;
            }
        }

        String rst = buff.toString();
        buff.setLength(0);
        if (edge.isEmpty()) {
            logger.trace("[👹Holder:parse:escape] return no-place-holder text and {} escaped", hze);
            addText(exon, rst, 0, rst.length());
            return new Gene(exon, emptySet());
        }

        //解析
        final RngChecker rngs = new RngChecker();
        int[] es = null;
        off = 0;
        for (int[] ed : edge) {
            if (es == null) {
                if (ed[1] == 0) {
                    addText(exon, rst, 0, ed[0]);
                    off = ed[0];
                    es = ed;
                }
                else {
                    if (lax) {
                        logger.warn("[👹Holder:parse:escape] drop unmatched suffix at origin text pos=" + ed[2]);
                    }
                    else {
                        throw new IllegalStateException("find unmatched suffix at origin text pos=" + ed[2]);
                    }
                }
            }
            else if (es[1] == 0) { // 前
                if (ed[1] == 1) {
                    int p1s = es[0] + pln;
                    int p2e = ed[0] + sln;
                    Clop clop = new Clop(es[0], p2e, p1s, ed[0]);
                    String expr = rst.substring(p1s, ed[0]);
                    TxtRnaUse use = new TxtRnaUse(rst, clop, expr, 0);
                    exon.add(use);
                    use.check(buff, rngs);
                    off = p2e;
                    es = ed;
                }
                else {
                    if (lax) {
                        logger.warn("[👹Holder:parse:escape] drop nested prefix at origin text pos=" + ed[2]);
                    }
                    else {
                        throw new IllegalStateException("find nested prefix at origin text pos=" + ed[2]);
                    }
                }
            }
            else { // 后
                if (ed[1] == 0) {
                    addText(exon, rst, es[0] + sln, ed[0]);
                    off = ed[0];
                    es = ed;
                }
                else {
                    if (lax) {
                        logger.warn("[👹Holder:parse:escape] drop nested suffix at origin text pos=" + ed[2]);
                    }
                    else {
                        throw new IllegalStateException("find nested suffix at origin text pos=" + ed[2]);
                    }
                }
            }
        }

        if (buff.length() > 0) {
            if (lax) {
                logger.warn("[👹Holder:parse:escape]" + buff);
            }
            else {
                throw new IllegalStateException(buff.toString());
            }
        }

        addText(exon, rst, off, rst.length());
        return new Gene(exon, rngs.getCheckedEngine());
    }

    private static void addText(ArrayList<Exon> exon, String txt, int s, int e) {
        if (e > s) {
            Clop clop = new Clop(s, e, s, e);
            exon.add(new TxtSimple(txt, clop));
        }
    }
}
