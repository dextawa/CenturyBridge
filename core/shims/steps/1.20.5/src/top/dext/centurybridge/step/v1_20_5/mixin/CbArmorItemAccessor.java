package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ArmorItem's own attribute modifiers (1.20.5 keeps them in a supplier), for a 1.20.4 subclass's super call (rt/CbTools) */
@Mixin(net.minecraft.class_1738.class)
public interface CbArmorItemAccessor {

    /** defaultModifiers */
    @Accessor("field_23741")
    java.util.function.Supplier<net.minecraft.class_9285> cbS4$defaultModifiers();
}
