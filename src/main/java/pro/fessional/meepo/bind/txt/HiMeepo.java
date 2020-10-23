package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;

import java.util.Map;
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
    public final String head;
    @NotNull
    public final String tail;

    public final boolean echo;
    public final boolean islf;

    public HiMeepo(@NotNull String text, Clop edge, Clop main, @NotNull String head, @NotNull String tail) {
        super(text, Life.nobodyAny(), edge, main);
        this.head = head;
        this.tail = tail;
        boolean b = true;
        char c0 = head.charAt(0);
        for (int i = 1, len = head.length(); i < len; i++) {
            if (c0 != head.charAt(i)) {
                b = false;
                break;
            }
        }
        this.echo = b;
        this.islf = tail.equals("\n");
    }

    @Override
    public void merge(Map<String, Object> ctx, StringBuilder buf) {
        // skip
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiMeepo hiMeepo = (HiMeepo) o;
        return head.equals(hiMeepo.head) &&
                tail.equals(hiMeepo.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }

    @Override
    public String toString() {
        return "HiMeepo{" +
                "head='" + head + '\'' +
                ", tail='" + tail + '\'' +
                '}';
    }
}
