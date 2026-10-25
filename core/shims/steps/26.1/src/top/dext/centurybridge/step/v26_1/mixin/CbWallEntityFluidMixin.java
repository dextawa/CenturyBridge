package top.dext.centurybridge.step.v26_1.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallEntityFluids;

/**
 * 1.21.11's Entity.updateFluidHeightAndDoFluidPushing(tag, speed) (class_1297.method_5692): measure the fluid of a
 * tag around the entity, push the entity along its flow by speed when it is pushed by fluids, answer whether it is
 * in it. 26.1 measures water and lava together (EntityFluidInteraction.update) in updateFluidInteraction(), reads
 * isInFluid(WATER) / isInFluid(LAVA) into two locals and pushes by each fluid's current when its local is true.
 *
 * Vanilla's path goes through the restored method: updateFluidInteraction's two in-fluid answers are taken from
 * this.updateFluidHeightAndDoFluidPushing(WATER, 0.014) and (LAVA, 0.007 or 0.0023333333333333335 by the
 * dimension's FAST_LAVA, the speeds 1.21.11 passed and 26.1 still pushes with). Inside that call the method reads
 * the measurement vanilla just made and leaves the push to vanilla, which pushes right after when the answer is
 * true -- so an override, or a mod's injector at its head or return, decides as on 1.21.11 whether the entity counts
 * as in that fluid and whether it is pushed. Called by a mod outside that path, it measures first (update) and
 * pushes itself, as 1.21.11's did. A tag 26.1 does not track (a mod's own fluid tag) is measured by 1.21.11's code
 * (rt/CbWallEntityFluids) and its height answered by getFluidHeight(tag), where 26.1 answers 0.
 *
 * Known difference: inside vanilla's path the push itself is vanilla's (EntityFluidInteraction's current, over the
 * fluid interaction box): a mod changing the speed argument or the push code of the 1.21.11 body does not change it.
 */
@Mixin(Entity.class)
public abstract class CbWallEntityFluidMixin {

    @Shadow
    @Final
    private EntityFluidInteraction fluidInteraction;

    @Unique
    private boolean cb$fluidVanillaPath;

    @Unique
    private Object2DoubleMap<TagKey<Fluid>> cb$legacyFluidHeights;

    @Shadow
    public abstract boolean touchingUnloadedChunk();

    @Shadow
    public abstract boolean isPushedByFluid();

    @Shadow
    public abstract Level level();

    /** 1.21.11 Entity.updateFluidHeightAndDoFluidPushing (class_1297.method_5692) */
    @CbVanillaReads("CbWallEntityFluidMixin: updateFluidInteraction takes its isInFluid(WATER) / isInFluid(LAVA) "
        + "answers from this method (cb$water / cb$lava) and pushes when it answers true")
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
            return fluidInteraction.isInFluid(tag); // measured by vanilla just now; vanilla pushes on a true answer
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

    /** 1.21.11 cleared Entity.fluidHeight at the start of each fluid update (updateInWaterStateAndDoFluidPushing) */
    @Inject(method = "updateFluidInteraction", at = @At("HEAD"))
    private void cb$clearLegacyHeights(CallbackInfoReturnable<Boolean> cir) {
        if (cb$legacyFluidHeights != null) {
            cb$legacyFluidHeights.clear();
        }
    }

    @ModifyVariable(method = "updateFluidInteraction", at = @At("STORE"), ordinal = 0)
    private boolean cb$water(boolean inWater) {
        return cb$vanillaPath(FluidTags.WATER, 0.014);
    }

    @ModifyVariable(method = "updateFluidInteraction", at = @At("STORE"), ordinal = 1)
    private boolean cb$lava(boolean inLava) {
        boolean fast = (Boolean) level().environmentAttributes().getDimensionValue(EnvironmentAttributes.FAST_LAVA);
        return cb$vanillaPath(FluidTags.LAVA, fast ? 0.007 : 0.0023333333333333335);
    }

    @Unique
    private boolean cb$vanillaPath(TagKey<Fluid> tag, double speed) {
        boolean outer = cb$fluidVanillaPath;
        cb$fluidVanillaPath = true;
        try {
            return updateFluidHeightAndDoFluidPushing(tag, speed);
        } finally {
            cb$fluidVanillaPath = outer;
        }
    }

    /** a tag 26.1 does not track: the height 1.21.11's measurement last recorded for it */
    @Inject(method = "getFluidHeight", at = @At("HEAD"), cancellable = true)
    private void cb$legacyHeight(TagKey<Fluid> tag, CallbackInfoReturnable<Double> cir) {
        if (cb$legacyFluidHeights != null && !CbWallEntityFluids.tracked(tag)) {
            cir.setReturnValue(cb$legacyFluidHeights.getDouble(tag));
        }
    }
}
