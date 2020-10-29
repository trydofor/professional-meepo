package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Exon;

import java.util.Objects;

/**
 * <pre>
 * ` <% H!MEEPO %> \n`
 * head=`<%`
 * tail=`%>`
 * edge=`<% H!MEEPO %>`
 * main=`H!MEEPO`
 *
 * ` // H!MEEPO \n`
 * head=`//`
 * tail=`\n`
 * edge=`// H!MEEPO \n`
 * main=`H!MEEPO`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class HiMeepo extends Exon {

    @NotNull
    public final Clop main;
    @NotNull
    public final String head;
    @NotNull
    public final String tail;
    /**
     * 是否支持字符重复
     */
    public final boolean echo;
    /**
     * 是否以保留空白
     */
    public final boolean trim;
    /**
     * 是否以\n结尾
     */
    public final boolean crlf;

    public HiMeepo(@NotNull String text, Clop edge, @NotNull Clop main, @NotNull String head, @NotNull String tail, boolean trim) {
        super(text, edge);
        this.main = main;
        this.head = head;
        this.tail = tail;
        this.trim = trim;
        boolean b = true;
        char c0 = head.charAt(0);
        for (int i = 1, len = head.length(); i < len; i++) {
            if (c0 != head.charAt(i)) {
                b = false;
                break;
            }
        }
        this.echo = b;
        this.crlf = tail.equals("\n");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiMeepo hiMeepo = (HiMeepo) o;
        return echo == hiMeepo.echo &&
                trim == hiMeepo.trim &&
                crlf == hiMeepo.crlf &&
                head.equals(hiMeepo.head) &&
                tail.equals(hiMeepo.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail, echo, trim, crlf);
    }

    @Override
    public String toString() {
        return "HiMeepo{" +
                "head='" + head + '\'' +
                ", tail='" + tail + '\'' +
                ", echo=" + echo +
                ", trim=" + trim +
                ", crlf=" + crlf +
                '}';
    }
}