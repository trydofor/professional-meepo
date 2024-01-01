package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * <pre>
 * ` // DNA:SON classpath://template/son/include-f.txt \n`
 * edge=`DNA:SON classpath://template/son/include-f.txt \n`
 *
 * main=`DNA:SON classpath://template/son/include-f.txt`
 * path=`classpath://template/son/include-f.txt`
 * </pre>
 *
 * @author trydofor
 * @since 2022-07-02
 */
public class DnaSon extends Exon {

    protected static final Logger logger = LoggerFactory.getLogger(DnaSon.class);

    @NotNull
    public final String path;
    public final ArrayList<Exon> gene = new ArrayList<>();

    public DnaSon(@NotNull String text, Clop edge, @NotNull String path) {
        super(text, edge);
        this.path = path;
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        logger.trace("[👹Merge:son] deal DNA:SON path={}", path);
        for (Exon exon : gene) {
            exon.merge(acid, buff);
        }
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("DnaSon{");
            buff.append("path='");
            Dent.lineIt(buff, path);
            buff.append("'}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
