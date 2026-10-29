package top.dext.centurybridge.step.v1_21_2.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21_2.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestPostEffect;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * GameRenderer.checkEntityPostEffect(entity) (class_757.method_3167), the vanilla caller of 1.21.1's loadEffect: 1.21.1
 * called loadEffect(&lt;ns&gt;:shaders/post/creeper.json / spider.json / invert.json) for a creeper / spider / enderman
 * camera, 1.21.2 calls setPostEffect(&lt;ns&gt;:creeper / spider / invert) at the same points (javap both). When a converted
 * mod mixes the restored loadEffect (CbMixedMembers), the call runs it with 1.21.1's location (CbRestPostEffect.location),
 * which makes the effect active through the same setPostEffect when the ShaderManager has it; unmixed, 1.21.2's call runs
 * untouched. The route wraps the call (MixinExtras @WrapOperation) at CbVanillaReads.ROUTE_FIRST, the innermost wrapper,
 * so a native 1.21.2 mod's @WrapOperation of the same call wraps it and keeps running either way, and a native mod's
 * injector on setPostEffect runs inside the restored body.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_757.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbRestPostEffectRouteMixin {

    private static final String LOAD_EFFECT = "net/minecraft/class_757.method_3168(Lnet/minecraft/class_2960;)V";

    @WrapOperation(method = "method_3167(Lnet/minecraft/class_1297;)V", require = 3, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_757;method_62904(Lnet/minecraft/class_2960;)V"))
    private void cb$v1_21_2$loadEffect(net.minecraft.class_757 renderer, net.minecraft.class_2960 id, Operation<Void> original) {
        if (CbMixedMembers.mixed(LOAD_EFFECT)) {
            ((CbRestPostEffect.Game) renderer).cb$v1_21_2$loadEffect(CbRestPostEffect.location(id));
            return;
        }
        original.call(renderer, id);
    }
}
