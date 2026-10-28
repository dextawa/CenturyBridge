package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityMisc;

/**
 * The fields of CbEntityMiscMixin that restore V's fields on the target, at the layer's CbVanillaReads.PRIORITY (mixin_priority.py
 * rule 3: below every earlier layer's mixin, so a climbing earlier layer's @Shadow of them resolves); CbEntityMiscMixin
 * keeps its injectors at the default priority and shadows these.
 */
@org.spongepowered.asm.mixin.Mixin(value = class_1297.class, priority = top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads.PRIORITY)
public abstract class CbEntityMiscMixinFields {

    public float field_5973;

    public float field_6039;

    public boolean field_5985;
}
