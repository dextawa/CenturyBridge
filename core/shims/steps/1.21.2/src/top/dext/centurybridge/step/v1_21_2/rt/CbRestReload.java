package top.dext.centurybridge.step.v1_21_2.rt;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

/**
 * 1.21.1's resource reload API on 1.21.2.
 *
 * PreparableReloadListener.reload took two ProfilerFillers, one for the preparation and one for the
 * apply phase (1.21.1's SimpleReloadInstance passed InactiveProfiler for both, its ProfiledReloadInstance
 * a fresh ActiveProfiler for each); 1.21.2's reload has no profiler arguments and its listeners profile
 * on the running thread's profiler (Profiler.get()). A 1.21.1 listener -- a mod class implementing the
 * six-argument reload -- keeps its own method; 1.21.2 calls the four-argument one, which an override
 * hook synthesizes to call it with the running thread's profiler for both phases (what 1.21.2's own
 * listeners profile into). A 1.21.1 call of the six-argument reload goes to the four-argument one when
 * the listener is 1.21.2's, to the listener's own six-argument method when it is a 1.21.1 one.
 */
public final class CbRestReload {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CbRestReload() {
    }

    /** a 1.21.1 reload listener's own reload (the adapter the override hook adds to a mod's listener class) */
    public interface Legacy {
        CompletableFuture<Void> method_25931(net.minecraft.class_3302.class_4045 barrier, net.minecraft.class_3300 manager,
                                             net.minecraft.class_3695 prepProfiler, net.minecraft.class_3695 applyProfiler,
                                             Executor background, Executor game);
    }

    /**
     * 1.21.2's reload of a 1.21.1 listener class (the override hook's bridge): its own six-argument reload,
     * with the running thread's profiler for both phases.
     */
    public static CompletableFuture<Void> reload(Legacy listener, net.minecraft.class_3302.class_4045 barrier,
                                                 net.minecraft.class_3300 manager, Executor background, Executor game) {
        net.minecraft.class_3695 profiler = net.minecraft.class_10209.method_64146();
        return listener.method_25931(barrier, manager, profiler, profiler, background, game);
    }

    /**
     * A 1.21.1 call of reload(barrier, manager, prepProfiler, applyProfiler, background, game) on any
     * listener: a 1.21.1 one runs its own method with the profilers it is given; a 1.21.2 one has no
     * place for them and profiles on the running thread (1.21.2's own behaviour for every listener).
     */
    public static CompletableFuture<Void> reload(net.minecraft.class_3302 listener, net.minecraft.class_3302.class_4045 barrier,
                                                 net.minecraft.class_3300 manager, net.minecraft.class_3695 prepProfiler,
                                                 net.minecraft.class_3695 applyProfiler, Executor background, Executor game) {
        if (listener instanceof Legacy legacy) {
            return legacy.method_25931(barrier, manager, prepProfiler, applyProfiler, background, game);
        }
        return listener.method_25931(barrier, manager, background, game);
    }

    /**
     * A 1.21.1 lambda or method reference implementing PreparableReloadListener (its SAM was the
     * six-argument reload; lambdaAdapters build it as a {@link Legacy}) as 1.21.2's listener: 1.21.2 calls
     * the four-argument reload, which runs the lambda with the running thread's profiler for both phases;
     * a 1.21.1 call of the six-argument reload reaches the lambda itself. getName() is 1.21.1's default,
     * the class's simple name -- the lambda's class, as 1.21.1 answered for a lambda listener.
     */
    public static net.minecraft.class_3302 listener(Legacy lambda) {
        return new LambdaListener(lambda);
    }

    static final class LambdaListener implements net.minecraft.class_3302, Legacy {
        private final Legacy lambda;

        LambdaListener(Legacy lambda) {
            this.lambda = lambda;
        }

        @Override
        public CompletableFuture<Void> method_25931(net.minecraft.class_3302.class_4045 barrier, net.minecraft.class_3300 manager,
                                                    Executor background, Executor game) {
            return reload(this.lambda, barrier, manager, background, game);
        }

        @Override
        public CompletableFuture<Void> method_25931(net.minecraft.class_3302.class_4045 barrier, net.minecraft.class_3300 manager,
                                                    net.minecraft.class_3695 prepProfiler, net.minecraft.class_3695 applyProfiler,
                                                    Executor background, Executor game) {
            return this.lambda.method_25931(barrier, manager, prepProfiler, applyProfiler, background, game);
        }

        @Override
        public String method_22322() {
            return this.lambda.getClass().getSimpleName();
        }
    }

    /** the accessor through which a 1.21.1 listener's super.reload(..) reaches its super's 1.21.2 reload */
    public interface ReloadSuper {
        CompletableFuture<Void> cb$super$method_25931(net.minecraft.class_3302.class_4045 barrier, net.minecraft.class_3300 manager,
                                                      Executor background, Executor game);
    }

    /**
     * A 1.21.1 listener's super.reload(barrier, manager, prepProfiler, applyProfiler, background, game):
     * super's 1.21.2 reload (FontManager's, ParticleEngine's, ResourceManagerReloadListener's default ..),
     * which profiles on the running thread where 1.21.1's took the two profilers.
     */
    public static CompletableFuture<Void> superReload(ReloadSuper self, net.minecraft.class_3302.class_4045 barrier,
                                                      net.minecraft.class_3300 manager, net.minecraft.class_3695 prepProfiler,
                                                      net.minecraft.class_3695 applyProfiler, Executor background, Executor game) {
        return self.cb$super$method_25931(barrier, manager, background, game);
    }

    /**
     * SimpleJsonResourceReloadListener.scanDirectory(manager, directory, gson, output) of 1.21.1: every
     * "&lt;directory&gt;/**.json" of the resource manager, parsed with the given Gson as a JsonElement, into
     * output by its id; a duplicate id is an error, a file that does not parse is logged and skipped.
     * 1.21.1's body (GsonHelper.fromJson, the same log message).
     */
    public static void scanDirectory(net.minecraft.class_3300 manager, String directory, Gson gson, Map<net.minecraft.class_2960, JsonElement> output) {
        net.minecraft.class_7654 lister = net.minecraft.class_7654.method_45114(directory);
        for (Map.Entry<net.minecraft.class_2960, net.minecraft.class_3298> e : lister.method_45113(manager).entrySet()) {
            net.minecraft.class_2960 file = e.getKey();
            net.minecraft.class_2960 id = lister.method_45115(file);
            try (BufferedReader reader = e.getValue().method_43039()) {
                JsonElement json = net.minecraft.class_3518.method_15276(gson, reader, JsonElement.class);
                JsonElement previous = output.put(id, json);
                if (previous != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException ex) {
                LOGGER.error("Couldn't parse data file {} from {}", id, file, ex);
            }
        }
    }
}
