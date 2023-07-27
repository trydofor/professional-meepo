package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;
import pro.fessional.meepo.util.Eval;
import pro.fessional.meepo.util.Eval.ArgType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static pro.fessional.meepo.bind.Const.ENGINE$CMD;
import static pro.fessional.meepo.bind.Const.ENGINE$EXE;
import static pro.fessional.meepo.bind.Const.ENGINE$SH;
import static pro.fessional.meepo.bind.Const.TXT$EMPTY;

/**
 * Exec (Runtime, ProcessBuilder) command line in single-thread
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class OsEngine implements RnaEngine {

    private static final Logger logger = LoggerFactory.getLogger(OsEngine.class);

    private static final String[] TYPE = {ENGINE$CMD, ENGINE$SH, ENGINE$EXE};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull RnaWarmed warm(@NotNull String type, @NotNull String expr) {
        RnaWarmed warmed;
        if (ENGINE$CMD.equalsIgnoreCase(type) || ENGINE$SH.equalsIgnoreCase(type)) {
            warmed = new RnaWarmed(type, expr);
        }
        else {
            warmed = new RnaWarmed(type, expr, Eval.parseArgs(expr, ArgType.Str));
            if (!ENGINE$EXE.equalsIgnoreCase(type)) {
                warmed.info = "\nunsupported type=" +
                              type + ", expr=" + expr;
            }
        }
        return warmed;
    }

    @Override
    public @NotNull Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute) {

        ProcessBuilder builder = new ProcessBuilder();
        Map<String, String> env = builder.environment();
        for (Map.Entry<String, Object> en : ctx.entrySet()) {
            Object v = en.getValue();
            if (v != null) {
                env.put(en.getKey(), v.toString());
            }
        }

        if (ENGINE$CMD.equalsIgnoreCase(expr.type)) {
            builder.command("cmd", "/c", expr.expr);
        }
        else if (ENGINE$SH.equalsIgnoreCase(expr.type)) {
            builder.command("bash", "-c", expr.expr);
        }
        else {
            List<String> args = expr.getTypedWork();
            builder.command(args);
        }

        Process p = null;
        String out = TXT$EMPTY;
        try {
            p = builder.start();

            final InputStream is = p.getInputStream();
            BufferedReader ord = new BufferedReader(new InputStreamReader(is, UTF_8));

            StringBuilder buf = new StringBuilder();
            String ln;
            String crlf = System.lineSeparator();
            while ((ln = ord.readLine()) != null) {
                buf.append(crlf).append(ln);
            }
            if (buf.length() > 0) {
                out = buf.substring(crlf.length());
            }

            p.waitFor();
        }
        catch (Throwable t) {
            if (mute) {
                logger.warn("mute failed-eval " + expr, t);
            }
            else {
                throw new IllegalStateException(expr.toString(), t);
            }
        }
        finally {
            if (p != null) p.destroy();
        }
        return out;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
