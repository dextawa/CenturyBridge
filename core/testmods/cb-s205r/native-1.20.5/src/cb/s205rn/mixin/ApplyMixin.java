package cb.s205rn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** RecipeManager.apply: the entry key and Recipe.CODEC.parse, the two sites CbRecipeApplyIdRoute (1.20.5 layer) takes */
@Mixin(net.minecraft.class_1863.class)
public abstract class ApplyMixin {
    @ModifyExpressionValue(method = "method_20705", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getKey()Ljava/lang/Object;"), remap = false)
    private Object cbs205rn$key(Object key) {
        cb.s205rn.Driver.keys++;
        return key;
    }

    @WrapOperation(method = "method_20705", at = @At(value = "INVOKE",
        target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"), remap = false)
    private com.mojang.serialization.DataResult<?> cbs205rn$parse(com.mojang.serialization.Codec<?> codec, com.mojang.serialization.DynamicOps<?> ops,
            Object input, Operation<com.mojang.serialization.DataResult<?>> original) {
        cb.s205rn.Driver.parses++;
        return original.call(codec, ops, input);
    }
}
