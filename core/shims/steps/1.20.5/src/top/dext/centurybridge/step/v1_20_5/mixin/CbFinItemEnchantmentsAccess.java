package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ItemEnchantmentsPredicate: its enchantment predicates, for 1.20.4's ItemPredicate.enchantments() / storedEnchantments() */
@Mixin(net.minecraft.class_9356.class)
public interface CbFinItemEnchantmentsAccess {

    @Accessor("field_49799")
    java.util.List<net.minecraft.class_2035> cbS4$enchantments();
}
