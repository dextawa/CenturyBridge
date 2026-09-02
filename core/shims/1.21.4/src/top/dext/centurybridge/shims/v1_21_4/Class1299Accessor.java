package top.dext.centurybridge.shims.v1_21_4;

import java.util.Optional;
import net.minecraft.class_1299;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Re-derivable key-materialized fields on EntityType (see CbPending: the entity half of the pending-key repair). */
@Mixin(class_1299.class)
public interface Class1299Accessor {

    @Accessor("field_6106")
    @Mutable
    void cbSetTranslationKey(String key);

    @Accessor("field_16526")
    @Mutable
    void cbSetLootTableKey(Optional<class_5321<class_52>> key);
}
