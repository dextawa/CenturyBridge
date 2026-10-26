package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbGuiLegacyTextures;

/**
 * The texture manager makes a plain file texture (ResourceTexture) for every id it has not seen --
 * getTexture and bindTexture construct it, register it and hand back / bind that object. For an id
 * that names a 1.20.1 GUI texture 1.20.2 no longer ships whole, the rebuilt composite is constructed
 * in its place (see CbGuiLegacyTextures), so the first draw already uses it. A plain ResourceTexture a
 * mod registers under such an id itself is swapped the same way; any other texture a mod registers
 * there is left alone.
 */
@Mixin(net.minecraft.class_1060.class)
public abstract class CbGuiTextureManagerBridge {

    // NEW's target is the constructor's descriptor with the type as its return (Mixin's BeforeNew reads the return
    // type of a method descriptor: "Lclass_1049;<init>(..)V" named type "V" and matched nothing)
    @Redirect(method = {"method_4619", "method_4618"},
              at = @At(value = "NEW", target = "(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1049;"))
    private net.minecraft.class_1049 cb$fileTexture(net.minecraft.class_2960 id) {
        return CbGuiLegacyTextures.fileTexture(id);
    }

    @Inject(method = "method_4616", at = @At("HEAD"), cancellable = true)
    private void cb$legacyGuiTexture(net.minecraft.class_2960 id, net.minecraft.class_1044 texture, CallbackInfo ci) {
        if (CbGuiLegacyTextures.replaces(id, texture)) {
            ((net.minecraft.class_1060) (Object) this).method_4616(id, CbGuiLegacyTextures.fileTexture(id));
            ci.cancel();
        }
    }
}
