package top.dext.centurybridge.step.v1_20_5.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_20_5.rt.CbRecipeSync;

/**
 * RecipeManager.apply(recipes, resources, profiler) (class_1863.method_20705): 1.20.4 decoded each datapack recipe in
 * fromJson(id, json) (method_17720), where the 1.20.2 layer's RecipeLoadIdBridge took the id; 1.20.5 decodes it inline --
 * the entry's key cast to the id, then Recipe.CODEC.parse(registryOps, json) (one call each, javap 1.20.4 / 1.20.5; the
 * same through 1.21.1) -- and no longer calls fromJson. Per entry the key is noted and the parse runs with it published as
 * the recipe being decoded (rt/CbRecipeSync), so a 1.20.1 serializer's read(id, json) has it as on 1.20.4. Neither
 * changes a value or skips a call (the parse is the wrapped operation's: a native mod's wrapper of it keeps running).
 */
@Mixin(net.minecraft.class_1863.class)
public abstract class CbRecipeApplyIdRoute {

    @ModifyExpressionValue(method = "method_20705", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getKey()Ljava/lang/Object;"))
    private Object cb$noteId(Object key) {
        CbRecipeSync.SCANNED.set(key instanceof net.minecraft.class_2960 id ? id : null);
        return key;
    }

    @WrapOperation(method = "method_20705", at = @At(value = "INVOKE",
        target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"))
    private com.mojang.serialization.DataResult<?> cb$publishId(com.mojang.serialization.Codec<?> codec,
            com.mojang.serialization.DynamicOps<?> ops, Object input, Operation<com.mojang.serialization.DataResult<?>> original) {
        net.minecraft.class_2960 id = CbRecipeSync.SCANNED.get();
        CbRecipeSync.SCANNED.remove();
        return id == null ? original.call(codec, ops, input) : CbRecipeSync.decoding(id, () -> original.call(codec, ops, input));
    }
}
