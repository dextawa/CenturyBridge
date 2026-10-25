package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_5131;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbEffectAttributeRoute;

/**
 * Where 1.20.1's living entity applied and removed an effect's attributes through
 * addAttributeModifiers(entity, attributes, amplifier) / removeAttributeModifiers(entity,
 * attributes, amplifier) -- onEffectAdded, onEffectUpdated, onEffectRemoved -- 1.20.2 calls
 * add(attributes, amplifier) / remove(attributes). Those calls go through CbEffectAttributeRoute:
 * 1.20.2's call still runs (a 1.20.2-era mod's mixin on it acts), the restored 1.20.1 members are
 * consulted beside it and change the attributes only where a converted mod's mixin changed what they
 * do; an effect whose 1.20.1 behaviour differs (CbLegacyMobEffectOverride) gets the 1.20.1 calls,
 * with the entity and the instance's amplifier.
 */
@Mixin(class_1309.class)
public abstract class CbLivingEffectAttributes {

    @Redirect(method = {"method_6020", "method_6009"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1291;method_5555(Lnet/minecraft/class_5131;I)V"))
    private void cb$addAttributes(class_1291 effect, class_5131 attributes, int amplifier) {
        CbEffectAttributeRoute.add(effect, (class_1309) (Object) this, attributes, amplifier);
    }

    @Redirect(method = "method_6009", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1291;method_5562(Lnet/minecraft/class_5131;)V"))
    private void cb$removeAttributesOnUpdate(class_1291 effect, class_5131 attributes,
                                             class_1293 instance, boolean reapply, class_1297 source) {
        CbEffectAttributeRoute.remove(effect, (class_1309) (Object) this, attributes, instance.method_5578());
    }

    @Redirect(method = "method_6129", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1291;method_5562(Lnet/minecraft/class_5131;)V"))
    private void cb$removeAttributesOnRemoval(class_1291 effect, class_5131 attributes, class_1293 instance) {
        CbEffectAttributeRoute.remove(effect, (class_1309) (Object) this, attributes, instance.method_5578());
    }
}
