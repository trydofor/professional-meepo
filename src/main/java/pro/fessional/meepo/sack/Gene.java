package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Dyn;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.util.Dent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class Gene {

    private static final int magic9 = 19;
    public final String text;
    public final int size;
    public final long born;
    private final List<Exon> exon;

    private final AtomicReference<String> cache;
    private volatile int leng = -1;

    public Gene(List<Exon> xna, String txt) {
        this.exon = new ArrayList<>(xna);
        this.text = txt;
        this.size = txt.length() + magic9;
        this.born = System.currentTimeMillis();
        this.cache = new AtomicReference<>();
    }

    /**
     * 使用context合并
     *
     * @param ctx 上下文
     * @return 结果
     */
    @NotNull
    public String merge(Map<String, Object> ctx) {
        String s = cache.get();
        if (s != null) return s;

        final int len = Math.max(leng, size);
        StringBuilder buf = new StringBuilder(len);
        Acid acid = new Acid(ctx);

        boolean cac = true;
        for (Exon exon : exon) {
            if (exon instanceof Dyn) cac = false;
            exon.merge(acid, buf);
        }

        String rst = buf.toString();
        if (cac) {
            cache.set(rst);
        } else {
            int bl = rst.length();
            if (bl > len) {
                this.leng = bl + magic9;
            }
        }
        return rst;
    }

    /**
     * 获得原始文本
     *
     * @return 文本
     */
    @NotNull
    public String origin() {
        return text;
    }

    /**
     * 通过gene构造原始文本，有用调试。建议使用#origin()
     *
     * @return template
     * @see #origin()
     */
    @NotNull
    public String build() {
        StringBuilder buf = new StringBuilder(size);
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
        StringBuilder buff = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        buff.append("Gene, born=").append(sdf.format(new Date(born))).append(", size=").append(size).append('\n');
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
    public int graph(StringBuilder buff, List<Exon> exon, int level, int start) {
        for (Exon ex : exon) {
            Clop edge = ex.edge;
            if (edge.start != start) {
                buff.append(Dent.dent(-level));
            } else {
                buff.append(Dent.dent(level));
            }
            buff.append(ex.toString());
            buff.append('\n');

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
