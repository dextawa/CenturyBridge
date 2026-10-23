package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.resource.conditions.CbLegacyConditions;

/**
 * Fabric API 1.20.4's LOADED_TAGS for the 1.20.4 resource conditions this layer gives back: the tags
 * TagManagerLoader read in this reload, with their entries (1.20.4's tags_populated ids pass only for a
 * tag with at least one), captured where 1.20.4's TagManagerLoaderMixin captured them -- and where 1.20.5's
 * still captures its own, which keeps only the tags' ids.
 */
@Mixin(net.minecraft.class_3505.class)
public abstract class CbFapiConditionTagsMixin {

    @Shadow
    private List<net.minecraft.class_3505.class_6863<?>> field_36396;

    @Dynamic("the lambda TagManagerLoader's reload applies the read tags in (thenAcceptAsync)")
    @Inject(method = "method_40098(Ljava/util/List;Ljava/lang/Void;)V", at = @At("RETURN"))
    private void cb$conditionTags(List<?> list, Void unused, CallbackInfo ci) {
        CbLegacyConditions.setTags(field_36396);
    }
}
