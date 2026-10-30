package cb.s205rn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * SimpleJsonResourceReloadListener.scanDirectory: fileToId and Codec.parse, the two sites CbRecipeScanIdRoute (1.21.2
 * layer) takes. Every scanned directory counts, so keys == parses is the check (recipes &lt;= parses).
 */
@Mixin(net.minecraft.class_4309.class)
public abstract class ApplyMixin {
    @ModifyExpressionValue(method = "method_51148", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_7654;method_45115(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2960;"), remap = false)
    private static net.minecraft.class_2960 cbs205rn$key(net.minecraft.class_2960 id) {
        cb.s205rn.Driver.keys++;
        return id;
    }

    @WrapOperation(method = "method_51148", at = @At(value = "INVOKE",
        target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"), remap = false)
    private static com.mojang.serialization.DataResult<?> cbs205rn$parse(com.mojang.serialization.Codec<?> codec, com.mojang.serialization.DynamicOps<?> ops,
            Object input, Operation<com.mojang.serialization.DataResult<?>> original) {
        cb.s205rn.Driver.parses++;
        return original.call(codec, ops, input);
    }
}
