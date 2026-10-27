package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.server.MinecraftServer;

/**
 * Values 26.1.2 implied where 26.2 asks for them (frameDefaults: a 26.1.2 override calling its own old method has no
 * 26.2 caller to take them from).
 */
public final class CbS26Server {
    private CbS26Server() {}

    /**
     * MinecraftServer.publishServer's scope: 26.1.2's publishServer(gameType, cheats, port) opened the game to LAN, the
     * only scope it had; 26.2's PublishCommand passes MultiplayerScope.LAN where 26.1.2's called the old method
     */
    public static MinecraftServer.MultiplayerScope lanScope(MinecraftServer self) {
        return MinecraftServer.MultiplayerScope.LAN;
    }

    /** the same scope for a 26.1.2 call of publishServer(gameType, cheats, port) (callReshapes push) */
    public static MinecraftServer.MultiplayerScope lan() {
        return MinecraftServer.MultiplayerScope.LAN;
    }

    /**
     * 26.1.2 MetricSampler.create(name, category, context, function): new MetricSamplerBuilder(name, category, function,
     * context).build(). 26.2's builder is the same with a sampling phase, END_TICK unless set (26.1.2 had one phase, the
     * tick's end; 26.2's create(name, category, supplier) also uses END_TICK).
     */
    public static <T> net.minecraft.util.profiling.metrics.MetricSampler createSampler(String name,
            net.minecraft.util.profiling.metrics.MetricCategory category, T context, java.util.function.ToDoubleFunction<T> function) {
        return new net.minecraft.util.profiling.metrics.MetricSampler.MetricSamplerBuilder<>(name, category, function, context).build();
    }

    /** superCtorReshapes push: the sampling phase of a 26.1.2 MetricSampler (26.1.2 sampled at the tick's end only) */
    public static net.minecraft.util.profiling.metrics.MetricSampler.SamplingPhase endTick() {
        return net.minecraft.util.profiling.metrics.MetricSampler.SamplingPhase.END_TICK;
    }
}
