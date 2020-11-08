package pro.fessional.meepo.bind.wow;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.fessional.meepo.bind.wow.Life.State.Dead;
import static pro.fessional.meepo.bind.wow.Life.State.Live;
import static pro.fessional.meepo.bind.wow.Life.State.Skip;

/**
 * 生命倒计时
 *
 * @author trydofor
 * @since 2020-10-29
 */
public class Tick extends Exon {

    @NotNull
    public final Life life;

    protected Tick(@NotNull char[] text, @NotNull Clop edge, @NotNull Life life) {
        super(text, edge);
        this.life = life;
    }

    public Tick(@NotNull String text, @NotNull Clop edge, @NotNull Life life) {
        super(text, edge);
        this.life = life;
    }

    protected Life.State match(List<N> lst, String txt, Pattern find) {
        Matcher m = find.matcher(txt);
        Life.State st = Skip;
        while (m.find()) {
            st = life.enjoy();
            if (st == Live) {
                int p0, p1;
                if (m.groupCount() > 0) {
                    p0 = m.start(1);
                    p1 = m.end(1);
                } else {
                    p0 = m.start();
                    p1 = m.end();
                }
                lst.add(new N(p0, p1, this));
            } else if (st == Dead) {
                break;
            }
        }
        return st;
    }
}
