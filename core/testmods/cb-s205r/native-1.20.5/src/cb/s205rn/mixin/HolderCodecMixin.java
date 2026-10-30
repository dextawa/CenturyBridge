package cb.s205rn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** RecipeHolder.STREAM_CODEC's composite in &lt;clinit&gt;, the value CbRecipeHolderCodecRoute (1.20.5 layer) also wraps */
@Mixin(net.minecraft.class_8786.class)
public abstract class HolderCodecMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_9139;method_56435(Lnet/minecraft/class_9139;Ljava/util/function/Function;Lnet/minecraft/class_9139;Ljava/util/function/Function;Ljava/util/function/BiFunction;)Lnet/minecraft/class_9139;"),
        remap = false)
    private static net.minecraft.class_9139<Object, Object> cbs205rn$count(net.minecraft.class_9139<Object, Object> codec) {
        return new net.minecraft.class_9139<>() {
            @Override
            public Object decode(Object buf) {
                cb.s205rn.Driver.codecDecodes++;
                return codec.decode(buf);
            }

            @Override
            public void encode(Object buf, Object value) {
                codec.encode(buf, value);
            }
        };
    }
}
