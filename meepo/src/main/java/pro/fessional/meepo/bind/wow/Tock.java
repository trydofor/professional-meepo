package pro.fessional.meepo.bind.wow;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.util.Dent;

import java.io.Writer;
import java.util.ArrayList;

/**
 * Process Control
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class Tock extends Exon {

    protected static final Logger logger = LoggerFactory.getLogger(Tock.class);

    public final @NotNull String tock;
    public final ArrayList<Exon> gene;
    protected final char @NotNull [] tock9;

    public Tock(@NotNull String text, @NotNull Clop edge, @NotNull String tock) {
        this(Dent.chars(text, edge), edge, tock);
    }

    protected Tock(char @NotNull [] text9, @NotNull Clop edge, @NotNull String tock) {
        super(text9, edge);
        this.tock = tock;
        this.gene = new ArrayList<>();
        this.tock9 = tock.toCharArray();
    }

    @Override
    public void build(Writer buff) {
        super.build(buff);
        for (Exon exon : gene) {
            exon.build(buff);
        }
    }
}
