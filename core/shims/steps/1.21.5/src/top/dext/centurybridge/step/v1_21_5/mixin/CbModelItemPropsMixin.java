package top.dext.centurybridge.step.v1_21_5.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.rt.CbBakedModels;

/**
 * SimpleModelWrapper.bake(baker, id, state) (class_10801.method_67931): the one place 1.21.5 turns a resolved model into
 * a block model part (Variant.bake calls it; javap 1.21.5..1.21.11). 1.21.4 baked the same resolved model into a
 * SimpleBakedModel carrying isGui3d / usesBlockLight / transforms; 1.21.5 keeps those only for item models
 * (ModelRenderProperties). They are read here off the ResolvedModel bake itself looked up (baker.getModel(id)) and
 * recorded for the part it returns, so 1.21.4's calls on the block model answer them (CbBakedModels). The bake runs as
 * 1.21.5 has it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10801.class)
public abstract class CbModelItemPropsMixin {

    @ModifyExpressionValue(method = "method_67931", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_7775;method_45872(Lnet/minecraft/class_2960;)Lnet/minecraft/class_10819;"))
    private static net.minecraft.class_10819 cb$resolved(net.minecraft.class_10819 resolved) {
        CbBakedModels.beginBake(resolved);
        return resolved;
    }

    @Inject(method = "method_67931", at = @At("RETURN"))
    private static void cb$part(net.minecraft.class_7775 baker, net.minecraft.class_2960 id, net.minecraft.class_3665 state,
                                CallbackInfoReturnable<Object> cir) {
        CbBakedModels.endBake(cir.getReturnValue());
    }
}
