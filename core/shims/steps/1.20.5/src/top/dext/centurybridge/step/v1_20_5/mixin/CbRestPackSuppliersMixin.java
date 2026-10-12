package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestPacks;

/**
 * 1.20.4 built pack suppliers with a builtin flag (FileResourcesSupplier(path, builtin),
 * PathResourcesSupplier(path, builtin)) and opened their packs with it; 1.20.5's suppliers take none and
 * open with the location they are handed. A supplier 1.20.4 code built as builtin opens with a BUILT_IN
 * location (CbRestPacks records the flag at construction).
 */
@Mixin({net.minecraft.class_3258.class_8615.class, net.minecraft.class_3259.class_8619.class})
public abstract class CbRestPackSuppliersMixin {

    @ModifyVariable(method = "method_52424", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_9224 cbRest$builtinPrimary(net.minecraft.class_9224 location) {
        return CbRestPacks.withSupplierFlag(this, location);
    }

    @ModifyVariable(method = "method_52425", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_9224 cbRest$builtinFull(net.minecraft.class_9224 location) {
        return CbRestPacks.withSupplierFlag(this, location);
    }
}
