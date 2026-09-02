package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_3857;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_21_4.CbDefaults;

/** The 1.20.1 ThrownItemEntity ctors without an ItemStack: the sentinel the bridges pass becomes new ItemStack(getDefaultItem()). */
@Mixin(class_3857.class)
public abstract class ThrownItemEntityBridge {

    @Shadow
    public abstract void method_16940(class_1799 stack);

    @Shadow
    protected abstract class_1792 method_16942();

    @Inject(method = "<init>(Lnet/minecraft/class_1299;Lnet/minecraft/class_1309;Lnet/minecraft/class_1937;Lnet/minecraft/class_1799;)V", at = @At("TAIL"))
    private void cb$defaultItemOwner(class_1299<?> type, class_1309 owner, class_1937 world, class_1799 stack, CallbackInfo ci) {
        if (stack == CbDefaults.THROWN_DEFAULT_ITEM) {
            method_16940(new class_1799(method_16942()));
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/class_1299;DDDLnet/minecraft/class_1937;Lnet/minecraft/class_1799;)V", at = @At("TAIL"))
    private void cb$defaultItemPos(class_1299<?> type, double x, double y, double z, class_1937 world, class_1799 stack, CallbackInfo ci) {
        if (stack == CbDefaults.THROWN_DEFAULT_ITEM) {
            method_16940(new class_1799(method_16942()));
        }
    }
}
