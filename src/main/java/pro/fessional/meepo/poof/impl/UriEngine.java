package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.util.Read;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE_URI;
import static pro.fessional.meepo.bind.Const.TXT_EMPTY;

/**
 * 以UTF8输出URI内容
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class UriEngine implements RnaEngine {

    private static final String[] TYPE = {ENGINE_URI};
    private static final String CLAS = "classpath:";
    private static final String FILE = "file://";

    private final Map<String, String> cache = new HashMap<>();

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {
        String s = cache.get(expr);
        if (s != null) return s;

        String str;
        InputStream is;
        try {
            if (expr.regionMatches(true, 0, CLAS, 0, CLAS.length())) {
                is = UriEngine.class.getResourceAsStream(expr.substring(CLAS.length()));
            } else if (expr.regionMatches(true, 0, FILE, 0, FILE.length())) {
                is = new FileInputStream(expr.substring(FILE.length()));
            } else {
                char c = expr.charAt(0);
                if (c == '.' || c == '/' || c == '\\') {
                    is = new FileInputStream(expr);
                } else {
                    URL url = new URI(expr).toURL();
                    URLConnection con = url.openConnection();
                    con.setConnectTimeout(3000);
                    con.setReadTimeout(3000);
                    is = con.getInputStream();
                }
            }
            str = Read.read(is);
            cache.put(expr, str);
        } catch (Exception e) {
            throw new IllegalStateException(expr, e);
        }

        return mute ? TXT_EMPTY : str;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
