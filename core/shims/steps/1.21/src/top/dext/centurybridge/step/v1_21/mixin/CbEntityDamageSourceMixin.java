package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * DamageSource.isIndirect(), which 1.21 replaced with its negation isDirect(): given back (covers in
 * s6fin-entity.json) with 1.20.6's body, so a 1.20.6 mod's calls and super calls link; a 1.20.6 override answers
 * 1.21's isDirect() negated (overrideHooks), as the Warden asked isIndirect() in 1.20.6 and isDirect() in 1.21.
 */
@Mixin(value = net.minecraft.class_1282.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityDamageSourceMixin {

    @Shadow
    @Final
    private class_1297 field_42292;

    @Shadow
    @Final
    private class_1297 field_42293;

    /** 1.20.6 isIndirect(): the causing entity is not the direct one */
    public boolean method_48790() {
        return this.field_42292 != this.field_42293;
    }
}
