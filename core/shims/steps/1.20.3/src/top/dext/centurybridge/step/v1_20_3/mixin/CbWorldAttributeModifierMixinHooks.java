package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1322;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldAttributeModifierName;

/**
 * The injectors into 1.20.3's own AttributeModifier methods that go with {@link CbWorldAttributeModifierMixin} (its
 * restored 1.20.2 getName stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.2 injectors can anchor inside
 * it): save() and the codec ask getName() as 1.20.2's save did; toString() reads the supplier directly.
 */
@Mixin(class_1322.class)
public abstract class CbWorldAttributeModifierMixinHooks {

    @Redirect(method = "method_26860", at = @At(value = "FIELD", target = "Lnet/minecraft/class_1322;field_46248:Ljava/lang/String;"))
    private String cb$saveName(class_1322 self) {
        return ((CbWorldAttributeModifierName) (Object) this).method_6185();
    }

    @Redirect(method = "toString", at = @At(value = "FIELD", target = "Lnet/minecraft/class_1322;field_46248:Ljava/lang/String;"))
    private String cb$toStringName(class_1322 self) {
        return ((CbPrioritySplit.AttributeModifierName) (Object) this).cb$v1_20_3$name();
    }

    @Redirect(method = "method_53965", at = @At(value = "FIELD", target = "Lnet/minecraft/class_1322;field_46248:Ljava/lang/String;"))
    private static String cb$codecName(class_1322 modifier) {
        return ((CbWorldAttributeModifierName) (Object) modifier).method_6185();
    }
}
