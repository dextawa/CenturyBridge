package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Optional;
import net.minecraft.class_1299;
import net.minecraft.class_2561;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EntityType's key-derived fields, remade with the real key of a type built for 1.21.1's build(String) (CbEntityTypes), and
 * the loot table EntityType's own getDefaultLootTable answers (CbEntityLoot). */
@Mixin(class_1299.class)
public interface CbEntityTypeAccess {

    @Mutable
    @Accessor("field_6106")
    void cb$setDescriptionId(String id);

    @Mutable
    @Accessor("field_16526")
    void cb$setLootTable(Optional<class_5321<class_52>> lootTable);

    @Accessor("field_16526")
    Optional<class_5321<class_52>> cb$lootTable();

    @Accessor("field_6092")
    void cb$setDescription(class_2561 description);
}
