package cb.sl6.mixin;

import cb.sl6.Count;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bindable: an implicit @Local Level at the TNT prime call of the flint-and-steel behaviour. 1.20.4's game jar declares
 * the local (slot 3) as Level, so it binds; 1.21's declares the same local (the same BlockSource.level() store) as
 * ServerLevel, where Mixin finds no Level local. The converter retypes the parameter to ServerLevel; the handler runs.
 */
@Mixin(targets = "net/minecraft/class_2357$10")
public abstract class ImplicitLevelMixin {
    @Inject(method = "method_10135", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2530;method_10738(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)V"))
    private void cbsl6$implicit(CallbackInfoReturnable<class_1799> cir, @Local class_1937 level) {
        Count.hit("bindImplicit");
        System.setProperty("cb.sl6.levelClass", level.getClass().getSimpleName());
    }
}
