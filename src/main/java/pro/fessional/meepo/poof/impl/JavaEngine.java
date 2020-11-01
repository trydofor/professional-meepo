package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.Meepo;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.impl.java.JavaEval;
import pro.fessional.meepo.util.Java;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.fessional.meepo.bind.Const.ENGINE$JAVA;
import static pro.fessional.meepo.bind.Const.TXT$EMPTY;

/**
 * 编译java代码并执行
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class JavaEngine implements RnaEngine {

    private static final Logger logger = LoggerFactory.getLogger(JavaEngine.class);
    private static final String[] TYPE = {ENGINE$JAVA};
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final ConcurrentHashMap<String, JavaEval> exprJava = new ConcurrentHashMap<>();

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {
        JavaEval java = exprJava.computeIfAbsent(expr, this::compile);
        Object rst = java.eval(ctx);
        return mute ? TXT$EMPTY : rst;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }

    private static final Pattern PtnImps = Pattern.compile("\\s*import\\s+[^;]+;\\s*", Pattern.MULTILINE);

    public JavaEval compile(String expr) {
        final long s = System.currentTimeMillis();
        Matcher m = PtnImps.matcher(expr);
        String imps, body, colon;
        if (m.find()) {
            StringBuilder buf = new StringBuilder();
            int end;
            do {
                end = m.end();
                for (int i = m.start(); i < end; i++) {
                    char c = expr.charAt(i);
                    if (c == ',') {
                        buf.append(";\nimport ");
                    } else {
                        buf.append(c);
                    }
                }
            } while (m.find());
            imps = buf.toString();
            body = expr.substring(end);
        } else {
            imps = TXT$EMPTY;
            body = expr;
        }
        colon = TXT$EMPTY;
        for (int i = body.length() - 1; i >= 0; i--) {
            char c = body.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (c != ';') colon = ";";
                break;
            }
        }

        HashMap<String, Object> ctx = new HashMap<>();
        String name = "Java" + counter.incrementAndGet();
        ctx.put("import", imps);
        ctx.put("class", name);
        ctx.put("method", body);
        ctx.put("colon", colon);
        String uri = "classpath:/pro/fessional/meepo/poof/impl/java/JavaName.java";
        String code = Meepo.merge(ctx, uri, Meepo.CACHE_ALWAYS);
        try {
            Class<JavaEval> clz = Java.compile("pro.fessional.meepo.poof.impl.java." + name, code);
            JavaEval java = Java.create(clz);
            logger.info("cost {}ms to compile {}", System.currentTimeMillis() - s, expr);
            return java;
        } catch (RuntimeException e) {
            logger.error("failed to create java-code=\n" + code + "\n", e);
            throw e;
        }
    }
}
