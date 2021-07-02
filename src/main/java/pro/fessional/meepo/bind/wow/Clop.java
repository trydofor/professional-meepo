package pro.fessional.meepo.bind.wow;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

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
     * 两个区间是否有交叠
     *
     * @param o 另外一个
     * @return 是否交叠
     */
    public boolean cross(Clop o) {
        return until > o.start && o.until > start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clop n = (Clop) o;
        return start == n.start && until == n.until;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, until);
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
