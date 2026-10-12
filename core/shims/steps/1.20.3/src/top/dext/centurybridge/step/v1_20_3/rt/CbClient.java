package top.dext.centurybridge.step.v1_20_3.rt;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.2 client API (Minecraft, Timer, HotbarManager, sound listeners and subtitles, the debug charts,
 * the section render dispatcher) on 1.20.3.
 */
@Environment(EnvType.CLIENT)
public final class CbClient {

    // ------------------------------------------------------------------ Minecraft (class_310)

    /** Minecraft.crash(CrashReport) took the game from Minecraft.getInstance() in 1.20.2 */
    public static net.minecraft.class_310 cbMinecraft() {
        return net.minecraft.class_310.method_1551();
    }

    /** ... and wrote the report under its game directory */
    public static File cbGameDir() {
        return net.minecraft.class_310.method_1551().field_1697;
    }

    /**
     * Minecraft.doWorldLoad(levelId, access, packs, stem, newWorld) of 1.20.2 is (access, packs, stem,
     * newWorld) since 1.20.3, which takes the level id from the access (1.20.2 used the argument only for
     * the world-load telemetry event and the crash report). A 1.20.2 Minecraft subclass's own override is
     * reached from the new shape with the access's level id as its first argument.
     */
    public interface CbLegacyWorldLoad {
        void method_29610(String levelId, net.minecraft.class_32.class_5143 access, net.minecraft.class_3283 packs,
                          net.minecraft.class_6904 stem, boolean newWorld);
    }

    public static void cbDoWorldLoad(CbLegacyWorldLoad mc, net.minecraft.class_32.class_5143 access,
                                     net.minecraft.class_3283 packs, net.minecraft.class_6904 stem, boolean newWorld) {
        mc.method_29610(access.method_27005(), access, packs, stem, newWorld);
    }

    // ------------------------------------------------------------------ Timer (class_317), HotbarManager (class_302)

    /**
     * Timer(float ticksPerSecond, long now) of 1.20.2 is (ticksPerSecond, now, targetMsptProvider) since
     * 1.20.3, which divides by the provider's answer for the configured ms-per-tick: the identity is the
     * fixed rate 1.20.2 ran at.
     */
    public static FloatUnaryOperator cbFixedTickRate() {
        return msPerTick -> msPerTick;
    }

    /** HotbarManager(File dir, DataFixer): the same directory as the Path 1.20.3 takes ("hotbar.nbt" in it) */
    public static Path cbPath(File dir) {
        return dir.toPath();
    }

    // ------------------------------------------------------------------ sounds (class_1145, class_359)

    /**
     * SoundEventListener.onPlaySound(SoundInstance, WeighedSoundEvents) gained the sound's audible range in
     * 1.20.3 (the subtitle overlay drops a subtitle outside it). 1.20.2 had no range: a direct 1.20.2-style
     * call passes an unlimited one, which is what 1.20.3 itself passes for a sound heard everywhere.
     */
    public static float cbUnlimitedRange() {
        return Float.POSITIVE_INFINITY;
    }

    /** a 1.20.2 SoundEventListener lambda or method reference: the two-argument method, under its name */
    @FunctionalInterface
    public interface CbSoundEventListener {
        void method_4884(net.minecraft.class_1113 sound, net.minecraft.class_1146 events);
    }

    /** the vanilla listener 1.20.3 calls, answering in the 1.20.2 lambda's terms (the range has no place there) */
    public static net.minecraft.class_1145 cbSoundEventListener(CbSoundEventListener listener) {
        return (sound, events, range) -> listener.method_4884(sound, events);
    }

    // ------------------------------------------------------------------ debug charts (class_8759)

    /**
     * TpsDebugChart(Font, SampleLogger) of 1.20.2 drew against the fixed 20 TPS (50 ms) target; 1.20.3 asks
     * a supplier for the target ms per tick.
     */
    public static Supplier<Float> cbFixedMspt() {
        return () -> 50.0F;
    }

    // ------------------------------------------------------------------ SectionRenderDispatcher (class_846)

    /**
     * SectionRenderDispatcher(level, renderer, executor, is64Bit, fixedBuffers) of 1.20.2 sized its own
     * pool of buffer packs from the memory, the processor count and is64Bit; 1.20.3 takes the pool and the
     * fixed pack from a RenderBuffers. A RenderBuffers with 1.20.2's pool size is made here; the fixed pack
     * the call passed is put back on the dispatcher after construction ({@link #cbSectionDispatcherBuilt}).
     */
    public static net.minecraft.class_4599 cbRenderBuffers(boolean is64Bit, net.minecraft.class_750 fixedBuffers) {
        int perPack = net.minecraft.class_1921.method_22720().stream().mapToInt(net.minecraft.class_1921::method_22722).sum();
        int byMemory = Math.max(1, (int) (Runtime.getRuntime().maxMemory() * 0.3) / (perPack * 4) - 1);
        int cpus = Runtime.getRuntime().availableProcessors();
        int threads = is64Bit ? cpus : Math.min(cpus, 4);
        return new net.minecraft.class_4599(Math.max(1, Math.min(threads, byMemory)));
    }

    public static void cbSectionDispatcherBuilt(net.minecraft.class_846 dispatcher, net.minecraft.class_638 level,
                                                net.minecraft.class_761 renderer, java.util.concurrent.Executor executor,
                                                boolean is64Bit, net.minecraft.class_750 fixedBuffers) {
        ((top.dext.centurybridge.step.v1_20_3.mixin.CbSectionRenderDispatcherAccess) dispatcher).cb$setFixedBuffers(fixedBuffers);
    }

    /**
     * RenderBuffers() of 1.20.2 held the fixed buffers only; the section builders' packs were the
     * SectionRenderDispatcher's own. 1.20.3's RenderBuffers(maxSectionBuilders) always allocates that pool
     * (SectionBufferBuilderPool.allocate keeps at least one pack): a 1.20.2 call gets the smallest one.
     */
    public static int cbNoSectionPool() {
        return 1;
    }

    /** RenderType.MEDIUM_BUFFER_SIZE (field_32773), public in 1.20.2 and gone in 1.20.3 */
    public static final int field_32773 = 262144;

    private CbClient() {
    }
}
