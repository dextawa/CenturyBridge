package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestPacks;

/**
 * The location an AbstractPackResources holds (its field): what 1.20.4's isBuiltin() returned the flag
 * of, read without a virtual location() call (CbRestPacks.superIsBuiltin, locationOfOwnBuiltin).
 */
@Mixin(net.minecraft.class_3255.class)
public abstract class CbRestAbstractPackResourcesMixin implements CbRestPacks.Located {

    @Shadow
    @Final
    private net.minecraft.class_9224 field_49031;

    @Override
    public net.minecraft.class_9224 cbRest$location() {
        return this.field_49031;
    }
}
