package pro.fessional.meepo.bind;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public final Clop edge;
    /**
     * 正文区
     */
    public final Clop main;

    /**
     * 原始文本
     */
    @NotNull
    public final String text;
    /**
     * 生命周期
     */
    @NotNull
    public final Life life;

    public Exon(@NotNull String text, @NotNull Life life, Clop edge) {
        this.edge = edge;
        this.main = edge;
        this.text = text;
        this.life = life;
    }

    public Exon(@NotNull String text, @NotNull Life life, Clop edge, Clop main) {
        this.edge = edge;
        this.main = main;
        this.text = text;
        this.life = life;
    }

    /**
     * 匹配其他文本，并返回匹配区间
     *
     * @param txt 文本
     * @param off 开始位置，包含
     * @param end 结束位置，不含
     * @return -1，不能匹配
     */
    public List<N> match(String txt, int off, int end) {
        return Collections.emptyList();
    }

    /**
     * 应用匹配的文本
     *
     * @param mtc 匹配
     * @param txt 文本
     * @param buf 输出
     */
    public void apply(N mtc, String txt, StringBuilder buf) {
    }

    /**
     * 重建回解析前的模板，默认edge
     *
     * @param buf buff
     */
    public void build(StringBuilder buf) {
        buf.append(text, edge.start, edge.until);
    }

    /**
     * 合并模板输出结果，默认edge
     *
     * @param ctx 上下文
     * @param buf buff
     */
    public void merge(Map<String, Object> ctx, StringBuilder buf) {
        buf.append(text, edge.start, edge.until);
    }


    public static class N {
        public final Clop pos;
        public final Exon xna;

        public N(int p0, int pos, Exon xna) {
            this.pos = new Clop(p0, pos);
            this.xna = xna;
        }

        public void apply(String txt, StringBuilder buf) {
            xna.apply(this, txt, buf);
        }
    }
}
