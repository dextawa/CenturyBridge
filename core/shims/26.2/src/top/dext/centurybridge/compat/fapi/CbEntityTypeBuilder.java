package top.dext.centurybridge.compat.fapi;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import top.dext.centurybridge.facade.v26_2.class_1317;

/**
 * fabric-object-builder-api-v1's {@code FabricEntityTypeBuilder} as 1.20.x mods
 * use it (38 corpus mods). Deleted from Fabric API in 1.21.2: the vanilla
 * {@code EntityType.Builder} grew every option, and the two Fabric extras
 * (alwaysUpdateVelocity, defaultAttributes/spawnPlacement at build time) moved
 * to interface injection / {@code FabricEntityType.Builder}. This class keeps
 * the old fluent shape and drives the vanilla builder underneath.
 *
 * <p>The one thing the old {@code build()} could not know is the registry id:
 * 1.20.1 derived the description id and loot table lazily from the registry,
 * 1.21.2+ bakes both into the type from the key passed to {@code build(key)}.
 * The type is built under a placeholder key and both strings are rewritten the
 * moment the mod registers it (RegistryEntryAddedCallback on ENTITY_TYPE), so
 * {@code entity.<ns>.<path>} and {@code entities/<ns>/<path>} come out exactly
 * as they did in 1.20.1.
 */
public class CbEntityTypeBuilder<T extends Entity> {

    private static final AtomicInteger SEQ = new AtomicInteger();
    private static final Set<EntityType<?>> PENDING = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
    private static final AtomicBoolean WARNED_SPAWN_BLOCKS = new AtomicBoolean();

    static {
        RegistryEntryAddedCallback.event(BuiltInRegistries.ENTITY_TYPE).register((raw, id, type) -> {
            if (PENDING.remove(type)) {
                bakeRegistryId(type, id);
            }
        });
    }

    /** what EntityType.Builder.build(key) derives from the key: description id and entity loot table */
    static void bakeRegistryId(EntityType<?> type, Identifier id) {
        setFinal(type, "descriptionId", id.toLanguageKey("entity"));
        setFinal(type, "lootTable", Optional.of(ResourceKey.create(Registries.LOOT_TABLE, id.withPrefix("entities/"))));
    }

    private static void setFinal(EntityType<?> type, String field, Object value) {
        try {
            Field f = EntityType.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(type, value);
        } catch (ReflectiveOperationException | RuntimeException e) {
            System.err.println("[CenturyBridge] cannot bake EntityType." + field + " for a FabricEntityTypeBuilder entity: " + e);
        }
    }

    protected MobCategory spawnGroup;
    protected EntityType.EntityFactory<T> factory;
    protected boolean saveable = true;
    protected boolean summonable = true;
    protected int trackRange = 5;
    protected int trackedUpdateRate = 3;
    protected Boolean forceTrackedVelocityUpdates;
    protected boolean fireImmune;
    protected boolean spawnableFarFromPlayer;
    protected EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
    protected FeatureFlag[] requiredFeatures;

    protected CbEntityTypeBuilder(MobCategory spawnGroup, EntityType.EntityFactory<T> factory) {
        this.spawnGroup = spawnGroup;
        this.factory = factory;
    }

    public static <T extends Entity> CbEntityTypeBuilder<T> create() {
        return create(MobCategory.MISC);
    }

    public static <T extends Entity> CbEntityTypeBuilder<T> create(MobCategory spawnGroup) {
        return create(spawnGroup, (t, level) -> null);
    }

    public static <T extends Entity> CbEntityTypeBuilder<T> create(MobCategory spawnGroup, EntityType.EntityFactory<T> factory) {
        return new CbEntityTypeBuilder<>(spawnGroup, factory);
    }

    public static <T extends LivingEntity> Living<T> createLiving() {
        return new Living<>(MobCategory.MISC, (t, level) -> null);
    }

    public static <T extends net.minecraft.world.entity.Mob> Mob<T> createMob() {
        return new Mob<T>(MobCategory.MISC, (t, level) -> null);
    }

    public CbEntityTypeBuilder<T> spawnGroup(MobCategory group) {
        this.spawnGroup = group;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <N extends T> CbEntityTypeBuilder<N> entityFactory(EntityType.EntityFactory<N> factory) {
        this.factory = (EntityType.EntityFactory<T>) factory;
        return (CbEntityTypeBuilder<N>) this;
    }

    public CbEntityTypeBuilder<T> disableSummon() {
        this.summonable = false;
        return this;
    }

    public CbEntityTypeBuilder<T> disableSaving() {
        this.saveable = false;
        return this;
    }

    public CbEntityTypeBuilder<T> fireImmune() {
        this.fireImmune = true;
        return this;
    }

    public CbEntityTypeBuilder<T> spawnableFarFromPlayer() {
        this.spawnableFarFromPlayer = true;
        return this;
    }

    public CbEntityTypeBuilder<T> dimensions(EntityDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public CbEntityTypeBuilder<T> trackable(int trackRangeBlocks, int trackedUpdateRate) {
        return trackable(trackRangeBlocks, trackedUpdateRate, true);
    }

    public CbEntityTypeBuilder<T> trackable(int trackRangeBlocks, int trackedUpdateRate, boolean forceTrackedVelocityUpdates) {
        trackRangeBlocks(trackRangeBlocks);
        trackedUpdateRate(trackedUpdateRate);
        return forceTrackedVelocityUpdates(forceTrackedVelocityUpdates);
    }

    public CbEntityTypeBuilder<T> trackRangeChunks(int range) {
        this.trackRange = range;
        return this;
    }

    /** 1.20.1: blocks rounded up to chunks */
    public CbEntityTypeBuilder<T> trackRangeBlocks(int range) {
        return trackRangeChunks((range + 15) / 16);
    }

    public CbEntityTypeBuilder<T> trackedUpdateRate(int rate) {
        this.trackedUpdateRate = rate;
        return this;
    }

    public CbEntityTypeBuilder<T> forceTrackedVelocityUpdates(boolean force) {
        this.forceTrackedVelocityUpdates = force;
        return this;
    }

    /**
     * 1.20.1 took a block set ("spawns in / is immune to these blocks"); since
     * 1.20.5 the builder only accepts a block TAG (immuneTo), which cannot be
     * minted from a block array at runtime. One corpus reference; kept as a
     * documented no-op rather than a crash.
     */
    public CbEntityTypeBuilder<T> specificSpawnBlocks(Block... blocks) {
        if (blocks.length > 0 && WARNED_SPAWN_BLOCKS.compareAndSet(false, true)) {
            System.err.println("[CenturyBridge] FabricEntityTypeBuilder.specificSpawnBlocks ignored on 26.x: EntityType.Builder.immuneTo takes a block tag now");
        }
        return this;
    }

    public CbEntityTypeBuilder<T> requires(FeatureFlag... flags) {
        this.requiredFeatures = flags;
        return this;
    }

    @SuppressWarnings("unchecked")
    public EntityType<T> build() {
        EntityType.Builder<T> b = EntityType.Builder.of(factory, spawnGroup);
        b.sized(dimensions.width(), dimensions.height());
        if (!saveable) {
            b.noSave();
        }
        if (!summonable) {
            b.noSummon();
        }
        if (fireImmune) {
            b.fireImmune();
        }
        if (spawnableFarFromPlayer) {
            b.canSpawnFarFromPlayer();
        }
        b.clientTrackingRange(trackRange);
        b.updateInterval(trackedUpdateRate);
        if (requiredFeatures != null) {
            b.requiredFeatures(requiredFeatures);
        }
        if (forceTrackedVelocityUpdates != null) {
            // interface-injected by fabric-object-builder-api-v1 into EntityType.Builder
            ((FabricEntityType.Builder<T>) (Object) b).alwaysUpdateVelocity(forceTrackedVelocityUpdates);
        }
        EntityType<T> type = b.build(ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath("centurybridge", "pending_entity_" + SEQ.incrementAndGet())));
        PENDING.add(type);
        return type;
    }

    /** was FabricEntityTypeBuilder.Living */
    public static class Living<T extends LivingEntity> extends CbEntityTypeBuilder<T> {
        protected Supplier<AttributeSupplier.Builder> defaultAttributeBuilder;

        protected Living(MobCategory spawnGroup, EntityType.EntityFactory<T> factory) {
            super(spawnGroup, factory);
        }

        @Override
        public Living<T> spawnGroup(MobCategory group) {
            super.spawnGroup(group);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <N extends T> Living<N> entityFactory(EntityType.EntityFactory<N> factory) {
            super.entityFactory(factory);
            return (Living<N>) this;
        }

        @Override
        public Living<T> disableSummon() {
            super.disableSummon();
            return this;
        }

        @Override
        public Living<T> disableSaving() {
            super.disableSaving();
            return this;
        }

        @Override
        public Living<T> fireImmune() {
            super.fireImmune();
            return this;
        }

        @Override
        public Living<T> spawnableFarFromPlayer() {
            super.spawnableFarFromPlayer();
            return this;
        }

        @Override
        public Living<T> dimensions(EntityDimensions dimensions) {
            super.dimensions(dimensions);
            return this;
        }

        @Override
        public Living<T> trackable(int trackRangeBlocks, int trackedUpdateRate) {
            super.trackable(trackRangeBlocks, trackedUpdateRate);
            return this;
        }

        @Override
        public Living<T> trackable(int trackRangeBlocks, int trackedUpdateRate, boolean forceTrackedVelocityUpdates) {
            super.trackable(trackRangeBlocks, trackedUpdateRate, forceTrackedVelocityUpdates);
            return this;
        }

        @Override
        public Living<T> trackRangeChunks(int range) {
            super.trackRangeChunks(range);
            return this;
        }

        @Override
        public Living<T> trackRangeBlocks(int range) {
            super.trackRangeBlocks(range);
            return this;
        }

        @Override
        public Living<T> trackedUpdateRate(int rate) {
            super.trackedUpdateRate(rate);
            return this;
        }

        @Override
        public Living<T> forceTrackedVelocityUpdates(boolean force) {
            super.forceTrackedVelocityUpdates(force);
            return this;
        }

        @Override
        public Living<T> specificSpawnBlocks(Block... blocks) {
            super.specificSpawnBlocks(blocks);
            return this;
        }

        @Override
        public Living<T> requires(FeatureFlag... flags) {
            super.requires(flags);
            return this;
        }

        public Living<T> defaultAttributes(Supplier<AttributeSupplier.Builder> defaultAttributeBuilder) {
            this.defaultAttributeBuilder = defaultAttributeBuilder;
            return this;
        }

        @Override
        public EntityType<T> build() {
            EntityType<T> type = super.build();
            if (defaultAttributeBuilder != null) {
                FabricDefaultAttributeRegistry.register(type, defaultAttributeBuilder.get());
            }
            return type;
        }
    }

    /** was FabricEntityTypeBuilder.Mob */
    public static class Mob<T extends net.minecraft.world.entity.Mob> extends Living<T> {
        protected class_1317.class_1319 restrictionLocation;
        protected Heightmap.Types restrictionHeightmap;
        protected SpawnPlacements.SpawnPredicate<T> spawnPredicate;

        protected Mob(MobCategory spawnGroup, EntityType.EntityFactory<T> factory) {
            super(spawnGroup, factory);
        }

        @Override
        public Mob<T> spawnGroup(MobCategory group) {
            super.spawnGroup(group);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <N extends T> Mob<N> entityFactory(EntityType.EntityFactory<N> factory) {
            super.entityFactory(factory);
            return (Mob<N>) this;
        }

        @Override
        public Mob<T> disableSummon() {
            super.disableSummon();
            return this;
        }

        @Override
        public Mob<T> disableSaving() {
            super.disableSaving();
            return this;
        }

        @Override
        public Mob<T> fireImmune() {
            super.fireImmune();
            return this;
        }

        @Override
        public Mob<T> spawnableFarFromPlayer() {
            super.spawnableFarFromPlayer();
            return this;
        }

        @Override
        public Mob<T> dimensions(EntityDimensions dimensions) {
            super.dimensions(dimensions);
            return this;
        }

        @Override
        public Mob<T> trackable(int trackRangeBlocks, int trackedUpdateRate) {
            super.trackable(trackRangeBlocks, trackedUpdateRate);
            return this;
        }

        @Override
        public Mob<T> trackable(int trackRangeBlocks, int trackedUpdateRate, boolean forceTrackedVelocityUpdates) {
            super.trackable(trackRangeBlocks, trackedUpdateRate, forceTrackedVelocityUpdates);
            return this;
        }

        @Override
        public Mob<T> trackRangeChunks(int range) {
            super.trackRangeChunks(range);
            return this;
        }

        @Override
        public Mob<T> trackRangeBlocks(int range) {
            super.trackRangeBlocks(range);
            return this;
        }

        @Override
        public Mob<T> trackedUpdateRate(int rate) {
            super.trackedUpdateRate(rate);
            return this;
        }

        @Override
        public Mob<T> forceTrackedVelocityUpdates(boolean force) {
            super.forceTrackedVelocityUpdates(force);
            return this;
        }

        @Override
        public Mob<T> specificSpawnBlocks(Block... blocks) {
            super.specificSpawnBlocks(blocks);
            return this;
        }

        @Override
        public Mob<T> requires(FeatureFlag... flags) {
            super.requires(flags);
            return this;
        }

        @Override
        public Mob<T> defaultAttributes(Supplier<AttributeSupplier.Builder> defaultAttributeBuilder) {
            super.defaultAttributes(defaultAttributeBuilder);
            return this;
        }

        /**
         * was spawnRestriction(SpawnRestriction.Location, Heightmap.Type, SpawnPredicate); the
         * Location enum died in 1.20.5 (SpawnPlacementType constants), the corpus reaches it
         * through the class_1317 facade.
         */
        public Mob<T> spawnRestriction(class_1317.class_1319 location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate) {
            this.restrictionLocation = location;
            this.restrictionHeightmap = heightmap;
            this.spawnPredicate = predicate;
            return this;
        }

        @Override
        public EntityType<T> build() {
            EntityType<T> type = super.build();
            if (spawnPredicate != null) {
                SpawnPlacements.register(type, restrictionLocation.cbToVanilla(), restrictionHeightmap, spawnPredicate);
            }
            return type;
        }
    }
}
