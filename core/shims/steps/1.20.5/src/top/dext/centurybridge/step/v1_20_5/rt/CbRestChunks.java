package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import top.dext.centurybridge.step.v1_20_5.facade.class_3193$class_3724;

/**
 * 1.20.4's chunk loading results on 1.20.5's.
 * <p>
 * 1.20.4 handed a chunk (or level chunk) future's result as {@code Either<chunk, ChunkLoadingFailure>}
 * -- the failure side only ever {@code ChunkLoadingFailure.UNLOADED}, the one implementation 1.20.4
 * had -- and named the unloaded results {@code ChunkHolder.UNLOADED_CHUNK} /
 * {@code UNLOADED_LEVEL_CHUNK} ({@code Either.right(UNLOADED)}) and their completed future
 * {@code UNLOADED_CHUNK_FUTURE}. 1.20.5 hands a {@code ChunkResult} (a success with the chunk, or a
 * failure with a message) under the same erased descriptors, and dropped ChunkLoadingFailure
 * (facade/class_3193$class_3724 stands in for it).
 * <p>
 * A 1.20.5 success is {@code Either.left(chunk)}, any 1.20.5 failure the 1.20.4 unloaded result
 * (1.20.4 knew no other failure); 1.20.4's unloaded constants map onto 1.20.5's, any other 1.20.4
 * failure onto a 1.20.5 failure carrying its text. Futures are converted lazily (thenApply, completing
 * with their source), the same future object for the same source future both ways, so the identity
 * tests 1.20.4 code makes ({@code future == UNLOADED_CHUNK_FUTURE}) hold. Every conversion is adaptive:
 * a value already of the wanted kind passes through.
 */
public final class CbRestChunks {

    /** ChunkHolder.UNLOADED_CHUNK as 1.20.4 held it */
    public static final Either<Object, class_3193$class_3724> UNLOADED_CHUNK = Either.right(class_3193$class_3724.field_16433);
    /** ChunkHolder.UNLOADED_LEVEL_CHUNK as 1.20.4 held it */
    public static final Either<Object, class_3193$class_3724> UNLOADED_LEVEL_CHUNK = Either.right(class_3193$class_3724.field_16433);

    /**
     * 1.20.5 future -> the 1.20.4 future standing for it, and back. Both sides are held weakly (keys
     * and values), so a pair lives exactly as long as someone holds one of its futures.
     */
    private static final Map<CompletableFuture<?>, WeakReference<CompletableFuture<?>>> TO_OLD = new WeakHashMap<>();
    private static final Map<CompletableFuture<?>, WeakReference<CompletableFuture<?>>> TO_NEW = new WeakHashMap<>();

    /** a 1.20.5 ChunkResult as the 1.20.4 Either (see the class comment); an Either passes through */
    public static Object cbEither(Object result) {
        if (!(result instanceof net.minecraft.class_9259<?> r)) {
            return result; // null, or already a 1.20.4 Either
        }
        if (r == net.minecraft.class_3193.field_16427) {
            return UNLOADED_LEVEL_CHUNK;
        }
        if (!r.method_57122()) {
            return UNLOADED_CHUNK;
        }
        return Either.left(r.method_57130(null));
    }

    /** a 1.20.4 Either as the 1.20.5 ChunkResult (see the class comment); a ChunkResult passes through */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object cbResult(Object either) {
        if (!(either instanceof Either e)) {
            return either;
        }
        if (e == UNLOADED_LEVEL_CHUNK) {
            return net.minecraft.class_3193.field_16427;
        }
        if (e == UNLOADED_CHUNK) {
            return net.minecraft.class_3193.field_16426;
        }
        return e.map(chunk -> net.minecraft.class_9259.method_57124(chunk), failure -> failure == class_3193$class_3724.field_16433
            ? net.minecraft.class_3193.field_16426 : net.minecraft.class_9259.method_57125(String.valueOf(failure)));
    }

    /** a 1.20.5 chunk future as the 1.20.4 one: completes with the converted result of the source */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture cbEitherFuture(CompletableFuture future) {
        if (future == null) {
            return null;
        }
        synchronized (TO_OLD) {
            CompletableFuture known = deref(TO_OLD.get(future));
            if (known != null) {
                return known;
            }
            if (TO_NEW.containsKey(future)) {
                return future; // already a 1.20.4 future of ours
            }
            CompletableFuture old = future.thenApply(CbRestChunks::cbEither);
            TO_OLD.put(future, new WeakReference<>(old));
            TO_NEW.put(old, new WeakReference<>(future));
            return old;
        }
    }

    /** a 1.20.4 chunk future as the 1.20.5 one (the inverse of {@link #cbEitherFuture}) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture cbResultFuture(CompletableFuture future) {
        if (future == null) {
            return null;
        }
        synchronized (TO_OLD) {
            CompletableFuture known = deref(TO_NEW.get(future));
            if (known != null) {
                return known;
            }
            if (TO_OLD.containsKey(future)) {
                return future; // already a 1.20.5 future we converted from
            }
            CompletableFuture neu = future.thenApply(CbRestChunks::cbResult);
            TO_NEW.put(future, new WeakReference<>(neu));
            TO_OLD.put(neu, new WeakReference<>(future));
            return neu;
        }
    }

    private static CompletableFuture<?> deref(WeakReference<CompletableFuture<?>> r) {
        return r == null ? null : r.get();
    }

    /** ChunkHolder.getAllFutures(): each (status, future) pair with the future as 1.20.4 had it */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List cbEitherFutures(List pairs) {
        if (pairs == null) {
            return null;
        }
        List out = new ArrayList(pairs.size());
        for (Object o : pairs) {
            Pair p = (Pair) o;
            out.add(Pair.of(p.getFirst(), cbEitherFuture((CompletableFuture) p.getSecond())));
        }
        return out;
    }

    /** the inverse of {@link #cbEitherFutures}, for a 1.20.4 override's answer */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List cbResultFutures(List pairs) {
        if (pairs == null) {
            return null;
        }
        List out = new ArrayList(pairs.size());
        for (Object o : pairs) {
            Pair p = (Pair) o;
            out.add(Pair.of(p.getFirst(), cbResultFuture((CompletableFuture) p.getSecond())));
        }
        return out;
    }

    /** ChunkHolder.UNLOADED_CHUNK (field getter) */
    public static Either field_16426() {
        return UNLOADED_CHUNK;
    }

    /** ChunkHolder.UNLOADED_LEVEL_CHUNK (field getter) */
    public static Either field_16427() {
        return UNLOADED_LEVEL_CHUNK;
    }

    /** ChunkHolder.UNLOADED_CHUNK_FUTURE (field getter): 1.20.5's completed future, converted */
    public static CompletableFuture field_16430() {
        return cbEitherFuture(net.minecraft.class_3193.field_16430);
    }

    private CbRestChunks() {
    }
}
