package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * <pre>
 * ` <% HI-MEEPO %> \n`
 * head=`<%`
 * tail=`%>`
 * edge=`<% HI-MEEPO %>`
 * main=`HI-MEEPO`
 *
 * ` // HI-MEEPO \n`
 * head=`//`
 * tail=`\n`
 * edge=`// HI-MEEPO \n`
 * main=`HI-MEEPO`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class HiMeepo extends Exon {

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
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("HiMeepo{");
            buff.append("head='");
            Dent.lineIt(buff, head);
            buff.append("', tail='");
            Dent.lineIt(buff, tail);
            buff.append("', echo=");
            buff.append(String.valueOf(echo));
            buff.append(", trim=");
            buff.append(String.valueOf(trim));
            buff.append(", crlf=");
            buff.append(String.valueOf(crlf));
            buff.append('}');
            buff.append("; ");
            edge.toString(buff);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
