package pro.fessional.meepo.bind;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.poof.RngChecker;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 * ` &lt;% DNA:RAW The gaps are all one space %&gt; \n`
 * edge=`&lt;% DNA:RAW The gaps are all one space %&gt;`
 * main=`DNA:RAW The gaps are all one space`
 * xxxx9 is xxxx in char[] format, Usually for internal use
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class Exon {

    /**
     * Edge area of the original template
     */
    @NotNull
    public final Clop edge;

    /**
     * Caching of edge area
     */
    protected final char @NotNull [] text9;

    protected Exon(char @NotNull [] text9, @NotNull Clop edge) {
        this.edge = edge;
        this.text9 = text9;
    }

    public Exon(@NotNull String text, @NotNull Clop edge) {
        this(Dent.chars(text, edge), edge);
    }

    /**
     * Match other text with range and return Life.State
     *
     * @param lst the range
     * @param txt other text
     */
    public Life.State match(List<N> lst, String txt) {
        return Life.State.Skip;
    }

    /**
     * Apply the matching text to generate Gene
     *
     * @param pos the range
     * @param txt the text
     * @param bar left margin
     * @return Gene
     */
    @NotNull
    public List<Exon> apply(Clop pos, String txt, int bar) {
        return Collections.emptyList();
    }

    /**
     * Rebuild the parsed template back to the original text.
     *
     * @param buff buff to write
     */
    public void build(Writer buff) {
        Dent.write(buff, text9);
    }

    /**
     * Merge the template to buffer
     *
     * @param acid merge context
     * @param buff buffer to write
     */
    public void merge(Acid acid, Writer buff) {
    }

    /**
     * When parsing and adding to gene, self check and preprocessing (engine warm-up)
     *
     * @param err err message builder
     * @param rng rnaEngine caching
     */
    public void check(StringBuilder err, RngChecker rng) {
    }

    public static class N implements Comparable<N> {
        public final int start;
        public final int until;
        public final Exon xna;

        public N(int p0, int p1, Exon xna) {
            this.start = p0;
            this.until = p1;
            this.xna = xna;
        }

        public boolean cross(N o) {
            return until > o.start && o.until > start;
        }

        @Override
        public int compareTo(@NotNull Exon.N o) {
            return start == o.start ? o.until - until : start - o.start;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, until, xna);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof N)) return false;
            N n = (N) o;
            return start == n.start && until == n.until && Objects.equals(xna, n.xna);
        }
    }
}
