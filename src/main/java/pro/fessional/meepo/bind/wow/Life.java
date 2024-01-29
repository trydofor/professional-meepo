package pro.fessional.meepo.bind.wow;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author trydofor
 * @since 2020-10-16
 */
public class Life {

    public enum State {
        /**
         * Unavailable due to run out
         */
        Dead,
        /**
         * Unavailable due to skip
         */
        Skip,
        /**
         * Available
         */
        Live
    }

    protected int count = 0;
    protected int index = 0;
    @NotNull
    protected final List<int[]> book; // null = any
    @NotNull
    public final String name;

    private Life(String name, List<int[]> book) {
        this.name = name == null ? Const.TXT$EMPTY : name;
        this.book = book == null ? Collections.emptyList() : book;
    }

    /**
     * Enjoy the life once and return to the state
     */
    public State enjoy() {
        count++;

        int bs = book.size();
        if (index < 0 || (bs > 0 && index >= bs)) {
            return State.Dead;
        }
        if (index == 0 && bs == 0) {
            return State.Live;
        }

        int[] page = book.get(index);
        final State st;
        if (page.length == 2) {
            st = count >= page[0] && count <= page[1] ? State.Live : State.Skip;
        }
        else {
            st = count == page[0] ? State.Live : State.Skip;
        }

        if (count >= page[page.length - 1]) {
            index++;
        }
        return st;
    }

    public int count() {
        return count;
    }

    /**
     * Close the life
     */
    public void close() {
        index = -1;
    }

    /**
     * reset the life
     */
    public void reset() {
        count = 0;
        index = 0;
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("Life{")
                .append("count=").write(String.valueOf(count));
            buff.append(", index=").write(String.valueOf(index));
            buff.append(", name='").write(name);
            buff.append("', book=[");
            for (int[] ints : book) {
                buff.append(",");
                buff.append(Arrays.toString(ints));
            }
            buff.append("]}");
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Parse scope(Life) without blanks, `1,2,3` and `1-3`.
     */
    public static Life parse(String str) {
        int len = str.length();
        if (len == 0) return nobodyOne();

        str = str.trim();
        char c = str.charAt(0);
        if (c == '*') return nobodyAny();
        if (c < '0' || c > '9') return new Life(str, null);

        String[] part = str.split(",");
        ArrayList<int[]> books = new ArrayList<>(part.length);
        for (String s : part) {
            int m = s.indexOf('-');
            if (m < 0) {
                int p = Integer.parseInt(s);
                books.add(new int[]{p});
            }
            else {
                int p1 = Integer.parseInt(s.substring(0, m));
                int p2 = Integer.parseInt(s.substring(m + 1));
                if (p1 == p2) {
                    books.add(new int[]{p1});
                }
                else if (p1 < p2) {
                    books.add(new int[]{p1, p2});
                }
                else {
                    books.add(new int[]{p2, p1});
                }
            }
        }

        // sort and check range
        books.sort((o1, o2) -> o1[0] == o2[0] ? o2[o2.length - 1] - o1[o1.length - 1] : o1[0] - o2[0]);
        Iterator<int[]> it = books.iterator();
        int[] aa = it.next();
        while (it.hasNext()) {
            int[] bb = it.next();

            if (aa.length == 1 && bb.length == 1) {
                if (aa[0] == bb[0]) it.remove();
            }
            else if (aa.length == 2 && bb.length == 2) {
                if (bb[1] <= aa[1]) {
                    it.remove();
                }
                else if (bb[0] <= aa[1]) {
                    it.remove();
                    aa[1] = bb[1];
                }
            }
            else if (aa.length == 2 && bb.length == 1) {
                if (aa[1] >= bb[0]) it.remove();
            }
            else {
                // aa.length == 1 && bb.length == 2 (including)
                aa = bb;
            }
        }

        return new Life(null, books);
    }

    public static Life nobodyOne() {
        return new Life(null, One);
    }

    public static Life nobodyAny() {
        return new Life(null, Collections.emptyList());
    }

    public static Life namedAny(String name) {
        return new Life(name, Collections.emptyList());
    }

    private static final List<int[]> One = Collections.singletonList(new int[]{1});
}
