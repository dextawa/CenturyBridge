package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The fields of CbRestOptionsMixin that restore V's fields on the target, at the layer's CbVanillaReads.PRIORITY (mixin_priority.py
 * rule 3: below every earlier layer's mixin, so a climbing earlier layer's @Shadow of them resolves); CbRestOptionsMixin
 * keeps its injectors at the default priority and shadows these.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@org.spongepowered.asm.mixin.Mixin(value = class_315.class, priority = top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads.PRIORITY)
public abstract class CbRestOptionsMixinFields {

    public boolean field_28777 = false;
}
