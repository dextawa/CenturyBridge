package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockContainers;

/**
 * BaseContainerBlockEntity's name from an item's CUSTOM_NAME (applyImplicitComponents): for a mod
 * container class overriding 1.20.4's setCustomName, the name goes through the override, as the
 * custom-named placement's setCustomName did in 1.20.4 (CbBlockContainers).
 */
@Mixin(net.minecraft.class_2624.class)
public abstract class CbBlockContainerNameMixin implements CbBlockContainers.Named {

    @Shadow
    private net.minecraft.class_2561 field_17376;

    @Override
    public void cb$setName(net.minecraft.class_2561 name) {
        this.field_17376 = name;
    }

    @Redirect(method = "method_57568", at = @At(value = "FIELD", target = "Lnet/minecraft/class_2624;field_17376:Lnet/minecraft/class_2561;",
        opcode = org.objectweb.asm.Opcodes.PUTFIELD))
    private void cb$legacyCustomName(net.minecraft.class_2624 self, net.minecraft.class_2561 name) {
        if (name != null && self instanceof CbBlockContainers.LegacyCustomName legacy) {
            legacy.method_17488(name);
        } else {
            this.field_17376 = name;
        }
    }
}
