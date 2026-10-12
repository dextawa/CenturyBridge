package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockContainers;

/**
 * BannerBlockEntity's name from an item's CUSTOM_NAME (applyImplicitComponents): for a mod banner
 * class overriding 1.20.4's setCustomName, the name goes through the override (CbBlockContainers).
 */
@Mixin(net.minecraft.class_2573.class)
public abstract class CbBlockBannerNameMixin implements CbBlockContainers.Named {

    @Shadow
    private net.minecraft.class_2561 field_11772;

    @Override
    public void cb$setName(net.minecraft.class_2561 name) {
        this.field_11772 = name;
    }

    @Redirect(method = "method_57568", at = @At(value = "FIELD", target = "Lnet/minecraft/class_2573;field_11772:Lnet/minecraft/class_2561;",
        opcode = org.objectweb.asm.Opcodes.PUTFIELD))
    private void cb$legacyCustomName(net.minecraft.class_2573 self, net.minecraft.class_2561 name) {
        if (name != null && self instanceof CbBlockContainers.LegacyBannerName legacy) {
            legacy.method_16842(name);
        } else {
            this.field_11772 = name;
        }
    }
}
