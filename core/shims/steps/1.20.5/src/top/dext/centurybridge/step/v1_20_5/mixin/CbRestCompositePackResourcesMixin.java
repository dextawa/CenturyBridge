package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestPacks;

/**
 * A CompositePackResources' location is its primary pack's (1.20.5's location()); 1.20.4's isBuiltin()
 * was the primary's too (CbRestPacks.superIsBuiltin, locationOfOwnBuiltin).
 */
@Mixin(net.minecraft.class_8614.class)
public abstract class CbRestCompositePackResourcesMixin implements CbRestPacks.Located {

    @Shadow
    @Final
    private net.minecraft.class_3262 field_45036;

    @Override
    public net.minecraft.class_9224 cbRest$location() {
        return this.field_45036.method_56926();
    }
}
