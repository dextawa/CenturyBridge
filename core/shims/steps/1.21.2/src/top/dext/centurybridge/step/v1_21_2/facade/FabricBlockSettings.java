package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.class_1299;
import net.minecraft.class_1767;
import net.minecraft.class_2248;
import net.minecraft.class_2498;
import net.minecraft.class_2680;
import net.minecraft.class_2766;
import net.minecraft.class_3619;
import net.minecraft.class_3620;
import net.minecraft.class_4970;
import net.minecraft.class_5321;
import net.minecraft.class_52;
import net.minecraft.class_7696;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelLoot;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelProperties;

/**
 * 1.21.1 Fabric API's FabricBlockSettings (fabric-object-builder-api-v1, net/fabricmc/fabric/api/object/builder/v1/
 * block/FabricBlockSettings; classRenames), gone from 1.21.2's Fabric API: BlockBehaviour.Properties with chainable
 * setters answering the settings themselves. Each body is 1.21.1's, read off its bytecode: a vanilla setter (super),
 * then {@code this}; the aliases call the method they named.
 *
 * <p>1.21.2's Properties wants the block's id before the block is built; a 1.21.1 mod gives none, and CenturyBridge's
 * pending ids give it one at construction (CbPendingIds), for these settings as for vanilla's.
 * <ul>
 * <li>the copy constructor copies every setting 1.21.1's copied (its AbstractBlockSettingsAccessor reads), as the
 *     field each is in 1.21.2 -- the loot table as 1.21.2's drops, which keeps an explicit table and derives the
 *     default one from the id, as 1.21.1's null key did; the id and description are the new block's own;</li>
 * <li>{@link #drops} sets the table (1.21.2's overrideLootTable); 1.21.1's null key -- derive it from the id --
 *     restores the drops of fresh properties; LootTable EMPTY is no table (CbLevelLoot);</li>
 * <li>{@link #dropsLike} is 1.21.1's Properties.dropsLike, as CbLevelLoot gives it back.</li>
 * </ul>
 */
public class FabricBlockSettings extends class_4970.class_2251 {

    protected FabricBlockSettings() {
        super();
    }

    protected FabricBlockSettings(class_4970.class_2251 settings) {
        this();
        CbLevelProperties from = (CbLevelProperties) settings;
        CbLevelProperties to = (CbLevelProperties) this;
        this.hardness(from.cb$destroyTime());
        this.resistance(from.cb$explosionResistance());
        this.collidable(from.cb$hasCollision());
        to.cb$isRandomlyTicking(from.cb$isRandomlyTicking());
        this.luminance(from.cb$lightEmission());
        to.cb$mapColor(from.cb$mapColor());
        this.sounds(from.cb$soundType());
        this.slipperiness(from.cb$friction());
        this.velocityMultiplier(from.cb$speedFactor());
        to.cb$dynamicShape(from.cb$dynamicShape());
        to.cb$canOcclude(from.cb$canOcclude());
        to.cb$isAir(from.cb$isAir());
        to.cb$ignitedByLava(from.cb$ignitedByLava());
        to.cb$liquid(from.cb$liquid());
        to.cb$forceSolidOff(from.cb$forceSolidOff());
        to.cb$forceSolidOn(from.cb$forceSolidOn());
        this.pistonBehavior(from.cb$pushReaction());
        to.cb$requiresCorrectToolForDrops(from.cb$requiresCorrectToolForDrops());
        to.cb$offsetFunction(from.cb$offsetFunction());
        to.cb$spawnTerrainParticles(from.cb$spawnTerrainParticles());
        to.cb$requiredFeatures(from.cb$requiredFeatures());
        this.emissiveLighting(from.cb$emissiveRendering());
        this.instrument(from.cb$instrument());
        to.cb$replaceable(from.cb$replaceable());
        this.jumpVelocityMultiplier(from.cb$jumpFactor());
        to.cb$drops(from.cb$drops());
        this.allowsSpawning(from.cb$isValidSpawn());
        this.solidBlock(from.cb$isRedstoneConductor());
        this.suffocates(from.cb$isSuffocating());
        this.blockVision(from.cb$isViewBlocking());
        this.postProcess(from.cb$hasPostProcess());
    }

    public static FabricBlockSettings create() {
        return new FabricBlockSettings();
    }

    public static FabricBlockSettings of() {
        return create();
    }

    public static FabricBlockSettings copyOf(class_4970 block) {
        return new FabricBlockSettings(block.method_54095());
    }

    public static FabricBlockSettings copyOf(class_4970.class_2251 settings) {
        return new FabricBlockSettings(settings);
    }

    /** noCollision() */
    public FabricBlockSettings noCollision() {
        super.method_9634();
        return this;
    }

    /** the vanilla setter: noCollision (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9634() {
        return this.noCollision();
    }

    /** nonOpaque() */
    public FabricBlockSettings nonOpaque() {
        super.method_22488();
        return this;
    }

    /** the vanilla setter: nonOpaque (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_22488() {
        return this.nonOpaque();
    }

    public FabricBlockSettings slipperiness(float value) {
        super.method_9628(value);
        return this;
    }

    /** the vanilla setter: slipperiness (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9628(float value) {
        return this.slipperiness(value);
    }

    public FabricBlockSettings velocityMultiplier(float value) {
        super.method_23351(value);
        return this;
    }

    /** the vanilla setter: velocityMultiplier (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_23351(float value) {
        return this.velocityMultiplier(value);
    }

    public FabricBlockSettings jumpVelocityMultiplier(float value) {
        super.method_23352(value);
        return this;
    }

    /** the vanilla setter: jumpVelocityMultiplier (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_23352(float value) {
        return this.jumpVelocityMultiplier(value);
    }

    public FabricBlockSettings sounds(class_2498 group) {
        super.method_9626(group);
        return this;
    }

    /** the vanilla setter: sounds (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9626(class_2498 group) {
        return this.sounds(group);
    }

    @Deprecated
    public FabricBlockSettings lightLevel(ToIntFunction<class_2680> levelFunction) {
        return this.luminance(levelFunction);
    }

    public FabricBlockSettings luminance(ToIntFunction<class_2680> luminanceFunction) {
        super.method_9631(luminanceFunction);
        return this;
    }

    /** the vanilla setter: luminance (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9631(ToIntFunction<class_2680> luminanceFunction) {
        return this.luminance(luminanceFunction);
    }

    public FabricBlockSettings strength(float hardness, float resistance) {
        super.method_9629(hardness, resistance);
        return this;
    }

    /** the vanilla setter: strength (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9629(float hardness, float resistance) {
        return this.strength(hardness, resistance);
    }

    public FabricBlockSettings breakInstantly() {
        super.method_9618();
        return this;
    }

    /** the vanilla setter: breakInstantly (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9618() {
        return this.breakInstantly();
    }

    public FabricBlockSettings strength(float strength) {
        super.method_9632(strength);
        return this;
    }

    /** the vanilla setter: strength (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9632(float strength) {
        return this.strength(strength);
    }

    public FabricBlockSettings ticksRandomly() {
        super.method_9640();
        return this;
    }

    /** the vanilla setter: ticksRandomly (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9640() {
        return this.ticksRandomly();
    }

    public FabricBlockSettings dynamicBounds() {
        super.method_9624();
        return this;
    }

    /** the vanilla setter: dynamicBounds (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_9624() {
        return this.dynamicBounds();
    }

    public FabricBlockSettings dropsNothing() {
        super.method_42327();
        return this;
    }

    /** the vanilla setter: dropsNothing (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_42327() {
        return this.dropsNothing();
    }

    /** dropsLike(block): 1.21.1's Properties.dropsLike (CbLevelLoot) */
    public FabricBlockSettings dropsLike(class_2248 block) {
        CbLevelLoot.method_16228(this, block);
        return this;
    }

    public FabricBlockSettings air() {
        super.method_26250();
        return this;
    }

    /** the vanilla setter: air (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_26250() {
        return this.air();
    }

    public FabricBlockSettings allowsSpawning(class_4970.class_4972<class_1299<?>> predicate) {
        super.method_26235(predicate);
        return this;
    }

    /** the vanilla setter: allowsSpawning (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_26235(class_4970.class_4972<class_1299<?>> predicate) {
        return this.allowsSpawning(predicate);
    }

    public FabricBlockSettings solidBlock(class_4970.class_4973 predicate) {
        super.method_26236(predicate);
        return this;
    }

    /** the vanilla setter: solidBlock (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_26236(class_4970.class_4973 predicate) {
        return this.solidBlock(predicate);
    }

    public FabricBlockSettings suffocates(class_4970.class_4973 predicate) {
        super.method_26243(predicate);
        return this;
    }

    /** the vanilla setter: suffocates (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_26243(class_4970.class_4973 predicate) {
        return this.suffocates(predicate);
    }

    public FabricBlockSettings blockVision(class_4970.class_4973 predicate) {
        super.method_26245(predicate);
        return this;
    }

    /** the vanilla setter: blockVision (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_26245(class_4970.class_4973 predicate) {
        return this.blockVision(predicate);
    }

    public FabricBlockSettings postProcess(class_4970.class_4973 predicate) {
        super.method_26247(predicate);
        return this;
    }

    /** the vanilla setter: postProcess (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_26247(class_4970.class_4973 predicate) {
        return this.postProcess(predicate);
    }

    public FabricBlockSettings emissiveLighting(class_4970.class_4973 predicate) {
        super.method_26249(predicate);
        return this;
    }

    /** the vanilla setter: emissiveLighting (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_26249(class_4970.class_4973 predicate) {
        return this.emissiveLighting(predicate);
    }

    public FabricBlockSettings requiresTool() {
        super.method_29292();
        return this;
    }

    /** the vanilla setter: requiresTool (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_29292() {
        return this.requiresTool();
    }

    public FabricBlockSettings mapColor(class_3620 color) {
        super.method_31710(color);
        return this;
    }

    /** the vanilla setter: mapColor (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_31710(class_3620 color) {
        return this.mapColor(color);
    }

    public FabricBlockSettings hardness(float hardness) {
        super.method_36557(hardness);
        return this;
    }

    /** the vanilla setter: hardness (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_36557(float hardness) {
        return this.hardness(hardness);
    }

    public FabricBlockSettings resistance(float resistance) {
        super.method_36558(resistance);
        return this;
    }

    /** the vanilla setter: resistance (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_36558(float resistance) {
        return this.resistance(resistance);
    }

    public FabricBlockSettings offset(class_4970.class_2250 offsetType) {
        super.method_49229(offsetType);
        return this;
    }

    /** the vanilla setter: offset (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_49229(class_4970.class_2250 offsetType) {
        return this.offset(offsetType);
    }

    public FabricBlockSettings noBlockBreakParticles() {
        super.method_45477();
        return this;
    }

    /** the vanilla setter: noBlockBreakParticles (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_45477() {
        return this.noBlockBreakParticles();
    }

    public FabricBlockSettings requires(class_7696... features) {
        super.method_45476(features);
        return this;
    }

    /** the vanilla setter: requires (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_45476(class_7696... features) {
        return this.requires(features);
    }

    public FabricBlockSettings mapColor(Function<class_2680, class_3620> mapColorProvider) {
        super.method_51520(mapColorProvider);
        return this;
    }

    /** the vanilla setter: mapColor (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_51520(Function<class_2680, class_3620> mapColorProvider) {
        return this.mapColor(mapColorProvider);
    }

    public FabricBlockSettings burnable() {
        super.method_50013();
        return this;
    }

    /** the vanilla setter: burnable (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_50013() {
        return this.burnable();
    }

    public FabricBlockSettings liquid() {
        super.method_51177();
        return this;
    }

    /** the vanilla setter: liquid (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_51177() {
        return this.liquid();
    }

    public FabricBlockSettings solid() {
        super.method_51369();
        return this;
    }

    /** the vanilla setter: solid (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_51369() {
        return this.solid();
    }

    public FabricBlockSettings notSolid() {
        super.method_51370();
        return this;
    }

    /** the vanilla setter: notSolid (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_51370() {
        return this.notSolid();
    }

    public FabricBlockSettings pistonBehavior(class_3619 pistonBehavior) {
        super.method_50012(pistonBehavior);
        return this;
    }

    /** the vanilla setter: pistonBehavior (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_50012(class_3619 pistonBehavior) {
        return this.pistonBehavior(pistonBehavior);
    }

    public FabricBlockSettings instrument(class_2766 instrument) {
        super.method_51368(instrument);
        return this;
    }

    /** the vanilla setter: instrument (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_51368(class_2766 instrument) {
        return this.instrument(instrument);
    }

    public FabricBlockSettings replaceable() {
        super.method_51371();
        return this;
    }

    /** the vanilla setter: replaceable (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_51371() {
        return this.replaceable();
    }

    @Deprecated
    public FabricBlockSettings lightLevel(int lightLevel) {
        return this.luminance(lightLevel);
    }

    public FabricBlockSettings luminance(int luminance) {
        return this.luminance(state -> luminance);
    }

    /** drops(key): 1.21.1's setLootTableKey -- null derives the table from the id, LootTable EMPTY is none */
    public FabricBlockSettings drops(class_5321<class_52> dropTableId) {
        if (dropTableId == null) {
            ((CbLevelProperties) this).cb$drops(((CbLevelProperties) class_4970.class_2251.method_9637()).cb$drops());
        } else {
            this.method_63502(CbLevelLoot.toOptional(dropTableId));
        }
        return this;
    }

    @Deprecated
    public FabricBlockSettings materialColor(class_3620 color) {
        return this.mapColor(color);
    }

    @Deprecated
    public FabricBlockSettings materialColor(class_1767 color) {
        return this.mapColor(color);
    }

    public FabricBlockSettings mapColor(class_1767 color) {
        return this.mapColor(color.method_7794());
    }

    /** the vanilla setter: mapColor(DyeColor) (1.21.1's bridge) */
    @Override
    public class_4970.class_2251 method_51517(class_1767 color) {
        return this.mapColor(color);
    }

    /** collidable(b): 1.21.1 set the collision flag alone */
    public FabricBlockSettings collidable(boolean collidable) {
        ((CbLevelProperties) this).cb$hasCollision(collidable);
        return this;
    }
}
