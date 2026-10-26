package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.4 FriendlyByteBuf.writeGameProfile / readGameProfile and writeProperty / readProperty: counted */
@Mixin(net.minecraft.class_2540.class)
public abstract class BufProfileMixin {
    @Inject(method = "method_43616(Lcom/mojang/authlib/GameProfile;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4b$writeProfile(com.mojang.authlib.GameProfile profile, CallbackInfo ci) {
        Driver.writeProfileAsked++;
    }

    @Inject(method = "method_43623()Lcom/mojang/authlib/GameProfile;", at = @At("HEAD"), remap = false)
    private void cbsmx4b$readProfile(CallbackInfoReturnable<com.mojang.authlib.GameProfile> cir) {
        Driver.readProfileAsked++;
    }

    @Inject(method = "method_43617(Lcom/mojang/authlib/properties/Property;)V", at = @At("HEAD"), remap = false)
    private void cbsmx4b$writeProperty(com.mojang.authlib.properties.Property property, CallbackInfo ci) {
        Driver.writePropertyAsked++;
    }

    @Inject(method = "method_43624()Lcom/mojang/authlib/properties/Property;", at = @At("HEAD"), remap = false)
    private void cbsmx4b$readProperty(CallbackInfoReturnable<com.mojang.authlib.properties.Property> cir) {
        Driver.readPropertyAsked++;
    }
}
