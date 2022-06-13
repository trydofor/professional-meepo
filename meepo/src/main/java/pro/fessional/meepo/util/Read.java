package pro.fessional.meepo.util;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 自动读取`classpath:`，`file://`和URL格式，
 * 如果没有协议部分，尝试以classpath和file读取
 *
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

    @NotNull
    public static String read(String uri, Charset cs) {
        String str;
        InputStream is = null;
        try {
            if (uri.regionMatches(true, 0, CLAS, 0, CLAS.length())) {
                is = Read.class.getResourceAsStream(uri.substring(CLAS.length()));
            }
            else if (uri.regionMatches(true, 0, FILE, 0, FILE.length())) {
                is = new FileInputStream(uri.substring(FILE.length()));
            }
            else {
                if (uri.indexOf(':') < 0) {
                    is = Read.class.getResourceAsStream(uri);
                    if (is == null) {
                        try {
                            is = new FileInputStream(uri);
                        }
                        catch (FileNotFoundException e) {
                            // ignore
                        }
                    }
                }

                if (is == null) {
                    URL url = new URI(uri).toURL();
                    URLConnection con = url.openConnection();
                    con.setConnectTimeout(3000);
                    con.setReadTimeout(3000);
                    is = con.getInputStream();
                }
            }
            str = Read.read(is, cs);
        }
        catch (Exception e) {
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
        return read(new InputStreamReader(is, cs == null ? UTF_8 : cs));
    }

    /**
     * 自动close Reader
     *
     * @param rd reader
     * @return 字符串
     */
    @NotNull
    public static String read(Reader rd) {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        try (Reader r0 = rd) {
            while ((len = r0.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return sb.toString();
    }
}
