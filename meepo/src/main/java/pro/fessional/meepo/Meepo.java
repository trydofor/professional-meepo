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
 * Use Meepo to load the template and can it.
 * `ttl` means the seconds to cache, less than 0 is not expired, equal to 0 is not cached.
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
     * Parse form the URI with ttl=CACHE_ALWAYS
     * URI support `file://`,`classpath:`,`http://`
     */
    @NotNull
    public static Gene parse(@NotNull String uri) {
        return parse(uri, CACHE_ALWAYS);
    }

    /**
     * Parse form the URI with the given ttl.
     * URI support `file://`,`classpath:`,`http://`
     */
    @NotNull
    public static Gene parse(@NotNull String uri, int ttl) {
        return ttl == 0 ? parse0(uri) : cache(uri, () -> parse0(uri), ttl);
    }

    /**
     * Parses text directly and cache by key with ttl=CACHE_ALWAYS
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull String txt) {
        return parse(key, txt, CACHE_ALWAYS);
    }

    /**
     * Parses text directly and cache by key with the given ttl.
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull String txt, int ttl) {
        return ttl == 0 ? Parser.parse(txt) : cache(key, () -> Parser.parse(txt), ttl);
    }

    /**
     * Parses from file and cache by its path with ttl=CACHE_ALWAYS
     */
    @NotNull
    public static Gene parse(@NotNull File file) {
        return parse(file, CACHE_ALWAYS);
    }

    /**
     * Parses from file and cache by its path with the given ttl.
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
     * Parses from stream and cache by its path with ttl=CACHE_ALWAYS
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull InputStream ins) {
        return parse(key, ins, CACHE_ALWAYS);
    }

    /**
     * Parses from stream and cache by its path with the given ttl.
     */
    @NotNull
    public static Gene parse(@NotNull String key, @NotNull InputStream ins, int ttl) {
        return ttl == 0 ? parse0(ins) : cache(key, () -> parse0(ins), ttl);
    }

    /////////////

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template, then merge with the context.
     *
     * @param ctx the context
     * @param txt placeholder template
     * @return result
     * @see #piece(Map, String, int)
     */
    @NotNull
    public static String piece(@NotNull Map<String, Object> ctx, @NotNull String txt) {
        return merge(ctx, txt, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template, then merge with the context.
     *
     * @param ctx the context
     * @param txt placeholder template
     * @param ttl cache ttl
     * @return result
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
     * Parse and cache (ttl=CACHE_ALWAYS) the template form URI, then merge with the context.
     *
     * @see #parse(String, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String uri) {
        return merge(ctx, uri, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template form URI, then merge with the context.
     *
     * @see #parse(String, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String uri, int ttl) {
        Gene gene = parse(uri, ttl);
        return gene.merge(ctx);
    }

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template form stream, then merge with the context.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins) {
        return merge(ctx, key, ins, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template form stream, then merge with the context.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins, int ttl) {
        Gene gene = parse(key, ins, ttl);
        return gene.merge(ctx);
    }

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template text, then merge with the context.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull String txt) {
        return merge(ctx, key, txt, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template text, then merge with the context.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull String txt, int ttl) {
        Gene gene = parse(key, txt, ttl);
        return gene.merge(ctx);
    }

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template form file, then merge with the context.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull File file) {
        return merge(ctx, file, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template form file, then merge with the context.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static String merge(@NotNull Map<String, Object> ctx, @NotNull File file, int ttl) {
        Gene gene = parse(file, ttl);
        return gene.merge(ctx);
    }
    /////////////

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template form URI, then merge with the context to stream.
     *
     * @see #parse(String, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String uri) {
        return steam(ctx, uri, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template form URI, then merge with the context to stream.
     *
     * @see #parse(String, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String uri, int ttl) {
        Gene gene = parse(uri, ttl);
        return steam(ctx, gene);
    }

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template form stream, then merge with the context to stream.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins) {
        return steam(ctx, key, ins, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template form stream, then merge with the context to stream.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull InputStream ins, int ttl) {
        Gene gene = parse(key, ins, ttl);
        return steam(ctx, gene);
    }

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template text, then merge with the context to stream.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, final String txt) {
        return steam(ctx, key, txt, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template text, then merge with the context to stream.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull String key, @NotNull String txt, int ttl) {
        Gene gene = parse(key, txt, ttl);
        return steam(ctx, gene);
    }

    /**
     * Parse and cache (ttl=CACHE_ALWAYS) the template from file, then merge with the context to stream.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull File file) {
        return steam(ctx, file, CACHE_ALWAYS);
    }

    /**
     * Parse and cache the template from file, then merge with the context to stream.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull File file, int ttl) {
        Gene gene = parse(file, ttl);
        return steam(ctx, gene);
    }

    /**
     * Merge the Gene with the context to stream.
     *
     * @see #parse(String, InputStream, int)
     */
    @NotNull
    public static InputStream steam(@NotNull Map<String, Object> ctx, @NotNull Gene gene) {
        String text = gene.merge(ctx);
        return new ByteArrayInputStream(text.getBytes(UTF_8));
    }

    ////////////

    /**
     * No cache, only parse template from URI support `file://`,`classpath:`,`http://`
     */
    @NotNull
    private static Gene parse0(String uri) {
        String txt = Read.read(uri);
        return Parser.parse(txt, uri);
    }


    /**
     * No cache, only parse template from file
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
     * No cache, only parse template from stream
     */
    @NotNull
    private static Gene parse0(InputStream ins) {
        String txt = Read.read(ins);
        return Parser.parse(txt);
    }
}
