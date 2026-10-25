package top.dext.centurybridge.shims.v1_20_2;

import com.mojang.serialization.Codec;
import net.minecraft.class_1863;
import net.minecraft.class_1865;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbRecipeJson;

/**
 * RecipeManager.fromJson(id, json): 1.20.2 decodes the recipe with its serializer's codec(); a vanilla serializer
 * with its 1.20.1 read(id, json) back (CbRecipeJson*) decodes through that member instead, as 1.20.1's
 * RecipeManager.fromJson called serializer.fromJson(id, json) -- so a 1.20.1 mod's mixin on the reader changes what
 * vanilla loads. CbRecipeJson.vanillaCodec says when the route is off (a subclass's own codec, another mod's mixin
 * on the serializer, -Dcenturybridge.legacyJsonRoute=false); the serializer's codec is used then.
 */
@Mixin(class_1863.class)
public abstract class CbRecipeJsonVanillaMixin {

    @Redirect(method = "method_17720", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1865;method_53736()Lcom/mojang/serialization/Codec;"), require = 1)
    private static Codec<?> cb$legacyReader(class_1865<?> serializer) {
        return CbRecipeJson.vanillaCodec(serializer);
    }
}
