package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * 1.21.1's server / util API on 1.21.2 (common side): the spawn-animals / spawn-npcs switches, the
 * two-flag spawn settings, the profiler and executor getters, Util.wrapThreadWithTaskName, Mth.color,
 * ProfilerFiller.tee, SoundEvent.getLocation and the sound constants 1.21.2 holder-wrapped or dropped.
 */
public final class CbRestServer {

    private CbRestServer() {
    }

    // ------------------------------------------------------------------ spawn switches

    /**
     * What a 1.21.1 MinecraftServer subclass (or the dedicated server's spawn-animals / spawn-npcs
     * properties) says about passive and npc spawning, read by the despawn check 1.21.1's ServerLevel
     * made every tick (CbRestServerLevelMixin): an Animal or WaterAnimal is discarded when the server
     * does not spawn animals, an Npc when npcs are off.
     */
    public interface SpawnFlags {
        /** MinecraftServer.isSpawningAnimals() */
        boolean method_3796();

        /** MinecraftServer.areNpcsEnabled() */
        boolean method_3736();
    }

    /**
     * 1.21.1's ServerLevel.shouldDiscardEntity: true when the server says the entity's kind does not
     * spawn here any more (animals off for an Animal / WaterAnimal, npcs off for an Npc).
     */
    public static boolean shouldDiscard(net.minecraft.server.MinecraftServer server, net.minecraft.class_1297 entity) {
        SpawnFlags flags = (SpawnFlags) server;
        if (!flags.method_3796() && (entity instanceof net.minecraft.class_1429 || entity instanceof net.minecraft.class_1480)) {
            return true;
        }
        return !flags.method_3736() && entity instanceof net.minecraft.class_1655;
    }

    /** DedicatedServerProperties.spawnAnimals ("spawn-animals", read as 1.21.1 read it) */
    public static boolean spawnAnimals(net.minecraft.class_3806 props) {
        return ((DedicatedFlags) props).cb$spawnAnimals();
    }

    /** DedicatedServerProperties.spawnNpcs ("spawn-npcs") */
    public static boolean spawnNpcs(net.minecraft.class_3806 props) {
        return ((DedicatedFlags) props).cb$spawnNpcs();
    }

    /** the two 1.21.1 server.properties entries 1.21.2 no longer reads (CbRestServerPropertiesMixin) */
    public interface DedicatedFlags {
        boolean cb$spawnAnimals();

        boolean cb$spawnNpcs();
    }

    /** ChunkSource.setSpawnSettings(spawnEnemies, spawnFriendlies) of 1.21.1, on every chunk source (covers) */
    public interface ChunkSpawnSettings {
        void method_12128(boolean enemies, boolean friendlies);
    }

    /** Level.setSpawnSettings(spawnEnemies, spawnFriendlies) of 1.21.1, on every level (a cover) */
    public interface LevelSpawnSettings {
        void method_8424(boolean enemies, boolean friendlies);
    }

    // ------------------------------------------------------------------ profiler, executors, threads

    /**
     * MinecraftServer.getProfiler(): the profiler of the running thread. 1.21.2 keeps the active profiler
     * thread-local (Profiler.get()) where 1.21.1 kept it on the server; on the server thread they are the
     * same profiler.
     */
    public static net.minecraft.class_3695 profiler(net.minecraft.server.MinecraftServer server) {
        return net.minecraft.class_10209.method_64146();
    }

    /** the running thread's profiler (Minecraft.getProfiler()'s body, CbRestMinecraftMixin) */
    public static net.minecraft.class_3695 threadProfiler() {
        return net.minecraft.class_10209.method_64146();
    }

    /** ProfilerFiller.tee(a, b): 1.21.2's combine, the same body */
    public static net.minecraft.class_3695 tee(net.minecraft.class_3695 a, net.minecraft.class_3695 b) {
        return net.minecraft.class_3695.method_64154(a, b);
    }

    /** Util.backgroundExecutor() as the ExecutorService 1.21.1 answered */
    public static ExecutorService backgroundExecutor() {
        return net.minecraft.class_156.method_18349().comp_3205();
    }

    /** Util.ioPool() */
    public static ExecutorService ioPool() {
        return net.minecraft.class_156.method_27958().comp_3205();
    }

    /** Util.nonCriticalIoPool() */
    public static ExecutorService nonCriticalIoPool() {
        return net.minecraft.class_156.method_55473().comp_3205();
    }

    /** Util.wrapThreadWithTaskName(name, runnable): 1.21.1's body (renames the thread only in an IDE run) */
    public static Runnable wrapThreadWithTaskName(String name, Runnable task) {
        if (!net.minecraft.class_155.field_1125) {
            return task;
        }
        return () -> {
            Thread thread = Thread.currentThread();
            String old = thread.getName();
            thread.setName(name);
            try {
                task.run();
            } finally {
                thread.setName(old);
            }
        };
    }

    /** Util.wrapThreadWithTaskName(name, supplier) */
    public static <V> Supplier<V> wrapThreadWithTaskName(String name, Supplier<V> task) {
        if (!net.minecraft.class_155.field_1125) {
            return task;
        }
        return () -> {
            Thread thread = Thread.currentThread();
            String old = thread.getName();
            thread.setName(name);
            try {
                return task.get();
            } finally {
                thread.setName(old);
            }
        };
    }

    // ------------------------------------------------------------------ small values

    /** Mth.color(r, g, b): alpha 0, as 1.21.1 packed it */
    public static int color(float r, float g, float b) {
        return net.minecraft.class_9848.method_61324(0, net.minecraft.class_3532.method_15375(r * 255.0F),
            net.minecraft.class_3532.method_15375(g * 255.0F), net.minecraft.class_3532.method_15375(b * 255.0F));
    }

    /** SoundEvent.getLocation(): the record's location */
    public static net.minecraft.class_2960 soundLocation(net.minecraft.class_3414 sound) {
        return sound.comp_3319();
    }

    /** SoundEvents.GENERIC_DRINK (a SoundEvent in 1.21.1, a registered holder in 1.21.2) */
    public static net.minecraft.class_3414 field_20613() {
        return net.minecraft.class_3417.field_20613.comp_349();
    }

    /** SoundEvents.GENERIC_EAT */
    public static net.minecraft.class_3414 field_20614() {
        return net.minecraft.class_3417.field_20614.comp_349();
    }

    /** SoundEvents.HONEY_DRINK */
    public static net.minecraft.class_3414 field_20615() {
        return net.minecraft.class_3417.field_20615.comp_349();
    }

    /** SoundEvents.HORSE_ARMOR */
    public static net.minecraft.class_3414 field_15141() {
        return net.minecraft.class_3417.field_15141.comp_349();
    }

    /** SoundEvents.OMINOUS_BOTTLE_DISPOSE */
    public static net.minecraft.class_3414 field_50103() {
        return net.minecraft.class_3417.field_50103.comp_349();
    }

    /**
     * SoundEvents.GOAT_HORN_PLAY ("item.goat_horn.play") and GOAT_SCREAMING_HORN_BREAK
     * ("entity.goat.screaming.horn_break"): 1.21.1 registered these ids although its sounds.json defines
     * neither (nothing plays them), and 1.21.2 no longer registers them. The same events, unregistered: a
     * sound event is its id, and playing one the sound manager has no definition for is silent, as it was.
     */
    public static final net.minecraft.class_3414 GOAT_HORN_PLAY =
        net.minecraft.class_3414.method_47908(net.minecraft.class_2960.method_60656("item.goat_horn.play"));

    public static final net.minecraft.class_3414 GOAT_SCREAMING_HORN_BREAK =
        net.minecraft.class_3414.method_47908(net.minecraft.class_2960.method_60656("entity.goat.screaming.horn_break"));

    /** SoundEvents.GOAT_HORN_PLAY */
    public static net.minecraft.class_3414 field_39025() {
        return GOAT_HORN_PLAY;
    }

    /** SoundEvents.GOAT_SCREAMING_HORN_BREAK */
    public static net.minecraft.class_3414 field_39026() {
        return GOAT_SCREAMING_HORN_BREAK;
    }

    // ------------------------------------------------------------------ constants 1.21.2 dropped

    /** SharedConstants.DEBUG_RESOURCE_LOAD_TIMES: false in 1.21.1 (a debug switch of the dev build) */
    public static boolean field_29694() {
        return false;
    }

    /** SharedConstants.INGAME_DEBUG_OUTPUT: false in 1.21.1 */
    public static boolean field_29720() {
        return false;
    }

    /** ClientboundContainerSetSlotPacket.CARRIED_ITEM: the container id of the carried stack, -1 */
    public static int field_33324() {
        return -1;
    }

    /** ClientboundContainerSetSlotPacket.PLAYER_INVENTORY: the container id of the player inventory, -2 */
    public static int field_33325() {
        return -2;
    }

    /** ServerboundEditBookPacket.MAX_BYTES_PER_CHAR: 4 */
    public static int field_34038() {
        return 4;
    }

    /** ServerPlayer.INTERACTION_DISTANCE_VERIFICATION_BUFFER: 1.0 (1.21.2 split it per block 1.0 / entity 3.0) */
    public static double field_47708() {
        return 1.0;
    }

    /**
     * MetricCategory.MAIL_BOXES: the category 1.21.1's ProcessorMailbox queues were charted under; 1.21.2's
     * successors of the mailbox (the consecutive executors) chart under CONSECUTIVE_EXECUTORS.
     */
    public static net.minecraft.class_5949 field_29552() {
        return net.minecraft.class_5949.field_54068;
    }
}
