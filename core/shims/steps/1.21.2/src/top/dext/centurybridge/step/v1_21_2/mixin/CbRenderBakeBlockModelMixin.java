package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.1 BlockModel.getDependencies(): its overrides' models and its parent (class_793.method_4755), on 1.21.2's fields.
 * 1.21.1 BlockModel.bake(baker, spriteGetter, state) was bake(baker, this, spriteGetter, state, true); a 1.21.1 subclass
 * overriding that five-argument bake (CbRenderBake.LegacyBake) is baked through it again, with the baker 1.21.2 passes.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_793.class)
public abstract class CbRenderBakeBlockModelMixin {

    @Shadow
    @Final
    private List<net.minecraft.class_799> field_4255;

    @Shadow
    protected net.minecraft.class_2960 field_4247;

    /** 1.21.1 BlockModel.getDependencies() */
    public Collection<net.minecraft.class_2960> method_4755() {
        Set<net.minecraft.class_2960> ids = new HashSet<>();
        for (net.minecraft.class_799 o : this.field_4255) {
            ids.add(o.comp_3193());
        }
        if (this.field_4247 != null) {
            ids.add(this.field_4247);
        }
        return ids;
    }

    @org.spongepowered.asm.mixin.injection.Inject(
        method = "method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;",
        at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    private void cb$legacyBake(net.minecraft.class_7775 baker, java.util.function.Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
                               net.minecraft.class_3665 state,
                               org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.class_1087> cir) {
        if (this instanceof top.dext.centurybridge.step.v1_21_2.rt.CbRenderBake.LegacyBake legacy) {
            cir.setReturnValue(legacy.method_3446(baker, (net.minecraft.class_793) (Object) this, sprites, state, true));
        }
    }
}
