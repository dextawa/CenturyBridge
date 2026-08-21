package top.dext.centurybridge.shims.v1_21_4;

import java.util.Optional;
import net.minecraft.class_4970;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Re-derivable materialized fields on AbstractBlock (see CbPending). */
@Mixin(class_4970.class)
public interface Class4970Accessor {

    @Accessor("field_23156")
    @Mutable
    void cbSetLootTableKey(Optional<class_5321<class_52>> key);

    @Accessor("field_54005")
    @Mutable
    void cbSetTranslationKey(String key);
}
