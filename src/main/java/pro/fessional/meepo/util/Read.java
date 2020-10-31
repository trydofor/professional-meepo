package pro.fessional.meepo.util;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author trydofor
 * @since 2020-10-31
 */
public class Read {

    @NotNull
    public static String read(InputStream is) {
        if (is == null) return Const.TXT_EMPTY;

        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        try (Reader rd = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            while ((len = rd.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return sb.toString();
    }
}
