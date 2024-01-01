package pro.fessional.meepo.util;

import pro.fessional.meepo.bind.wow.Clop;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-11-01
 */
public class Dent {

    private static final Map<Integer, char[]> PADDING = new HashMap<>();

    public static char[] chars(String text, Clop clop) {
        char[] chs = new char[clop.length];
        text.getChars(clop.start, clop.until, chs, 0);
        return chs;
    }

    public static char[] chars(String text, int start, int end) {
        char[] chs = new char[end - start];
        text.getChars(start, end, chs, 0);
        return chs;
    }

    public static void write(Writer out, char[] str) {
        try {
            out.write(str);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Output as a tree, `#` means intersection
     *
     * @param out   the output
     * @param level indent level
     */
    public static void treeIt(Writer out, int level) {
        char[] chr = PADDING.computeIfAbsent(level, s -> {
            boolean ng = false;
            if (s < 0) {
                s = -s;
                ng = true;
            }
            StringBuilder sb = new StringBuilder(2 * s);
            for (int i = 0; i < s; i++) {
                if (ng) {
                    sb.append("#-");
                }
                else {
                    sb.append("|-");
                }
            }
            char[] cs = new char[sb.length()];
            sb.getChars(0, cs.length, cs, 0);
            return cs;
        });

        write(out, chr);
    }

    public static void lineIt(Writer out, char[] str) {
        try {
            if (str == null) return;
            for (char c : str) {
                lineIt(out, c);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void lineIt(Writer out, CharSequence str, Clop edg) {
        lineIt(out, str, edg.start, edg.until);
    }

    public static void lineIt(Writer out, CharSequence str, int off, int end) {
        try {
            if (str == null) return;
            for (int i = off; i < end; i++) {
                lineIt(out, str.charAt(i));
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void lineIt(Writer out, char c) throws IOException {
        if (c == '\\') {
            out.append("\\\\");
        }
        else if (c == '\r') {
            out.append("\\r");
        }
        else if (c == '\n') {
            out.append("\\n");
        }
        else if (c == '\t') {
            out.append("\\t");
        }
        else {
            out.append(c);
        }
    }

    public static void lineIt(Writer out, CharSequence str) {
        if (str == null) return;
        lineIt(out, str, 0, str.length());
    }

    private static final char[] LEFT = "                                                                ".toCharArray();

    public static void indent(Writer out, int lft) {
        try {
            for (int i = lft; i > 0; i -= LEFT.length) {
                if (i >= LEFT.length) {
                    out.write(LEFT);
                }
                else {
                    out.write(LEFT, 0, i);
                }
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void indent(Writer out, int lft, Object val) {
        if (val == null) return;

        Class<?> clz = val.getClass();
        if (Collection.class.isAssignableFrom(clz)) {
            boolean f = false;
            for (Object o : ((Collection<?>) val)) {
                if (o == null) continue;
                if (f) {
                    indent(out, lft);
                }
                else {
                    f = true;
                }
                toString(out, o);
            }
        }
        else if (clz.isArray()) {
            boolean f = false;
            for (int i = 0, len = Array.getLength(val); i < len; i++) {
                Object o = Array.get(val, i);
                if (o == null) continue;

                if (f) {
                    indent(out, lft);
                }
                else {
                    f = true;
                }
                toString(out, o);
            }
        }
        else {
            toString(out, val);
        }
    }

    public static void toString(Writer out, Object val) {
        if (val == null) return;

        if (out instanceof StringWriter) {
            StringBuffer buf = ((StringWriter) out).getBuffer();
            if (val instanceof CharSequence) {
                buf.append((CharSequence) val);
            }
            else if (val instanceof Integer) {
                buf.append(((Integer) val).intValue());
            }
            else if (val instanceof Long) {
                buf.append(((Long) val).longValue());
            }
            else if (val instanceof Double) {
                buf.append(((Double) val).doubleValue());
            }
            else if (val instanceof Float) {
                buf.append(((Float) val).floatValue());
            }
            else if (val instanceof Boolean) {
                buf.append(((Boolean) val).booleanValue());
            }
            else if (val instanceof Short) {
                buf.append(((Short) val).shortValue());
            }
            else if (val instanceof Byte) {
                buf.append(((Byte) val).byteValue());
            }
            else if (val instanceof Character) {
                buf.append(((Character) val).charValue());
            }
            else if (val instanceof BigDecimal) {
                buf.append(((BigDecimal) val).toPlainString());
            }
            else if (val instanceof char[]) {
                buf.append((char[]) val);
            }
            else {
                buf.append(val);
            }
        }
        else {
            try {
                if (val instanceof String) {
                    out.write((String) val);
                }
                else if (val instanceof Character) {
                    out.write((Character) val);
                }
                else if (val instanceof BigDecimal) {
                    out.write(((BigDecimal) val).toPlainString());
                }
                else if (val instanceof char[]) {
                    out.write((char[]) val);
                }
                else {
                    out.write(val.toString());
                }
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
