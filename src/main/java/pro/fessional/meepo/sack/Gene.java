package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.util.Dent;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
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

    private final ThreadLocal<CharArrayWriter> buff;
    private volatile String text7 = null;
    private volatile char[] text9 = null;

    public Gene(List<Exon> xna, Set<String> rng) {
        this.exon = Collections.unmodifiableList(xna);
        this.rngs = Collections.unmodifiableSet(rng);
        this.born = System.currentTimeMillis();
        this.buff = ThreadLocal.withInitial(() -> new CharArrayWriter(1024));
    }

    /**
     * 使用context合并
     *
     * @param ctx 上下文
     * @return 结果
     */
    @NotNull
    public String merge(Map<String, Object> ctx) {
        final String str = text7;
        if (str != null) return str;

        final CharArrayWriter out = buff.get();

        out.reset();
        merge(ctx, out);
        out.flush();

        final String rst = out.toString();
        if (rngs.isEmpty()) {
            synchronized (this) {
                if (text7 == null) {
                    text7 = rst;
                    text9 = rst.toCharArray();
                }
            }
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
        final char[] str = text9;
        if (rngs.isEmpty() && str != null) {
            Dent.pend(out, str);
        } else {
            Acid acid = new Acid(ctx, rngs);
            for (Exon exon : exon) {
                exon.merge(acid, out);
            }
            acid.clear();
        }
    }

    /**
     * 通过gene构造原始文本，有用调试。
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
     * 打印层级语法树，并校验edge的连续性
     *
     * @return 语法树
     */
    public String graph() {
        StringWriter buff = new StringWriter();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        buff.append("Gene, born=").append(sdf.format(new Date(born)));
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
    public int graph(Writer buff, List<Exon> exon, int level, int start) {
        for (Exon ex : exon) {
            Clop edge = ex.edge;
            if (edge.start != start) {
                Dent.dent(buff, -level);
            } else {
                Dent.dent(buff, level);
            }
            try {
                buff.append(ex.toString());
                buff.append('\n');
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            if (ex instanceof Tock) {
                Tock tock = (Tock) ex;
                start = graph(buff, tock.gene, level + 1, edge.until);
            } else {
                start = edge.until;
            }
        }
        return start;
    }
}
