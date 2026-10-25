package cb.clienttest.mixin;

import cb.clienttest.ModelProbe;
import java.util.function.Function;
import net.minecraft.class_1059;
import net.minecraft.class_1087;
import net.minecraft.class_2960;
import net.minecraft.class_3665;
import net.minecraft.class_4730;
import net.minecraft.class_7775;
import net.minecraft.class_793;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.1 mod's mixins on BlockModel (smx4 client checks 1b/1c, as 1.20.1 has them): getMaterial HEAD answers
 * minecraft:block/diamond_block for every texture of the stone models, so stone renders with the diamond block texture;
 * bake(ModelBaker, BlockModel, Function, ModelState, ResourceLocation, boolean) RETURN counts the models baked.
 */
@Mixin(class_793.class)
public abstract class BlockModelMixin {
    @Inject(method = "method_24077(Ljava/lang/String;)Lnet/minecraft/class_4730;", at = @At("HEAD"), cancellable = true)
    private void cbClientTest$material(String name, CallbackInfoReturnable<class_4730> cir) {
        if (ModelProbe.isStone(((class_793) (Object) this).field_4252)) {
            ModelProbe.stoneMaterials++;
            cir.setReturnValue(new class_4730(class_1059.field_5275, new class_2960("block/diamond_block")));
        }
    }

    @Inject(method = "method_3446(Lnet/minecraft/class_7775;Lnet/minecraft/class_793;Ljava/util/function/Function;Lnet/minecraft/class_3665;Lnet/minecraft/class_2960;Z)Lnet/minecraft/class_1087;",
        at = @At("RETURN"))
    private void cbClientTest$baked(class_7775 baker, class_793 model, Function<?, ?> sprites, class_3665 state,
            class_2960 id, boolean gui3d, CallbackInfoReturnable<class_1087> cir) {
        ModelProbe.bakes++;
    }
}
