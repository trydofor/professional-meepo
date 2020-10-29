package pro.fessional.meepo.bind;

import org.jetbrains.annotations.NotNull;

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

    public Clop(int start) {
        this.start = start;
        this.until = start + 1;
        this.length = until - start;
    }

    public Clop(int start, int until) {
        this.start = start;
        this.until = until;
        this.length = until - start;
    }

    public Clop shift(int off) {
        return new Clop(start + off, until + off);
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
        return "Clop{" +
                "start=" + start +
                ", until=" + until +
                ", length=" + length +
                '}';
    }
}
