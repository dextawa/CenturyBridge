package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.11 SpecialModelRenderers.<clinit> (class_10517) at its read of Blocks.CHEST (class_2246.field_10034), the chest's
 * entry of the static block mapping: 26.1 builds that mapping in BuiltInBlockModels.addDefaults (mixinAnchorMoves)
 */
@Mixin(net.minecraft.class_10517.class)
public abstract class SpecialBlockMappingMixin {
    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/class_2246;field_10034:Lnet/minecraft/class_2248;"),
            remap = false)
    private static void cbswallc$chestMapping(CallbackInfo ci) {
        SwallClient.hit("specialModels.blockMapping");
    }
}
