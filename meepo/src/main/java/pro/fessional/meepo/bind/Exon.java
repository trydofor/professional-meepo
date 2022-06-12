package pro.fessional.meepo.bind;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.poof.RngChecker;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 * ` &lt;% DNA:RAW 空白处都是一个空格 %&gt; \n`
 * edge=`&lt;% DNA:RAW 空白处都是一个空格 %&gt;`
 * main=`DNA:RAW 空白处都是一个空格`
 * xxxx9 为xxxx的char[]形式，一般为内部使用
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class Exon {

    /**
     * 原始模板的边缘区
     */
    @NotNull
    public final Clop edge;

    /**
     * 边缘内字符串缓存
     */

    protected final char @NotNull [] text9;

    protected Exon(char @NotNull [] text9, @NotNull Clop edge) {
        this.edge = edge;
        this.text9 = text9;
    }

    public Exon(@NotNull String text, @NotNull Clop edge) {
        this(Dent.chars(text, edge), edge);
    }

    /**
     * 匹配其他文本，并根据匹配状态，设置匹配区间
     *
     * @param lst 匹配区间
     * @param txt 文本
     * @return 本次匹配状态
     */
    public Life.State match(List<N> lst, String txt) {
        return Life.State.Skip;
    }

    /**
     * 应用匹配的文本
     *
     * @param pos 匹配
     * @param txt 文本
     * @param bar 左距
     * @return 基因
     */
    @NotNull
    public List<Exon> apply(Clop pos, String txt, int bar) {
        return Collections.emptyList();
    }

    /**
     * 重建回解析前的模板，默认edge
     *
     * @param buff buff
     */
    public void build(Writer buff) {
        Dent.write(buff, text9);
    }

    /**
     * 合并模板输出结果，默认edge
     *
     * @param acid 执行环境
     * @param buff 输出buff
     */
    public void merge(Acid acid, Writer buff) {
    }

    /**
     * parse时，在加入gene时，对自身检查，预处理（引擎预热）
     *
     * @param err 错误信息队列
     * @param rng rnaEngine缓存
     */
    public void check(StringBuilder err, RngChecker rng) {
    }

    public static class N implements Comparable<N> {
        public final int start;
        public final int until;
        public final Exon xna;

        public N(int p0, int p1, Exon xna) {
            this.start = p0;
            this.until = p1;
            this.xna = xna;
        }

        public boolean cross(N o) {
            return until > o.start && o.until > start;
        }

        @Override
        public int compareTo(@NotNull Exon.N o) {
            return start == o.start ? o.until - until : start - o.start;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, until, xna);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof N)) return false;
            N n = (N) o;
            return start == n.start && until == n.until && Objects.equals(xna, n.xna);
        }
    }
}
