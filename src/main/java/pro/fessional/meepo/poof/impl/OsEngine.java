package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.RnaEngine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static pro.fessional.meepo.bind.Const.ENGINE$CMD;
import static pro.fessional.meepo.bind.Const.ENGINE$EXE;
import static pro.fessional.meepo.bind.Const.ENGINE$SH;
import static pro.fessional.meepo.bind.Const.TXT$EMPTY;

/**
 * 单线程简单的执行 exec (Runtime, ProcessBuilder);
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class OsEngine implements RnaEngine {

    private static final String[] TYPE = {ENGINE$CMD, ENGINE$SH, ENGINE$EXE};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {

        ProcessBuilder builder = new ProcessBuilder();
        Map<String, String> env = builder.environment();
        for (Map.Entry<String, Object> en : ctx.entrySet()) {
            Object v = en.getValue();
            if (v != null) {
                env.put(en.getKey(), v.toString());
            }
        }

        if (ENGINE$CMD.equals(type)) {
            builder.command("cmd", "/c", expr);
        } else if (ENGINE$SH.equals(type)) {
            builder.command("bash", "-c", expr);
        } else {
            builder.command(arg(expr));
        }

        Process p = null;
        try {
            p = builder.start();

            String stdOut = TXT$EMPTY;
            if (!mute) {
                final InputStream out = p.getInputStream();
                BufferedReader ord = new BufferedReader(new InputStreamReader(out, UTF_8));

                StringBuilder buf = new StringBuilder();
                String ln;
                String crlf = System.lineSeparator();
                while ((ln = ord.readLine()) != null) {
                    buf.append(crlf).append(ln);
                }
                if (buf.length() > 0) {
                    stdOut = buf.substring(crlf.length());
                }
            }

            p.waitFor();
            return stdOut;
        } catch (Exception e) {
            throw new IllegalStateException(expr, e);
        } finally {
            if (p != null) p.destroy();
        }
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }


    /**
     * 按空白解析命令行，支持引号块和转义 "one\" arg"
     *
     * @param line 参数行
     * @return 解析后命令行
     */
    public static List<String> arg(String line) {
        if (line == null || line.isEmpty()) return Collections.emptyList();
        List<String> args = new ArrayList<>();
        int len = line.length();
        StringBuilder buf = new StringBuilder(len);
        char qto = 0;
        boolean esc = false;
        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);
            if (c == '\\') {
                if (esc) {
                    buf.append(c);
                    esc = false;
                } else {
                    esc = true;
                }
            } else if (c == '"' || c == '\'') {
                if (esc) {
                    buf.append(c);
                    esc = false;
                } else {
                    if (qto == 0) {
                        qto = c;
                    } else {
                        if (qto == c) {
                            args.add(buf.toString());
                            buf.setLength(0);
                            qto = 0;
                        } else {
                            buf.append(c);
                        }
                    }
                }
            } else if (c == ' ' || c == '\t') {
                if (qto > 0) {
                    buf.append(c);
                } else {
                    if (buf.length() > 0) {
                        args.add(buf.toString());
                        buf.setLength(0);
                    }
                }
            } else {
                if (esc) {
                    buf.append('\\');
                    esc = false;
                }
                buf.append(c);
            }
        }

        if (buf.length() > 0) {
            if (esc) {
                buf.append('\\');
            }
            args.add(buf.toString());
        }

        return args;
    }
}
