package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBlockDefs;

/**
 * Every MultiPart 1.21.2 builds from parsed selectors (MultiPart.Definition.instantiate) keeps the definition and the
 * selectors it was built from: 1.21.1's MultiPart held both (see CbRenderBlockDefs).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_816.class_9982.class)
public abstract class CbRenderBlockDefInstantiateMixin {

    @Inject(method = "method_62339", at = @At("RETURN"))
    private void cb$attach(net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition,
                           CallbackInfoReturnable<net.minecraft.class_816> cir) {
        ((CbRenderBlockDefs.Part) cir.getReturnValue()).cb$attach(definition, ((net.minecraft.class_816.class_9982) (Object) this).comp_3052());
    }
}
