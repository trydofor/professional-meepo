package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <pre>
 * ` plain text \n`
 * edge=` plain text \n`
 * main=` plain text \n`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtSimple extends Exon {

    public TxtSimple(@NotNull String text, Clop edge) {
        super(text, edge);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        Dent.write(buff, text9);
    }


    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("TxtSimple{");
            buff.append("text='");
            Dent.lineIt(buff, text9);
            buff.append("'}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
