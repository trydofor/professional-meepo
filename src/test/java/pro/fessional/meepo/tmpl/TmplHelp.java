package pro.fessional.meepo.tmpl;

import pro.fessional.meepo.bind.Const;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class TmplHelp {

    public static String load(String path) {
        InputStream is = TmplHelp.class.getResourceAsStream(path);
        StringBuilder buff = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                buff.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    public static String trim(String txt) {
        if (txt == null) return Const.TXT_EMPTY;
        return txt.replaceAll("[\n\t\r ]+", " ");
    }
}
