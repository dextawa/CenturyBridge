package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_2835;
import net.minecraft.class_2841;
import net.minecraft.class_6563;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbPalettes;

/**
 * 1.21.8's PalettedContainer built its palettes with its registry and itself as their resize handler (javap -c 1.21.8
 * PalettedContainer.Configuration.createData: factory.create(bits, registry, container, values)). 1.21.9's builds them
 * with neither; this gives each palette it builds the two back (rt/CbPalettes): its strategy's registry and the
 * container, at the return of every constructor and of createOrReuseData, the one place a container replaces its
 * palette. Additive RETURN injectors: what 1.21.9 builds and every mod's mixin on it are untouched.
 */
@Mixin(class_2841.class)
public abstract class CbPalettedContainerTagMixin<T> {

    @Shadow
    private volatile class_2841.class_6561<T> field_34560;

    @Shadow
    @org.spongepowered.asm.mixin.Final
    private class_6563<T> field_34561;

    @Inject(method = {"<init>(Ljava/lang/Object;Lnet/minecraft/class_6563;)V",
        "<init>(Lnet/minecraft/class_6563;Lnet/minecraft/class_11895;Lnet/minecraft/class_6490;Lnet/minecraft/class_2837;)V",
        "<init>(Lnet/minecraft/class_2841;)V"}, at = @At("RETURN"))
    private void cb$tagBuilt(CallbackInfo ci) {
        this.cb$tag(this.field_34560);
    }

    @Inject(method = "method_38297(Lnet/minecraft/class_2841$class_6561;I)Lnet/minecraft/class_2841$class_6561;", at = @At("RETURN"))
    private void cb$tagResized(class_2841.class_6561<T> old, int bits, CallbackInfoReturnable<class_2841.class_6561<T>> cir) {
        this.cb$tag(cir.getReturnValue());
    }

    private void cb$tag(class_2841.class_6561<T> data) {
        if (data != null) {
            CbPalettes.tag(data.comp_119(), this.field_34561.method_74163(), (class_2835<?>) (Object) this);
        }
    }
}
