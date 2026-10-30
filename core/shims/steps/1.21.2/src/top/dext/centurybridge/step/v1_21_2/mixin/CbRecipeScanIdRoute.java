package top.dext.centurybridge.step.v1_21_2.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_21_2.rt.CbRecipeSync;

/**
 * SimpleJsonResourceReloadListener.scanDirectory(resources, dir, ops, codec, out) (class_4309.method_51148), where 1.21.2
 * decodes each datapack recipe (and every other file of a scanned directory): per file the id FileToIdConverter.fileToId
 * made (method_45115, one call) is noted, and codec.parse(ops, json) (one call) runs with it published as the recipe being
 * decoded (rt/CbRecipeSync), as 1.21.1's RecipeManager.fromJson(id, ..) had it for the 1.20.2 layer. Neither changes a
 * value or skips a call: the id passes unchanged, the parse is the wrapped operation's (a native mod's wrapper of the same
 * call keeps running); a codec that does not ask ignores the id.
 */
@Mixin(net.minecraft.class_4309.class)
public abstract class CbRecipeScanIdRoute {

    @ModifyExpressionValue(method = "method_51148", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_7654;method_45115(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2960;"))
    private static net.minecraft.class_2960 cb$v1_21_2$noteId(net.minecraft.class_2960 id) {
        CbRecipeSync.SCANNED.set(id);
        return id;
    }

    @WrapOperation(method = "method_51148", at = @At(value = "INVOKE",
        target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"))
    private static com.mojang.serialization.DataResult<?> cb$v1_21_2$publishId(com.mojang.serialization.Codec<?> codec,
            com.mojang.serialization.DynamicOps<?> ops, Object input, Operation<com.mojang.serialization.DataResult<?>> original) {
        net.minecraft.class_2960 id = CbRecipeSync.SCANNED.get();
        CbRecipeSync.SCANNED.remove();
        return id == null ? original.call(codec, ops, input) : CbRecipeSync.decoding(id, () -> original.call(codec, ops, input));
    }
}
