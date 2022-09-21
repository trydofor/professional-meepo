package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class Gene {

    public final long born;
    public final List<Exon> exon;
    public final Set<String> rngs;

    private volatile SoftReference<Text9> text9 = null;

    public Gene(List<Exon> xna, Set<String> rng) {
        this.exon = Collections.unmodifiableList(xna);
        this.rngs = Collections.unmodifiableSet(rng);
        this.born = System.currentTimeMillis();
    }

    /**
     * 使用context合并
     *
     * @param ctx 上下文
     * @return 结果
     */
    @NotNull
    public String merge(Map<String, Object> ctx) {
        return merge(ctx, 8096);
    }

    /**
     * 使用context合并
     *
     * @param ctx 上下文
     * @param len 预计结果的大小，避免buff扩容
     * @return 结果
     */
    @NotNull
    public String merge(Map<String, Object> ctx, int len) {
        final Text9 t9 = takeText9();
        if (t9 != null) {
            return t9.text7;
        }

        final StringWriter out = new StringWriter(len);
        merge(ctx, out);
        final String rst = out.toString();

        if (rngs.isEmpty()) {
            giveText9(rst);
        }
        return rst;
    }

    /**
     * 使用context合并
     *
     * @param ctx 上下文
     * @param out 输出
     */
    public void merge(Map<String, Object> ctx, Writer out) {
        if (rngs.isEmpty()) {
            final Text9 t9 = takeText9();
            if (t9 != null) {
                Dent.write(out, t9.chars);
                return;
            }
        }

        Acid acid = new Acid(ctx, rngs);
        for (Exon exon : exon) {
            exon.merge(acid, out);
        }
        acid.clear();
    }

    /**
     * 通过gene构造原始或转移后文本，有用调试。
     *
     * @return template
     */
    @NotNull
    public String build() {
        StringWriter buf = new StringWriter(1024);
        for (Exon exon : exon) {
            exon.build(buf);
        }
        return buf.toString();
    }

    /**
     * 打印层级语法树，并校验edge的连续性，`#`表示有交叉
     *
     * @return 语法树
     */
    public String graph() {
        StringWriter buff = new StringWriter();
        buff.append("Gene, born=").append(Instant.ofEpochMilli(born).toString());
        buff.append(", rngs=").write(String.join(",", rngs));
        buff.append('\n');
        graph(buff, exon, 1, 0);
        return buff.toString();
    }

    /**
     * 打印层级语法树，并校验edge的连续性
     *
     * @param buff  输出
     * @param exon  列表
     * @param level 层级，1-base
     * @param start 校验起点（包含），0-base
     * @return 校验终点，不包含
     */
    public static int graph(Writer buff, List<Exon> exon, int level, int start) {
        for (Exon ex : exon) {
            Clop edge = ex.edge;
            if (edge.start != start) {
                Dent.treeIt(buff, -level);
            }
            else {
                Dent.treeIt(buff, level);
            }
            try {
                buff.append(ex.toString());
                buff.append('\n');
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }

            if (ex instanceof Tock) {
                Tock tock = (Tock) ex;
                start = graph(buff, tock.gene, level + 1, edge.until);
            }
            else {
                start = edge.until;
            }
        }
        return start;
    }

    private void giveText9(String str) {
        if (text9 != null && text9.get() != null) {
            text9 = new SoftReference<>(new Text9(str));
        }
    }

    private Text9 takeText9() {
        if (text9 != null) {
            final Text9 t9 = text9.get();
            if (t9 != null) {
                return t9;
            }
            else {
                text9 = null;
            }
        }
        return null;
    }

    //
    private static class Text9 {
        private final String text7;
        private final char[] chars;

        public Text9(String t) {
            this.text7 = t;
            this.chars = t.toCharArray();
        }
    }
}
