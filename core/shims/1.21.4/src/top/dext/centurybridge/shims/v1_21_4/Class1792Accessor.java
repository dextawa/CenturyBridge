package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1792;
import net.minecraft.class_9323;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Re-derivable materialized fields on Item (see CbPending). */
@Mixin(class_1792.class)
public interface Class1792Accessor {

    @Accessor("field_8014")
    @Mutable
    void cbSetTranslationKey(String key);

    @Accessor("field_49263")
    @Mutable
    void cbSetComponents(class_9323 components);
}
