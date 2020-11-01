package pro.fessional.meepo.bind.wow;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.bind.Exon;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 流程控制器
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class Tock extends Exon {

    protected static final Logger logger = LoggerFactory.getLogger(Tock.class);

    @NotNull
    public final String tock;
    public final ArrayList<Exon> gene;

    public Tock(@NotNull String text, @NotNull Clop edge, @NotNull String tock) {
        super(text, edge);
        this.tock = tock;
        this.gene = new ArrayList<>();
    }

    @Override
    public void build(StringBuilder buf) {
        super.build(buf);
        for (Exon exon : gene) {
            exon.build(buf);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tock tock1 = (Tock) o;
        return tock.equals(tock1.tock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tock);
    }

    @Override
    public String toString() {
        return "Tock{" +
                "tock='" + tock + '\'' +
                '}';
    }
}
