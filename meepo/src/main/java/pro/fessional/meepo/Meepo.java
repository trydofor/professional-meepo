package pro.fessional.meepo;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Holder;
import pro.fessional.meepo.sack.Parser;
import pro.fessional.meepo.util.Read;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 通过Meepo类，load的模板，可以缓存。
 * ttl表示，缓存的秒数，小于0为不过期，等于0为不缓存。
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class Meepo {

    public static final int CACHE_ALWAYS = -1;
    public static final int CACHE_NEVER = 0;
    public static final int CACHE_MINUTE = 60;
    public static final int CACHE_HOUR = CACHE_MINUTE * 60;
    public static final int CACHE_DAY = CACHE_HOUR * 24;
    public static final int CACHE_MONTH = CACHE_DAY * 30;

    private static final ConcurrentHashMap<String, Gene> CACHING = new ConcurrentHashMap<>();

    /**
     * 通过uri解析（ttl=CACHE_ALWAYS）
     * URI支持`file://`,`classpath:`,`http://`等
     *
     * @param uri 模板uri
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull String uri) {
        return parse(uri, CACHE_ALWAYS);
    }

    /**
     * 通过uri解析，URI支持`file://`,`classpath:`,`http://`等
     *
     * @param uri 模板uri
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull String uri, int ttl) {
        return ttl == 0 ? parse0(uri) : cache(uri, () -> parse0(uri), ttl);
    }

    /**
     * 直接解析文本，并通过key控制缓存（ttl=CACHE_ALWAYS）
     *
     * @param key 缓存key
     * @param txt 模板文本
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull String txt) {
        return parse(key, txt, CACHE_ALWAYS);
    }

    /**
     * 直接解析文本，并通过key控制缓存
     *
     * @param key 缓存key
     * @param txt 模板文本
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull String txt, int ttl) {
        return ttl == 0 ? Parser.parse(txt) : cache(key, () -> Parser.parse(txt), ttl);
    }

    /**
     * 解析文件，并通过key控制缓存（ttl=CACHE_ALWAYS）
     *
     * @param file 模板文件
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull File file) {
        return parse(file, CACHE_ALWAYS);
    }

    /**
     * 解析文件，并通过key控制缓存
     *
     * @param file 模板文件
     * @param ttl  缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull File file, int ttl) {
        if (ttl == 0) {
            return parse0(file);
        }

        String key = file.getAbsolutePath();
        return cache(key, () -> parse0(file), ttl);
    }

    /**
     * 解析文本，并通过key控制缓存（ttl=CACHE_ALWAYS）
     *
     * @param key 缓存key
     * @param ins 模板流
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull InputStream ins) {
        return parse(key, ins, CACHE_ALWAYS);
    }

    /**
     * 解析文本，并通过key控制缓存
     *
     * @param key 缓存key
     * @param ins 模板流
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 基因
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull InputStream ins, int ttl) {
        return ttl == 0 ? parse0(ins) : cache(key, () -> parse0(ins), ttl);
    }

    /////////////

    /**
     * 通过缓存，解析并合并占位符模板（ttl=CACHE_ALWAYS）
     *
     * @param ctx 合并环境
     * @param txt 占位符模板
     * @return 合并后文本
     * @see #piece(Map, String, int)
     */
    @NotNull
    public static String piece(@NotNull Map<String, Object> ctx, @NotNull String txt) {
        return merge(ctx, txt, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并占位符模板
     *
     * @param ctx 合并环境
     * @param txt 占位符模板
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, int)
     * @see pro.fessional.meepo.sack.Holder#parse(String)
     */
    @NotNull
    public static String piece(@NotNull Map<String, Object> ctx, @NotNull String txt, int ttl) {
        final Gene gene = ttl == 0 ? Holder.parse(txt) : cache(txt, () -> Holder.parse(txt), ttl);
        return gene.merge(ctx);
    }


    @NotNull
    public static Gene cache(@NotNull String key, @NotNull Supplier<Gene> gen, int ttl) {
        final Gene gene;
        if (ttl < 0) {
            gene = CACHING.computeIfAbsent(key, s -> gen.get());
        }
        else {
            final long born = System.currentTimeMillis() - ttl * 1000L;
            gene = CACHING.compute(key, (s, e) -> (e != null && born <= e.born) ? e : gen.get());
        }
        return Objects.requireNonNull(gene);
    }

    /**
     * 通过缓存，解析并合并（ttl=CACHE_ALWAYS）
     *
     * @param ctx 合并环境
     * @param uri 模板uri
     * @return 合并后文本
     * @see #parse(String, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String uri) {
        return merge(ctx, uri, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并
     *
     * @param ctx 合并环境
     * @param uri 模板uri
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String uri, int ttl) {
        Gene gene = parse(uri, ttl);
        return gene.merge(ctx);
    }

    /**
     * 通过缓存，解析并合并（ttl=CACHE_ALWAYS）
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param ins 模板流
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins) {
        return merge(ctx, key, ins, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param ins 模板流
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins, int ttl) {
        Gene gene = parse(key, ins, ttl);
        return gene.merge(ctx);
    }

    /**
     * 通过缓存，解析并合并（ttl=CACHE_ALWAYS）
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param txt 模板文本
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull String txt) {
        return merge(ctx, key, txt, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param txt 模板文本
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull String txt, int ttl) {
        Gene gene = parse(key, txt, ttl);
        return gene.merge(ctx);
    }

    /**
     * 通过缓存，解析并合并（ttl=CACHE_ALWAYS）
     *
     * @param ctx  合并环境
     * @param file 模板文件
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull File file) {
        return merge(ctx, file, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并
     *
     * @param ctx  合并环境
     * @param file 模板文件
     * @param ttl  缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull File file, int ttl) {
        Gene gene = parse(file, ttl);
        return gene.merge(ctx);
    }
    /////////////

    /**
     * 通过缓存，解析并合并，蒸汽它（ttl=CACHE_ALWAYS）
     *
     * @param ctx 合并环境
     * @param uri 模板uri
     * @return 合并后文本
     * @see #parse(String, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String uri) {
        return steam(ctx, uri, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并，蒸汽它
     *
     * @param ctx 合并环境
     * @param uri 模板uri
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String uri, int ttl) {
        Gene gene = parse(uri, ttl);
        return steam(ctx, gene);
    }

    /**
     * 通过缓存，解析并合并，蒸汽它（ttl=CACHE_ALWAYS）
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param ins 模板流
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins) {
        return steam(ctx, key, ins, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并，蒸汽它
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param ins 模板流
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins, int ttl) {
        Gene gene = parse(key, ins, ttl);
        return steam(ctx, gene);
    }

    /**
     * 通过缓存，解析并合并，蒸汽它（ttl=CACHE_ALWAYS）
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param txt 模板文本
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, final String txt) {
        return steam(ctx, key, txt, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并，蒸汽它
     *
     * @param ctx 合并环境
     * @param key 模板key
     * @param txt 模板文本
     * @param ttl 缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull String txt, int ttl) {
        Gene gene = parse(key, txt, ttl);
        return steam(ctx, gene);
    }

    /**
     * 通过缓存，解析并合并，蒸汽它（ttl=CACHE_ALWAYS）
     *
     * @param ctx  合并环境
     * @param file 模板文件
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull File file) {
        return steam(ctx, file, CACHE_ALWAYS);
    }

    /**
     * 通过缓存，解析并合并，蒸汽它
     *
     * @param ctx  合并环境
     * @param file 模板文件
     * @param ttl  缓存秒数，小于0为不过期，等于0为不缓存。
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull File file, int ttl) {
        Gene gene = parse(file, ttl);
        return steam(ctx, gene);
    }

    /**
     * 合并模板，并蒸汽它
     *
     * @param ctx  合并环境
     * @param gene 模板基因
     * @return 合并后文本
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull Gene gene) {
        String text = gene.merge(ctx);
        return new ByteArrayInputStream(text.getBytes(UTF_8));
    }

    ////////////

    /**
     * 不缓存，通过uri解析，URI支持`file://`,`classpath:`,`http://`等
     *
     * @param uri 模板uri
     * @return 基因
     */
    @NotNull
    private static Gene parse0(String uri) {
        String txt = Read.read(uri);
        return Parser.parse(txt, uri);
    }


    /**
     * 不缓存，解析文件
     *
     * @param file 模板文件
     * @return 基因
     */
    @NotNull
    private static Gene parse0(File file) {
        try {
            String txt = Read.read(new FileInputStream(file));
            return Parser.parse(txt, file.getAbsolutePath());
        }
        catch (FileNotFoundException e) {
            throw new IllegalStateException("failed to load file=" + file, e);
        }
    }

    /**
     * 不缓存，解析流
     *
     * @param ins 模板流
     * @return 基因
     */
    @NotNull
    private static Gene parse0(InputStream ins) {
        String txt = Read.read(ins);
        return Parser.parse(txt);
    }
}
