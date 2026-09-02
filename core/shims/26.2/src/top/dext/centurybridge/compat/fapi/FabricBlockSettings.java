package top.dext.centurybridge.compat.fapi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * fabric-object-builder-api-v1's {@code FabricBlockSettings} as 1.20.x mods use
 * it (29 corpus mods, 90 references). Deleted from Fabric API in 1.21: the
 * vanilla {@code BlockBehaviour.Properties} has every setter. This is the old
 * fluent subclass over the 26.2 Properties (its constructor is public through
 * Fabric API's transitive access widener -- the shim compiles against the
 * widened stub, see WidenStub).
 *
 * <p>Two families of members: the Fabric names returning FabricBlockSettings
 * (copyOf, nonOpaque, sounds, luminance ...) and the intermediary-named vanilla
 * setters the corpus reaches THROUGH this owner (method_9630 = copy(block),
 * method_29292 = requiresTool ...): javac names the static receiver type as the
 * owner and the wall only translates vanilla owners, so those must exist here
 * under their 1.20.1 intermediary names with the exact 1.20.1 descriptor
 * (return type BlockBehaviour.Properties, not the covariant subclass).
 */
public class FabricBlockSettings extends BlockBehaviour.Properties {

    protected FabricBlockSettings() {
        super();
    }

    protected FabricBlockSettings(BlockBehaviour.Properties settings) {
        super();
        copyFrom(settings);
    }

    /** 1.20.1 copied every field of the source settings object; done generically so a new field cannot be missed. */
    private void copyFrom(BlockBehaviour.Properties src) {
        for (Field f : BlockBehaviour.Properties.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            try {
                f.setAccessible(true);
                f.set(this, f.get(src));
            } catch (ReflectiveOperationException | RuntimeException e) {
                System.err.println("[CenturyBridge] FabricBlockSettings copy: cannot copy " + f.getName() + ": " + e);
            }
        }
    }

    private void set(String field, Object value) {
        try {
            Field f = BlockBehaviour.Properties.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(this, value);
        } catch (ReflectiveOperationException | RuntimeException e) {
            System.err.println("[CenturyBridge] FabricBlockSettings: cannot set " + field + ": " + e);
        }
    }

    // ---- factories ----

    public static FabricBlockSettings create() {
        return new FabricBlockSettings();
    }

    public static FabricBlockSettings of() {
        return create();
    }

    /** copies drops and description overrides too, as the 1.20.1 field-by-field copy did */
    public static FabricBlockSettings copyOf(BlockBehaviour block) {
        return new FabricBlockSettings(BlockBehaviour.Properties.ofFullCopy(block));
    }

    public static FabricBlockSettings copyOf(BlockBehaviour.Properties settings) {
        return new FabricBlockSettings(settings);
    }

    // ---- Fabric-named fluent setters (1.20.1 FabricBlockSettings) ----

    public FabricBlockSettings noCollision() {
        super.noCollision();
        return this;
    }

    public FabricBlockSettings nonOpaque() {
        super.noOcclusion();
        return this;
    }

    public FabricBlockSettings slipperiness(float value) {
        super.friction(value);
        return this;
    }

    public FabricBlockSettings velocityMultiplier(float value) {
        super.speedFactor(value);
        return this;
    }

    public FabricBlockSettings jumpVelocityMultiplier(float value) {
        super.jumpFactor(value);
        return this;
    }

    public FabricBlockSettings sounds(SoundType group) {
        super.sound(group);
        return this;
    }

    public FabricBlockSettings lightLevel(ToIntFunction<BlockState> levelFunction) {
        super.lightLevel(levelFunction);
        return this;
    }

    public FabricBlockSettings luminance(ToIntFunction<BlockState> luminanceFunction) {
        super.lightLevel(luminanceFunction);
        return this;
    }

    public FabricBlockSettings strength(float hardness, float resistance) {
        super.strength(hardness, resistance);
        return this;
    }

    public FabricBlockSettings breakInstantly() {
        super.instabreak();
        return this;
    }

    public FabricBlockSettings strength(float strength) {
        super.strength(strength);
        return this;
    }

    public FabricBlockSettings ticksRandomly() {
        super.randomTicks();
        return this;
    }

    public FabricBlockSettings dynamicBounds() {
        super.dynamicShape();
        return this;
    }

    public FabricBlockSettings dropsNothing() {
        super.noLootTable();
        return this;
    }

    public FabricBlockSettings dropsLike(Block block) {
        super.overrideLootTable(block.getLootTable());
        return this;
    }

    public FabricBlockSettings air() {
        super.air();
        return this;
    }

    public FabricBlockSettings allowsSpawning(BlockBehaviour.StateArgumentPredicate<EntityType<?>> predicate) {
        super.isValidSpawn(predicate);
        return this;
    }

    public FabricBlockSettings solidBlock(BlockBehaviour.StatePredicate predicate) {
        super.isRedstoneConductor(predicate);
        return this;
    }

    public FabricBlockSettings suffocates(BlockBehaviour.StatePredicate predicate) {
        super.isSuffocating(predicate);
        return this;
    }

    public FabricBlockSettings blockVision(BlockBehaviour.StatePredicate predicate) {
        super.isViewBlocking(predicate);
        return this;
    }

    public FabricBlockSettings requiresTool() {
        super.requiresCorrectToolForDrops();
        return this;
    }

    public FabricBlockSettings mapColor(MapColor color) {
        super.mapColor(color);
        return this;
    }

    public FabricBlockSettings hardness(float hardness) {
        super.destroyTime(hardness);
        return this;
    }

    public FabricBlockSettings resistance(float resistance) {
        super.explosionResistance(resistance);
        return this;
    }

    public FabricBlockSettings offset(BlockBehaviour.OffsetType offsetType) {
        super.offsetType(offsetType);
        return this;
    }

    public FabricBlockSettings noBlockBreakParticles() {
        super.noTerrainParticles();
        return this;
    }

    public FabricBlockSettings requires(FeatureFlag... features) {
        super.requiredFeatures(features);
        return this;
    }

    public FabricBlockSettings mapColor(Function<BlockState, MapColor> mapColorProvider) {
        super.mapColor(mapColorProvider);
        return this;
    }

    public FabricBlockSettings burnable() {
        super.ignitedByLava();
        return this;
    }

    public FabricBlockSettings liquid() {
        super.liquid();
        return this;
    }

    public FabricBlockSettings solid() {
        super.forceSolidOn();
        return this;
    }

    public FabricBlockSettings notSolid() {
        super.forceSolidOff();
        return this;
    }

    public FabricBlockSettings pistonBehavior(PushReaction pistonBehavior) {
        super.pushReaction(pistonBehavior);
        return this;
    }

    public FabricBlockSettings instrument(NoteBlockInstrument instrument) {
        super.instrument(instrument);
        return this;
    }

    public FabricBlockSettings replaceable() {
        super.replaceable();
        return this;
    }

    public FabricBlockSettings lightLevel(int lightLevel) {
        return luminance(lightLevel);
    }

    public FabricBlockSettings luminance(int luminance) {
        super.lightLevel(state -> luminance);
        return this;
    }

    public FabricBlockSettings drops(Identifier dropTableId) {
        super.overrideLootTable(Optional.of(ResourceKey.create(Registries.LOOT_TABLE, dropTableId)));
        return this;
    }

    public FabricBlockSettings materialColor(MapColor color) {
        return mapColor(color);
    }

    public FabricBlockSettings materialColor(DyeColor color) {
        return mapColor(color);
    }

    public FabricBlockSettings mapColor(DyeColor color) {
        super.mapColor(color);
        return this;
    }

    /** 1.20.1 wrote the flag directly; noCollision() would also clear occlusion */
    public FabricBlockSettings collidable(boolean collidable) {
        set("hasCollision", collidable);
        return this;
    }

    // ---- inherited vanilla setters under their 1.20.1 intermediary names (owner = this class) ----

    /** AbstractBlock.Settings.copy(block) */
    public static BlockBehaviour.Properties method_9630(BlockBehaviour block) {
        return BlockBehaviour.Properties.ofLegacyCopy(block);
    }

    /** AbstractBlock.Settings.create() */
    public static BlockBehaviour.Properties method_9637() {
        return BlockBehaviour.Properties.of();
    }

    public BlockBehaviour.Properties method_9634() {
        return noCollision();
    }

    public BlockBehaviour.Properties method_22488() {
        return nonOpaque();
    }

    public BlockBehaviour.Properties method_9628(float value) {
        return slipperiness(value);
    }

    public BlockBehaviour.Properties method_23351(float value) {
        return velocityMultiplier(value);
    }

    public BlockBehaviour.Properties method_23352(float value) {
        return jumpVelocityMultiplier(value);
    }

    public BlockBehaviour.Properties method_9626(SoundType group) {
        return sounds(group);
    }

    public BlockBehaviour.Properties method_9631(ToIntFunction<BlockState> fn) {
        return luminance(fn);
    }

    public BlockBehaviour.Properties method_9629(float hardness, float resistance) {
        return strength(hardness, resistance);
    }

    public BlockBehaviour.Properties method_9618() {
        return breakInstantly();
    }

    public BlockBehaviour.Properties method_9632(float strength) {
        return strength(strength);
    }

    public BlockBehaviour.Properties method_9640() {
        return ticksRandomly();
    }

    public BlockBehaviour.Properties method_9624() {
        return dynamicBounds();
    }

    public BlockBehaviour.Properties method_42327() {
        return dropsNothing();
    }

    public BlockBehaviour.Properties method_16228(Block block) {
        return dropsLike(block);
    }

    public BlockBehaviour.Properties method_50013() {
        return burnable();
    }

    public BlockBehaviour.Properties method_51177() {
        return liquid();
    }

    public BlockBehaviour.Properties method_51369() {
        return solid();
    }

    public BlockBehaviour.Properties method_51370() {
        return notSolid();
    }

    public BlockBehaviour.Properties method_50012(PushReaction behavior) {
        return pistonBehavior(behavior);
    }

    public BlockBehaviour.Properties method_26250() {
        return air();
    }

    public BlockBehaviour.Properties method_26235(BlockBehaviour.StateArgumentPredicate<EntityType<?>> predicate) {
        return allowsSpawning(predicate);
    }

    public BlockBehaviour.Properties method_26236(BlockBehaviour.StatePredicate predicate) {
        return solidBlock(predicate);
    }

    public BlockBehaviour.Properties method_26243(BlockBehaviour.StatePredicate predicate) {
        return suffocates(predicate);
    }

    public BlockBehaviour.Properties method_26245(BlockBehaviour.StatePredicate predicate) {
        return blockVision(predicate);
    }

    public BlockBehaviour.Properties method_29292() {
        return requiresTool();
    }

    public BlockBehaviour.Properties method_31710(MapColor color) {
        return mapColor(color);
    }

    public BlockBehaviour.Properties method_36557(float hardness) {
        return hardness(hardness);
    }

    public BlockBehaviour.Properties method_36558(float resistance) {
        return resistance(resistance);
    }

    public BlockBehaviour.Properties method_49229(BlockBehaviour.OffsetType type) {
        return offset(type);
    }

    public BlockBehaviour.Properties method_45477() {
        return noBlockBreakParticles();
    }

    public BlockBehaviour.Properties method_45476(FeatureFlag[] flags) {
        return requires(flags);
    }

    public BlockBehaviour.Properties method_51368(NoteBlockInstrument instrument) {
        return instrument(instrument);
    }

    public BlockBehaviour.Properties method_51371() {
        return replaceable();
    }

    public BlockBehaviour.Properties method_51520(Function<BlockState, MapColor> fn) {
        return mapColor(fn);
    }

    public BlockBehaviour.Properties method_51517(DyeColor color) {
        return mapColor(color);
    }
}
