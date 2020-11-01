package pro.fessional.meepo.util;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.poof.impl.UriEngine;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author trydofor
 * @since 2020-10-31
 */
public class Read {

    private static final String CLAS = "classpath:";
    private static final String FILE = "file://";

    @NotNull
    public static String read(String uri) {
        return read(uri, UTF_8);
    }

    public static String read(String uri, Charset cs) {
        String str;
        InputStream is;
        try {
            if (uri.regionMatches(true, 0, CLAS, 0, CLAS.length())) {
                is = UriEngine.class.getResourceAsStream(uri.substring(CLAS.length()));
            } else if (uri.regionMatches(true, 0, FILE, 0, FILE.length())) {
                is = new FileInputStream(uri.substring(FILE.length()));
            } else {
                char c = uri.charAt(0);
                if (c == '.' || c == '/' || c == '\\') {
                    is = new FileInputStream(uri);
                } else {
                    URL url = new URI(uri).toURL();
                    URLConnection con = url.openConnection();
                    con.setConnectTimeout(3000);
                    con.setReadTimeout(3000);
                    is = con.getInputStream();
                }
            }
            str = Read.read(is);
        } catch (Exception e) {
            throw new IllegalStateException(uri, e);
        }
        return str;
    }

    @NotNull
    public static String read(InputStream is) {
        return read(is, UTF_8);
    }

    /**
     * 自动close InputStream
     *
     * @param is 输入流
     * @param cs 字符集，默认UTF8
     * @return 字符串
     */
    @NotNull
    public static String read(InputStream is, Charset cs) {
        if (is == null) return Const.TXT$EMPTY;
        if (cs == null) cs = UTF_8;

        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        try (Reader rd = new InputStreamReader(is, cs)) {
            while ((len = rd.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return sb.toString();
    }
}
