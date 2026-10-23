package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_3857;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ThrowableItemProjectile's protected getDefaultItem, for the item 1.21.1's constructors left in place (CbEntityProjectiles). */
@Mixin(class_3857.class)
public interface CbEntityProjectileInvoker {

    @Invoker("method_16942")
    class_1792 cb$getDefaultItem();
}
