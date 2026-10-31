package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_4588;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.11: VertexConsumer.addVertex(Matrix4fc, x, y, z)'s head (the default every implementor without its own runs) */
@Mixin(class_4588.class)
public interface NativeVertexMixin {
    @Inject(method = "method_22918(Lorg/joml/Matrix4fc;FFF)Lnet/minecraft/class_4588;", at = @At("HEAD"))
    private void cbSmx1dn$addVertex(Matrix4fc matrix, float x, float y, float z, CallbackInfoReturnable<class_4588> cir) {
        Probe.bumpRender("addVertex");
    }
}
