package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Re-derivable materialized fields on Item (see CbPending). */
@Mixin(net.minecraft.world.item.Item.class)
public interface Class1792Accessor {

    @Accessor("descriptionId")
    @Mutable
    void cbSetTranslationKey(String key);

    @Accessor("components")
    @Mutable
    void cbSetComponents(net.minecraft.core.component.DataComponentMap components);
}
