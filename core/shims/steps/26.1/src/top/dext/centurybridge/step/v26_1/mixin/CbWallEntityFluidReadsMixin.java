package top.dext.centurybridge.step.v26_1.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallEntityFluids;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 26.1's fluid update read through 1.21.11's restored updateFluidHeightAndDoFluidPushing (CbWallEntityFluidMixin):
 * updateFluidInteraction's in-water / in-lava locals become the restored method's answer for (WATER, 0.014) and
 * (LAVA, the dimension's speed) -- vanilla's own value unless a mod mixed or overrode the method; the heights 1.21.11's
 * code measured for tags 26.1 does not track are cleared at each update, as 1.21.11 cleared Entity.fluidHeight, and
 * answered by getFluidHeight where 26.1 answers 0. Default priority: injectors into 26.1 methods.
 */
@Mixin(Entity.class)
public abstract class CbWallEntityFluidReadsMixin {

    @Shadow
    public abstract Level level();

    @Inject(method = "updateFluidInteraction", at = @At("HEAD"))
    private void cb$clearLegacyHeights(CallbackInfoReturnable<Boolean> cir) {
        Object2DoubleMap<TagKey<Fluid>> heights = ((CbWallLegacyCalls.EntityFluidLegacy) this).cb$legacyFluidHeightsIfAny();
        if (heights != null) {
            heights.clear();
        }
    }

    @ModifyVariable(method = "updateFluidInteraction", at = @At("STORE"), ordinal = 0)
    private boolean cb$water(boolean inWater) {
        return ((CbWallLegacyCalls.EntityFluidLegacy) this).cb$vanillaPath(FluidTags.WATER, 0.014, inWater);
    }

    @ModifyVariable(method = "updateFluidInteraction", at = @At("STORE"), ordinal = 1)
    private boolean cb$lava(boolean inLava) {
        boolean fast = (Boolean) level().environmentAttributes().getDimensionValue(EnvironmentAttributes.FAST_LAVA);
        return ((CbWallLegacyCalls.EntityFluidLegacy) this).cb$vanillaPath(FluidTags.LAVA, fast ? 0.007 : 0.0023333333333333335, inLava);
    }

    /**
     * a tag 26.1 does not track: the height 1.21.11's measurement last recorded for it, where 26.1 answered 0 (an
     * answer a 26.1 mod made otherwise stands)
     */
    @Inject(method = "getFluidHeight", at = @At("RETURN"), cancellable = true)
    private void cb$legacyHeight(TagKey<Fluid> tag, CallbackInfoReturnable<Double> cir) {
        Object2DoubleMap<TagKey<Fluid>> heights = ((CbWallLegacyCalls.EntityFluidLegacy) this).cb$legacyFluidHeightsIfAny();
        if (heights != null && !CbWallEntityFluids.tracked(tag) && cir.getReturnValueD() == 0.0 && heights.containsKey(tag)) {
            cir.setReturnValue(heights.getDouble(tag));
        }
    }
}
