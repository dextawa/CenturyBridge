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
        if (server instanceof LegacyProfiler legacy) {
            return legacy.method_16044();
        }
        return net.minecraft.class_10209.method_64146();
    }

    /** getProfiler() of a 1.21.1 MinecraftServer subclass that overrides it (1.21.2's is private) */
    public interface LegacyProfiler {
        net.minecraft.class_3695 method_16044();
    }

    /** MinecraftServer.getProfiler() itself, a 1.21.1 subclass's super call: the running thread's profiler */
    public static net.minecraft.class_3695 superProfiler(net.minecraft.server.MinecraftServer server) {
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

    // ------------------------------------------------------------------ function tags (ServerFunctionLibrary / ServerFunctionManager)

    /** ServerFunctionLibrary.getTag(id): 1.21.2 answers a List (still the tag's functions); 1.21.1 callers take a Collection */
    public static java.util.Collection<net.minecraft.class_2158<net.minecraft.class_2168>> libraryTag(net.minecraft.class_5349 library,
            net.minecraft.class_2960 id) {
        return library.method_29459(id);
    }

    /** ServerFunctionManager.getTag(id), as above */
    public static java.util.Collection<net.minecraft.class_2158<net.minecraft.class_2168>> managerTag(net.minecraft.class_2991 manager,
            net.minecraft.class_2960 id) {
        return manager.method_29462(id);
    }

    /**
     * a 1.21.1 override's Collection as the List 1.21.2's getTag answers: the same list when it is one, else
     * an unmodifiable list of its elements in iteration order (1.21.1's own answer was the tag's list)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.List tagList(java.util.Collection tag) {
        if (tag == null || tag instanceof java.util.List) {
            return (java.util.List) tag;
        }
        return java.util.Collections.unmodifiableList(new java.util.ArrayList(tag));
    }

    /** super's 1.21.2 ServerFunctionLibrary.getTag, from a 1.21.1 subclass's super.getTag(id) */
    public interface LibraryTagSuper {
        java.util.List<net.minecraft.class_2158<net.minecraft.class_2168>> cb$super$method_29459(net.minecraft.class_2960 id);
    }

    /** super's 1.21.2 ServerFunctionManager.getTag, from a 1.21.1 subclass's super.getTag(id) */
    public interface ManagerTagSuper {
        java.util.List<net.minecraft.class_2158<net.minecraft.class_2168>> cb$super$method_29462(net.minecraft.class_2960 id);
    }

    public static java.util.Collection<net.minecraft.class_2158<net.minecraft.class_2168>> superLibraryTag(LibraryTagSuper self,
            net.minecraft.class_2960 id) {
        return self.cb$super$method_29459(id);
    }

    public static java.util.Collection<net.minecraft.class_2158<net.minecraft.class_2168>> superManagerTag(ManagerTagSuper self,
            net.minecraft.class_2960 id) {
        return self.cb$super$method_29462(id);
    }

    // ------------------------------------------------------------------ ChunkHolder change notifications

    /**
     * the answer of a 1.21.1 ChunkHolder subclass's blockChanged / sectionLightChanged (void in 1.21.1) to
     * 1.21.2's question "should this holder be broadcast": yes. 1.21.1 broadcast every holder each tick;
     * 1.21.2 broadcasts the holders that said yes, and a holder's broadcastChanges sends nothing when no
     * section changed, so a yes is never wrong where a no could drop the change.
     */
    public static boolean broadcastAfterChange() {
        return true;
    }

    /** super's 1.21.2 blockChanged(pos), from a 1.21.1 subclass's super.blockChanged(pos) */
    public interface BlockChangedSuper {
        boolean cb$super$method_14002(net.minecraft.class_2338 pos);
    }

    /** super's 1.21.2 sectionLightChanged(layer, y), from a 1.21.1 subclass's super call */
    public interface LightChangedSuper {
        boolean cb$super$method_14012(net.minecraft.class_1944 layer, int sectionY);
    }

    public static void superBlockChanged(BlockChangedSuper self, net.minecraft.class_2338 pos) {
        self.cb$super$method_14002(pos);
    }

    public static void superLightChanged(LightChangedSuper self, net.minecraft.class_1944 layer, int sectionY) {
        self.cb$super$method_14012(layer, sectionY);
    }

    /**
     * GenerationChunkHolder.addSaveDependency(future), abstract in 1.21.2, of a 1.21.1 class extending
     * GenerationChunkHolder directly: 1.21.1 had no save dependencies on a generation holder (only
     * ChunkHolder, which implements it, chained its saves), so such a holder's saves wait on nothing.
     */
    public static void addSaveDependency(net.minecraft.class_9761 holder, java.util.concurrent.CompletableFuture<?> future) {
    }

    // ------------------------------------------------------------------ LootContextParamSet (class_176)

    /** LootContextParamSet.isAllowed(param): the param among the allowed ones (1.21.2's getAllowed) */
    public static boolean isAllowed(net.minecraft.class_176 set, net.minecraft.class_169<?> param) {
        return set.method_777().contains(param);
    }

    /** LootContextParamSet.validateUser(context, user): reported to the context's reporter, as 1.21.1 */
    public static void validateUser(net.minecraft.class_176 set, net.minecraft.class_58 context, net.minecraft.class_46 user) {
        validateUser(set, context.method_60288(), user);
    }

    /**
     * LootContextParamSet.validateUser(reporter, user): the parameters the user reads that this set does
     * not allow, reported with 1.21.1's message (1.21.2 checks a user against its ValidationContext's own set)
     */
    public static void validateUser(net.minecraft.class_176 set, net.minecraft.class_8942 reporter, net.minecraft.class_46 user) {
        java.util.Set<net.minecraft.class_169<?>> missing = com.google.common.collect.Sets.difference(user.method_293(), set.method_777());
        if (!missing.isEmpty()) {
            reporter.method_54947("Parameters " + missing + " are not provided in this context");
        }
    }

    /** LootContextParamSet.builder() */
    public static net.minecraft.class_176.class_177 method_35554() {
        return new net.minecraft.class_176.class_177();
    }

    // ------------------------------------------------------------------ text filter

    /** LegacyTextFilter.createFromConfig(config): 1.21.2's builds the same LegacyTextFilter (or null), typed wider */
    public static net.minecraft.class_5514 method_33805(String config) {
        return (net.minecraft.class_5514) net.minecraft.class_5514.method_33805(config);
    }

    // ------------------------------------------------------------------ data pack loading

    /**
     * RegistryDataLoader.load(resources, context, registries): 1.21.2 takes the context as registry lookups;
     * 1.21.1's context access answers them as its registries (each a lookup of itself, tags as bound)
     */
    public static net.minecraft.class_5455.class_6890 method_56515(net.minecraft.class_3300 resources, net.minecraft.class_5455 context,
            java.util.List<net.minecraft.class_7655.class_7657<?>> registries) {
        return net.minecraft.class_7655.method_56515(resources, context.method_62678().toList(), registries);
    }

    /** RegistryDataLoader.load(networkContents, resources, context, registries), as above */
    public static net.minecraft.class_5455.class_6890 method_56519(
            java.util.Map<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>, net.minecraft.class_7655.class_9841> contents,
            net.minecraft.class_5912 resources, net.minecraft.class_5455 context, java.util.List<net.minecraft.class_7655.class_7657<?>> registries) {
        return net.minecraft.class_7655.method_56519(contents, resources, context.method_62678().toList(), registries);
    }

    /**
     * ReloadableServerRegistries.reload(registries, resources, executor): 1.21.2 also takes the tags still to be
     * bound and answers the layers with the lookup they were validated against; 1.21.1 reloaded against the
     * registries' tags as bound (no pending tags) and answered the layers
     */
    public static java.util.concurrent.CompletableFuture<net.minecraft.class_7780<net.minecraft.class_7659>> method_58284(
            net.minecraft.class_7780<net.minecraft.class_7659> registries, net.minecraft.class_3300 resources, java.util.concurrent.Executor executor) {
        return net.minecraft.class_9383.method_58284(registries, java.util.List.of(), resources, executor).thenApply(r -> r.comp_2898());
    }

    /**
     * ReloadableServerRegistries.Holder.get(): the frozen registries the holder was made of. 1.21.2's
     * ReloadableServerResources makes it of the reloaded layers' composite access, as 1.21.1 did, and keeps it as a
     * lookup provider; a holder made of a plain provider has no registry access to answer
     */
    public static net.minecraft.class_5455.class_6890 holderRegistries(net.minecraft.class_9383.class_9385 holder) {
        if (holder instanceof LegacyHolderGet legacy) {
            return legacy.method_58289();
        }
        return superHolderRegistries(holder);
    }

    /** get() of a 1.21.1 ReloadableServerRegistries.Holder subclass that overrides it (1.21.2 has no get to override) */
    public interface LegacyHolderGet {
        net.minecraft.class_5455.class_6890 method_58289();
    }

    /** ReloadableServerRegistries.Holder.get() itself (also a 1.21.1 subclass's super call) */
    public static net.minecraft.class_5455.class_6890 superHolderRegistries(net.minecraft.class_9383.class_9385 holder) {
        net.minecraft.class_7225.class_7874 registries = ((top.dext.centurybridge.step.v1_21_2.mixin.CbRestRegistriesHolderAccessor) holder).cb$registries();
        if (registries instanceof net.minecraft.class_5455.class_6890 frozen) {
            return frozen;
        }
        throw new UnsupportedOperationException("CenturyBridge: ReloadableServerRegistries.Holder.get() of a holder made of a"
            + " lookup provider that is not a frozen registry access (1.21.2 allows that; 1.21.1 holders were always made of one)");
    }

    /**
     * ReloadableServerResources.loadResources(resources, registries, features, commands, permissionLevel, background, main):
     * 1.21.1 loaded the tags among its reload listeners; 1.21.2 takes them loaded, as MinecraftServer and WorldLoader
     * load them (TagLoader.loadTagsForExistingRegistries over the composite access), and binds them in
     * updateStaticRegistryTags, 1.21.1's updateRegistryTags
     */
    public static java.util.concurrent.CompletableFuture<net.minecraft.class_5350> method_29466(net.minecraft.class_3300 resources,
            net.minecraft.class_7780<net.minecraft.class_7659> registries, net.minecraft.class_7699 features,
            net.minecraft.class_2170.class_5364 commands, int permissionLevel, java.util.concurrent.Executor background,
            java.util.concurrent.Executor main) {
        java.util.List<net.minecraft.class_2378.class_10106<?>> tags = net.minecraft.class_3503.method_61307(resources, registries.method_45926());
        return net.minecraft.class_5350.method_29466(resources, registries, tags, features, commands, permissionLevel, background, main);
    }

    /** ReloadableServerResources.updateRegistryTags(): 1.21.2's updateStaticRegistryTags, which binds the loaded tags */
    public static void updateRegistryTags(net.minecraft.class_5350 resources) {
        resources.method_61248();
    }

    /**
     * WorldLoader.DataLoadContext.datapackWorldgen(): 1.21.1 answered the frozen worldgen registries; 1.21.2 keeps a
     * lookup provider, which WorldLoader makes of those registries' lookups with their pending tags. The context's
     * own provider when it is a registry access (a 1.21.1 caller's), else the access 1.21.1's WorldLoader put there,
     * recorded as 1.21.2's WorldLoader.load makes the context (CbRestWorldLoaderMixin)
     */
    public static net.minecraft.class_5455.class_6890 datapackWorldgen(net.minecraft.class_7237.class_7660 context) {
        net.minecraft.class_7225.class_7874 worldgen = context.comp_989();
        if (worldgen instanceof net.minecraft.class_5455.class_6890 frozen) {
            return frozen;
        }
        net.minecraft.class_5455.class_6890 recorded = WORLDGEN_ACCESS.get(worldgen);
        if (recorded != null) {
            return recorded;
        }
        throw new UnsupportedOperationException("CenturyBridge: WorldLoader.DataLoadContext.datapackWorldgen() as a frozen"
            + " registry access: this context was not made by 1.21.2's WorldLoader.load, which keeps only registry lookups with pending tags there");
    }

    /** the frozen access 1.21.1's WorldLoader put in a DataLoadContext, by the lookup provider 1.21.2 puts there */
    private static final java.util.Map<net.minecraft.class_7225.class_7874, net.minecraft.class_5455.class_6890> WORLDGEN_ACCESS =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /**
     * WorldLoader.load (CbRestWorldLoaderMixin): records, for the provider it hands the world-data supplier, the
     * access 1.21.1 handed there -- the layered access with the loaded worldgen registries in place, as the access
     * for loading dimensions (the static and worldgen layers, frozen)
     */
    public static void recordWorldgenAccess(net.minecraft.class_7225.class_7874 provider, net.minecraft.class_7780<net.minecraft.class_7659> layers,
                                            net.minecraft.class_5455.class_6890 worldgen) {
        WORLDGEN_ACCESS.put(provider, layers.method_45930(net.minecraft.class_7659.field_39972, worldgen).method_45935(net.minecraft.class_7659.field_39973));
    }

    /**
     * WorldOpenFlows.createFreshLevel(name, settings, options, dimensions, screen): 1.21.1's dimensions function
     * took the datapack worldgen access; 1.21.2 hands it the lookup provider WorldLoader made in its place, so it
     * gets the access that provider stands for (a registry access passes as it is)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.function.Function worldgenDimensions(java.util.function.Function dimensions) {
        return provider -> dimensions.apply(provider instanceof net.minecraft.class_5455 access ? access
            : datapackWorldgenOf((net.minecraft.class_7225.class_7874) provider));
    }

    /**
     * createFreshLevel's dimensions function as a 1.21.1 override reads it: 1.21.2's function of a lookup provider
     * applies to the registry accesses the override hands it (each is a lookup provider), so it passes as it is
     */
    @SuppressWarnings("rawtypes")
    public static java.util.function.Function legacyDimensions(java.util.function.Function dimensions) {
        return dimensions;
    }

    private static net.minecraft.class_5455.class_6890 datapackWorldgenOf(net.minecraft.class_7225.class_7874 provider) {
        net.minecraft.class_5455.class_6890 recorded = WORLDGEN_ACCESS.get(provider);
        if (recorded == null) {
            throw new UnsupportedOperationException("CenturyBridge: a 1.21.1 world dimensions function asked with a lookup provider"
                + " 1.21.2's WorldLoader.load did not make: it has no registry access to stand for");
        }
        return recorded;
    }

    /**
     * ServerPlayer.awardRecipesByKey(list), call side: 1.21.1 named the recipes by id, 1.21.2 by ResourceKey in
     * the recipe registry -- each id becomes that key (a key passes unchanged: the converters undo each other)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.List recipeKeys(java.util.List recipes) {
        java.util.List out = new java.util.ArrayList(recipes.size());
        for (Object o : recipes) {
            out.add(o instanceof net.minecraft.class_2960 id ? net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_52178, id) : o);
        }
        return out;
    }

    /** ServerPlayer.awardRecipesByKey(list), override side: the keys 1.21.2 passes as the ids a 1.21.1 override reads */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.List recipeIds(java.util.List recipes) {
        java.util.List out = new java.util.ArrayList(recipes.size());
        for (Object o : recipes) {
            out.add(o instanceof net.minecraft.class_5321<?> key ? key.method_29177() : o);
        }
        return out;
    }

    // ------------------------------------------------------------------ JSON reload listeners

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private static top.dext.centurybridge.step.v1_21_2.mixin.CbRestJsonListenerAccessor json(net.minecraft.class_4309<?> listener) {
        return (top.dext.centurybridge.step.v1_21_2.mixin.CbRestJsonListenerAccessor) (Object) listener;
    }

    /**
     * A SimpleJsonResourceReloadListener's prepared map as 1.21.1 had it -- each file's JSON -- made from
     * 1.21.2's parsed values: it keeps the JSON ops it was encoded with and the value each untouched entry
     * stands for, so handing it back gives 1.21.2 those same values.
     */
    static final class JsonView extends java.util.HashMap<net.minecraft.class_2960, com.google.gson.JsonElement> {
        final com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> ops;
        final java.util.IdentityHashMap<com.google.gson.JsonElement, Object> parsed = new java.util.IdentityHashMap<>();

        JsonView(com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> ops) {
            this.ops = ops;
        }
    }

    /**
     * 1.21.2's parsed values of a JSON listener as 1.21.1's JSON: each value encoded with the listener's own
     * codec and ops (a JSON value -- what a 1.21.1 listener built through the gson constructor holds -- passes
     * as it is)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Map toJson(net.minecraft.class_4309 listener, java.util.Map values) {
        com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> ops = json(listener).cb$ops();
        com.mojang.serialization.Codec codec = json(listener).cb$codec();
        JsonView out = new JsonView(ops);
        values.forEach((id, value) -> {
            if (value instanceof com.google.gson.JsonElement element) {
                out.put((net.minecraft.class_2960) id, element);
            } else {
                com.google.gson.JsonElement element = (com.google.gson.JsonElement) codec.encodeStart(ops, value).getOrThrow();
                out.parsed.put(element, value);
                out.put((net.minecraft.class_2960) id, element);
            }
        });
        return out;
    }

    /**
     * 1.21.1's JSON of a listener's files as 1.21.2's parsed values: each JSON parsed with the listener's codec
     * and ops, as 1.21.2's prepare parses a file (a value that does not parse is logged and left out, as there);
     * entries of a {@link JsonView} left untouched give back the value they stand for
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Map fromJson(net.minecraft.class_4309 listener, java.util.Map jsonMap) {
        com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> ops = json(listener).cb$ops();
        com.mojang.serialization.Codec codec = json(listener).cb$codec();
        JsonView view = jsonMap instanceof JsonView v ? v : null;
        java.util.Map out = new java.util.HashMap();
        for (Object o : jsonMap.entrySet()) {
            java.util.Map.Entry e = (java.util.Map.Entry) o;
            Object value = e.getValue();
            Object same = view != null ? view.parsed.get(value) : null;
            if (same != null || !(value instanceof com.google.gson.JsonElement)) {
                out.put(e.getKey(), same != null ? same : value);
                continue;
            }
            com.mojang.serialization.DataResult r = codec.parse(ops, value);
            r.ifSuccess(v -> out.put(e.getKey(), v)).ifError(err -> LOGGER.error("Couldn't parse data file '{}': {}", e.getKey(), err));
        }
        return out;
    }

    /**
     * super.prepare(resources, profiler) of a 1.21.1 JSON listener: 1.21.2's SimpleJsonResourceReloadListener.prepare
     * (its files scanned with its codec and ops), answered as 1.21.1's map of each file's JSON
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Map superPrepare(net.minecraft.class_4309 listener, net.minecraft.class_3300 resources, net.minecraft.class_3695 profiler) {
        java.util.Map values = new java.util.HashMap();
        net.minecraft.class_4309.method_51148(resources, json(listener).cb$directory(), json(listener).cb$ops(), json(listener).cb$codec(), values);
        return toJson(listener, values);
    }

    /** prepare(resources, profiler) of a 1.21.1 JSON listener, as 1.21.2 reads it: its JSON as the listener's values */
    @SuppressWarnings("rawtypes")
    public static java.util.Map preparedValues(net.minecraft.class_4309 listener, java.util.Map jsonMap) {
        return fromJson(listener, jsonMap);
    }

    /**
     * ServerAdvancementManager.apply(map, resources, profiler), override side: 1.21.2 hands a 1.21.1 override the
     * parsed advancements; it reads each one's JSON, encoded with the manager's own codec and ops (its registries)
     */
    @SuppressWarnings("rawtypes")
    public static java.util.Map advancementJson(net.minecraft.class_2989 manager, java.util.Map advancements) {
        return toJson(manager, advancements);
    }

    /**
     * ServerAdvancementManager.apply(map, resources, profiler), call side: the advancement JSON a 1.21.1 caller
     * hands in, parsed as 1.21.1's apply parsed it (Advancement.CODEC over the registries): a map from
     * {@link #toJson} gives back the advancements its untouched entries stand for and parses with its ops, any
     * other map with the running server's registries
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Map advancements(java.util.Map jsonMap) {
        JsonView view = jsonMap instanceof JsonView v ? v : null;
        com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> ops = view != null ? view.ops : null;
        java.util.Map out = new java.util.HashMap();
        for (Object o : jsonMap.entrySet()) {
            java.util.Map.Entry e = (java.util.Map.Entry) o;
            Object value = e.getValue();
            Object same = view != null ? view.parsed.get(value) : null;
            if (same == null && value instanceof com.google.gson.JsonElement element) {
                if (ops == null) {
                    ops = serverJsonOps();
                }
                same = net.minecraft.class_161.field_47179.parse(ops, element).getOrThrow();
            }
            out.put(e.getKey(), same != null ? same : value);
        }
        return out;
    }

    private static com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> serverJsonOps() {
        net.minecraft.server.MinecraftServer server = CbRestRecipesHolder.server;
        if (server == null) {
            throw new IllegalStateException("CenturyBridge: ServerAdvancementManager.apply with 1.21.1 advancement JSON outside a running"
                + " server: 1.21.2 parses advancements with the server's registries, and there is none");
        }
        return server.method_30611().method_57093(com.mojang.serialization.JsonOps.INSTANCE);
    }

    // ------------------------------------------------------------------ ServerGamePacketListener

    /**
     * handleClientTickEnd of a 1.21.1 class implementing ServerGamePacketListener (abstractFills): 1.21.1's client
     * sent no such message, so a 1.21.1 listener receives nothing for it, as it did
     */
    public static void clientTickEnd(net.minecraft.class_2792 listener, net.minecraft.class_9836 packet) {
    }

    /**
     * handleBundleItemSelectedPacket of a 1.21.1 class implementing ServerGamePacketListener (abstractFills): 1.21.1's
     * client sent no such message (bundle item selection is 1.21.2's), so a 1.21.1 listener receives nothing for it
     */
    public static void selectBundleItem(net.minecraft.class_2792 listener, net.minecraft.class_9837 packet) {
    }
}
