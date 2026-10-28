package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.function.Function;
import net.minecraft.class_11677;
import net.minecraft.class_572;
import net.minecraft.class_5605;
import net.minecraft.class_5609;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbArmorLayers;

/**
 * Where 1.21.9's HumanoidModel.createArmorMeshSet(meshOf, inner, outer) returns, has rt/CbArmorLayers build the whole
 * armor mesh at both deformations beside the set it cut to slots (1.21.8's inner / outer armor layers). Every vanilla
 * armor set is built here (javap -c 1.21.9: the only callers of method_72853 are HumanoidModel.createArmorMeshSet,
 * ArmorStandArmorModel and ZombieVillagerModel; piglins go through HumanoidModel's). Additive RETURN injector.
 */
@Mixin(class_572.class)
public abstract class CbArmorMeshSetMixin {

    @Inject(method = "method_72853(Ljava/util/function/Function;Lnet/minecraft/class_5605;Lnet/minecraft/class_5605;)Lnet/minecraft/class_11677;",
        at = @At("RETURN"))
    private static void cb$wholeArmorMesh(Function<class_5605, class_5609> meshOf, class_5605 inner, class_5605 outer,
                                          CallbackInfoReturnable<class_11677<class_5609>> cir) {
        CbArmorLayers.built(cir.getReturnValue(), meshOf, inner, outer);
    }
}
