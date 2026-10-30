package top.dext.centurybridge.step.v1_20_5.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.dext.centurybridge.step.v1_20_5.rt.CbRecipeSync;

/**
 * RecipeHolder.STREAM_CODEC (class_8786.field_48357, built in its static initializer as composite(ResourceLocation
 * .STREAM_CODEC, id, Recipe.STREAM_CODEC, value, RecipeHolder::new): the id comes first on the wire, javap 1.20.5): the
 * codec stored is one whose decode peeks the id (on a duplicate of the buffer; nothing consumed) and runs the codec it
 * wraps with that id published as the recipe being decoded (rt/CbRecipeSync), so a 1.20.1 serializer's read(id, buf)
 * has it as on 1.20.4. Decode and encode are the wrapped codec's own -- a native mod's change to the same codec keeps
 * working, whichever wraps the other; a buffer whose first field is no id (another mod's wire form) publishes none.
 */
@Mixin(net.minecraft.class_8786.class)
public abstract class CbRecipeHolderCodecRoute {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9139;method_56435(Lnet/minecraft/class_9139;Ljava/util/function/Function;Lnet/minecraft/class_9139;Ljava/util/function/Function;Ljava/util/function/BiFunction;)Lnet/minecraft/class_9139;"))
    private static net.minecraft.class_9139<Object, Object> cb$publishRecipeId(net.minecraft.class_9139<Object, Object> codec) {
        return CbRecipeSync.publishingId(codec);
    }
}
