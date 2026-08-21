package top.dext.centurybridge.shims.v1_21_4;

import java.util.Optional;
import net.minecraft.class_4970;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_21_4.CbPending;

/**
 * Legacy construct-then-register order: the constructor's two key-derived
 * reads get a placeholder key first (see CbPending). Redirects rather than
 * an inject because constructors only accept injection at RETURN.
 */
@Mixin(class_4970.class)
public abstract class AbstractBlockCtorBridge {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4970$class_2251;method_63505()Ljava/util/Optional;"))
    private Optional<class_5321<class_52>> cb$lootKey(class_4970.class_2251 settings) {
        CbPending.ensureBlockKey(this, settings);
        return ((Class2251Accessor) settings).cbLootTableKey();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4970$class_2251;method_63506()Ljava/lang/String;"))
    private String cb$translationKey(class_4970.class_2251 settings) {
        CbPending.ensureBlockKey(this, settings);
        return ((Class2251Accessor) settings).cbTranslationKey();
    }
}
