package pro.fessional.meepo.bind;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * ` <% DNA:RAW 空白处都是一个空格 %> \n`
 * edge=`<% DNA:RAW 空白处都是一个空格 %>`
 * main=`DNA:RAW 空白处都是一个空格`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class Exon {

    /**
     * 边缘区
     */
    @NotNull
    public final Clop edge;

    /**
     * 原始文本
     */
    @NotNull
    public final String text;

    public Exon(@NotNull String text, @NotNull Clop edge) {
        this.edge = edge;
        this.text = text;
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
    public void build(Appendable buff) {
        Dent.pend(buff, text, edge.start, edge.until);
    }

    /**
     * 合并模板输出结果，默认edge
     *
     * @param acid 执行环境
     * @param buff 输出buff
     */
    public void merge(Acid acid, Appendable buff) {
    }

    /**
     * parse时，在加入gene时，对自身检查，预处理（引擎预热）
     *
     * @param err 错误信息队列
     */
    public void check(StringBuilder err) {
    }

    public static class N implements Comparable<N> {
        public final Clop pos;
        public final Exon xna;

        public N(int p0, int p1, Exon xna) {
            this.pos = new Clop(p0, p1);
            this.xna = xna;
        }

        @Override
        public int compareTo(@NotNull Exon.N o) {
            return pos.compareTo(o.pos);
        }
    }
}
