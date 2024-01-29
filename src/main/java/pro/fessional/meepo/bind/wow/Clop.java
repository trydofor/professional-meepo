package pro.fessional.meepo.bind.wow;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * close-open range
 *
 * @author trydofor
 * @since 2020-10-19
 */
public class Clop implements Comparable<Clop> {

    public final int start;
    public final int until;
    public final int length;

    public final int line0;
    public final int line1;

    public Clop(int start, int until, int line0, int line1) {
        this.start = start;
        this.until = until;
        this.length = until - start;
        this.line0 = line0;
        this.line1 = line1;
    }

    /**
     * Whether the two intervals overlap
     *
     * @param o other clop
     */
    public boolean cross(Clop o) {
        return until > o.start && o.until > start;
    }

    @Override
    public int compareTo(@NotNull Clop o) {
        return start == o.start ? o.until - until : start - o.start;
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("Clop{");
            buff.append("start=").write(String.valueOf(start));
            buff.append(", until=").write(String.valueOf(until));
            buff.append(", length=").write(String.valueOf(length));
            buff.append(", line0=").write(String.valueOf(line0));
            buff.append(", line1=").write(String.valueOf(line1));
            buff.write("}");
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
