package top.dext.centurybridge.step.v26_1.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallEntityFluids;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11's Entity.updateFluidHeightAndDoFluidPushing(tag, speed) (class_1297.method_5692): measure the fluid of a
 * tag around the entity, push the entity along its flow by speed when it is pushed by fluids, answer whether it is
 * in it. 26.1 measures water and lava together (EntityFluidInteraction.update) in updateFluidInteraction(), reads
 * isInFluid(WATER) / isInFluid(LAVA) into two locals and pushes by each fluid's current when its local is true.
 *
 * Vanilla's path goes through the restored method (mixin/CbWallEntityFluidReadsMixin): updateFluidInteraction's two
 * in-fluid answers are taken from this.updateFluidHeightAndDoFluidPushing(WATER, 0.014) and (LAVA, 0.007 or
 * 0.0023333333333333335 by the dimension's FAST_LAVA, the speeds 1.21.11 passed and 26.1 still pushes with). Inside
 * that call the method answers the value vanilla just stored (its measurement, and whatever a 26.1 mod's mixin made
 * of it) and leaves the push to vanilla, which pushes right after when the answer is true -- so unmixed vanilla's
 * value stands, and an override, or a mod's injector at its head or return, decides as on 1.21.11 whether the entity
 * counts as in that fluid and whether it is pushed. Called by a mod outside that path, it measures first (update) and
 * pushes itself, as 1.21.11's did. A tag 26.1 does not track (a mod's own fluid tag) is measured by 1.21.11's code
 * (rt/CbWallEntityFluids) and its height answered by getFluidHeight(tag), where 26.1 answers 0.
 *
 * Known difference: inside vanilla's path the push itself is vanilla's (EntityFluidInteraction's current, over the
 * fluid interaction box) and so is the height: a mod changing the speed argument, the push or the measurement of the
 * 1.21.11 body does not change them there. The marker carries only the answer (HEAD / RETURN injectors); the rest
 * stays open as "vanilla path partial".
 */
@Mixin(value = Entity.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallEntityFluidMixin implements CbWallEntityFluids.Legacy, CbWallLegacyCalls.EntityFluidLegacy {

    @Shadow
    @Final
    private EntityFluidInteraction fluidInteraction;

    @Unique
    private boolean cb$fluidVanillaPath;

    @Unique
    private boolean cb$vanillaAnswer;

    @Unique
    private Object2DoubleMap<TagKey<Fluid>> cb$legacyFluidHeights;

    @Shadow
    public abstract boolean touchingUnloadedChunk();

    @Shadow
    public abstract boolean isPushedByFluid();

    /** 1.21.11 Entity.updateFluidHeightAndDoFluidPushing (class_1297.method_5692) */
    @CbVanillaReads(value = "CbWallEntityFluidReadsMixin: updateFluidInteraction takes its isInFluid(WATER) / isInFluid(LAVA) "
        + "answers from this method (cb$water / cb$lava) and pushes when it answers true; inside that call the body "
        + "returns vanilla's measurement, so only what decides the answer is carried, not the speed or the push",
        carries = {"Inject@HEAD", "Inject@RETURN", "ModifyReturnValue@RETURN"})
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double speed) {
        Entity self = (Entity) (Object) this;
        if (!CbWallEntityFluids.tracked(tag)) {
            if (touchingUnloadedChunk()) {
                return false;
            }
            if (cb$legacyFluidHeights == null) {
                cb$legacyFluidHeights = new Object2DoubleArrayMap<>(2);
            }
            return CbWallEntityFluids.measureAndPush(self, tag, speed, cb$legacyFluidHeights);
        }
        if (cb$fluidVanillaPath) {
            return cb$vanillaAnswer; // vanilla's own answer, measured just now; vanilla pushes on a true answer
        }
        if (touchingUnloadedChunk()) {
            return false;
        }
        fluidInteraction.update(self, !isPushedByFluid());
        if (isPushedByFluid()) {
            fluidInteraction.applyCurrentTo(tag, self, speed);
        }
        return fluidInteraction.isInFluid(tag);
    }

    /** the restored method's answer in vanilla's path: vanilla's own value (whatever a 26.1 mod made of it) unless mixed */
    @Override
    public boolean cb$vanillaPath(TagKey<Fluid> tag, double speed, boolean vanilla) {
        boolean outer = cb$fluidVanillaPath;
        boolean outerAnswer = cb$vanillaAnswer;
        cb$fluidVanillaPath = true;
        cb$vanillaAnswer = vanilla;
        try {
            return updateFluidHeightAndDoFluidPushing(tag, speed);
        } finally {
            cb$fluidVanillaPath = outer;
            cb$vanillaAnswer = outerAnswer;
        }
    }

    @Override
    public Object2DoubleMap<TagKey<Fluid>> cb$legacyFluidHeightsIfAny() {
        return cb$legacyFluidHeights;
    }

    @Override
    public Object2DoubleMap<TagKey<Fluid>> cb$legacyFluidHeights() {
        if (cb$legacyFluidHeights == null) {
            cb$legacyFluidHeights = new Object2DoubleArrayMap<>(2);
        }
        return cb$legacyFluidHeights;
    }
}
